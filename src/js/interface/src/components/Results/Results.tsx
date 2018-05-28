import * as React from 'react'
import {
    CollectionView,
    CollectionViewDelegate,
    GridLayout,
    Insets,
    Size,
    Spacing,
    CollectionViewAnimationReason,
    CollectionViewAnimationPhase,
    Animation
} from 'collection-view'
import './Results.css'
import { Result, WikipediaPreview } from '../../types'
import { diff } from '../../diff'
import ResultComponent from '../Result/Result'
import * as ReactDOM from 'react-dom'
import { requestPreview } from '../../api'
import { Style } from 'collection-view/dist/declarations/utils'
import { Position } from 'collection-view/dist/declarations/types'

export interface Props {
    query: string
    results: Result[]
}

const animationDuration = 400

export default class Results
    extends React.Component<Props, {}>
    implements CollectionViewDelegate {

    private view?: CollectionView
    private results: Result[]
    private wrapper: HTMLDivElement | null
    // TODO: replace by cache
    // result URI -> wikipedia preview
    private previews = new Map<string, WikipediaPreview>()
    // TODO: replace by cache, can be larger than `previews`
    private urisWithoutPreviews = new Set<string>()
    private elementIndices = new WeakMap<Element, number>()

    private static renderResult(element: HTMLElement, result: Result, preview?: WikipediaPreview) {
        const component = (
            <ResultComponent
                uri={result.uri}
                imageURL={preview && preview.thumbnailURL || ''}
                label={result.label}
                description={preview && preview.description || ''}
                extractHTML={preview && preview.extractHTML || ''}
                wikipediaTitle={result.wikipediaTitle}
            />
        )

        ReactDOM.render(component, element)
    }

    private static getAnimationTimingFunction(reason: CollectionViewAnimationReason): string | undefined {
        switch (reason) {
            case CollectionViewAnimationReason.ELEMENT_ADDITION:
                return 'cubic-bezier(0.0, 0.0, 0.2, 1)'
            case CollectionViewAnimationReason.ELEMENT_REMOVAL:
                return 'cubic-bezier(0.4, 0.0, 1, 1)'
            case CollectionViewAnimationReason.ELEMENT_MOVE:
            case CollectionViewAnimationReason.LAYOUT_UPDATE:
                return 'cubic-bezier(0.4, 0.0, 0.2, 1)'
            default:
                return undefined
        }
    }

    constructor(props: Props) {
        super(props)

        this.results = props.results
    }

    getCount() {
        return this.results.length
    }

    configureElement(element: HTMLElement, index: number) {
        element.classList.add('ResultsItem')

        // keep track that element currently renders given index
        this.elementIndices.set(element, index)

        const result = this.results[index]

        const {uri} = result

        const preview = this.previews.get(uri)

        Results.renderResult(element, result, preview)

        // only fetch preview if it is not available
        if (preview) {
            return
        }

        const {wikipediaTitle} = result

        if (wikipediaTitle
            && !this.urisWithoutPreviews.has(uri)) {

            const [p] = requestPreview(wikipediaTitle)
            p.then(newPreview => {
                this.previews.set(uri, newPreview)
                // check if element still represents index
                const currentIndex = this.elementIndices.get(element)
                if (currentIndex !== index || !this.view) {
                    return
                }

                // re-render
                Results.renderResult(element, result, newPreview)

            }).catch(() => {
                this.urisWithoutPreviews.add(uri)
            })
        }
    }

    getAnimation(index: number, info: any, property: string, reason: CollectionViewAnimationReason): Animation {
        const timingFunction = Results.getAnimationTimingFunction(reason)
        return new Animation(animationDuration, 0, timingFunction)
    }

    getStyle(index: number, phase: CollectionViewAnimationPhase, info: any, position: Position): Style {
        const {x, y} = position
        switch (phase) {
            case CollectionViewAnimationPhase.ELEMENT_APPEARING:
                return {'transform': `translate3d(${x}px, ${y}px, -100px)`}
            case CollectionViewAnimationPhase.ELEMENT_APPEARED:
                return {'transform': `translate3d(${x}px, ${y}px, 0)`}
            case CollectionViewAnimationPhase.ELEMENT_DISAPPEARED:
                return {'transform': `translate3d(${x}px, ${y}px, 50px)`}
            default:
                return {}
        }
    }

    onScroll(collectionView: CollectionView) {
        this.updatePerspective()
    }

    invalidateElement(element: HTMLElement, index: number) {
        // keep track that element does not render given index anymore
        this.elementIndices.delete(element)
    }

    shouldComponentUpdate() {
        return false
    }

    componentWillReceiveProps(nextProps: Props) {
        if (nextProps.query === this.props.query
            && nextProps.results.length === this.props.results.length) {

            return
        }

        this.update(nextProps.results)
    }

    componentWillUnmount() {
        this.uninstallView()
    }

    render() {
        return (
            <div
                className="Results"
                ref={this.onWrapperRef}
            >
                <div
                    ref={this.onScrollRef}
                    style={{perspective: 800}}
                />
            </div>
        )
    }

    private onWrapperRef = (element: HTMLDivElement | null) => {
        this.wrapper = element
        this.updateWrapperClasses()
    }

    private onScrollRef = (element: HTMLDivElement | null) => {
        if (!element) {
            this.uninstallView()
            return
        }

        this.installView(element)
    }

    private installView(element: HTMLDivElement) {
        const inset = 16
        const layout = new GridLayout({
                                          insets: new Insets(inset, inset, inset, inset),
                                          itemSize: new Size(310, 346),
                                          spacing: new Spacing(24, 24)
                                      })
        this.view = new CollectionView(element, layout, this)
    }

    private uninstallView() {
        if (!this.view) {
            return
        }
        this.view.uninstall(element =>
                                ReactDOM.unmountComponentAtNode(element))
    }

    private update(results: Result[]) {
        const oldResults = this.results
        this.results = results

        this.updateWrapperClasses()

        if (!this.view) {
            return
        }

        const [removed, added, moved] =
            diff(oldResults, results, result => result.uri)

        this.view.changeIndices(removed, added, moved, {
            delayScroll: true
        }).then(() => {
            this.updatePerspective()
        })
    }

    private updateWrapperClasses() {
        if (!this.wrapper) {
            return
        }

        if (this.results.length) {
            this.wrapper.classList.remove('Results-empty')
        } else {
            this.wrapper.classList.add('Results-empty')
        }
    }

  private updatePerspective() {
    const collectionView = this.view
    if (!collectionView) {
        return
    }

    const containerCenterY = collectionView.containerSize.height / 2
    const contentCenterY = collectionView.contentSize.height / 2
    const centerY = collectionView.scrollPosition.y
      + (this.results.length
        ? Math.min(containerCenterY, contentCenterY)
        : containerCenterY)

    collectionView.content.style.perspectiveOrigin = `50% ${centerY}px`
  }
}
