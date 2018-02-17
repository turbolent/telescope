import * as React from 'react'
import { CollectionView, CollectionViewDelegate, GridLayout } from 'collection-view'
import './Results.css'
import { Result } from '../../types'
import { diff } from '../../diff'
import ResultComponent from '../Result/Result'
import * as ReactDOM from 'react-dom'

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

    constructor(props: Props) {
        super(props)

        this.results = props.results
    }

    getCount() {
        return this.results.length
    }

    configureElement(element: HTMLElement, index: number) {
        element.classList.add('ResultsItem')

        const result = this.results[index]
        let component = (
            <ResultComponent
                imageURL=""
                label={result.label}
                description=""
                extractHTML=""
            />
        )
        ReactDOM.render(component, element)
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
                                          itemSize: [300, 330],
                                          spacing: [20, 20]
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
