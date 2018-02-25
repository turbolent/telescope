import * as React from 'react'
import { CollectionView, CollectionViewDelegate, GridLayout } from 'collection-view'
import './Results.css'
import { Result, WikipediaPreview } from '../../types'
import { diff } from '../../diff'
import ResultComponent from '../Result/Result'
import * as ReactDOM from 'react-dom'
import { requestPreview } from '../../api'

export interface Props {
    query: string
    results: Result[]
}

export default class Results
    extends React.Component<Props, {}>
    implements CollectionViewDelegate {

    private view?: CollectionView
    private results: Result[]
    private wrapper: HTMLDivElement | null
    // TODO: replace by cache
    // result URI -> wikipedia preview
    private previews = new Map<string, WikipediaPreview>()
    // TODO: replace by cache, can be larger than previews
    private urisWithoutPreviews = new Set<string>()
    private elementIndices = new WeakMap<Element, number>()

    static renderResult(element: HTMLElement, result: Result, preview?: WikipediaPreview) {
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
            <div className="Results" ref={this.onWrapperRef}>
                <div ref={this.onScrollRef} />
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
                                          insets: [[inset, inset], [inset, inset]],
                                          itemSize: [300, 380],
                                          spacing: [24, 24]
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

        this.view.changeIndices(removed, added, moved)
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
}
