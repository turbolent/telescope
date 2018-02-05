import * as React from 'react'
import { CollectionView, CollectionViewDelegate, GridLayout } from 'collection-view'
import './Results.css'
import { Result } from '../../types'
import { diff } from '../../diff'

interface ComponentState {
    view?: CollectionView
    results: Result[]
}

export interface Props {
    query: string
    results: Result[]
}

export default class Results
    extends React.Component<Props, ComponentState>
    implements CollectionViewDelegate {

    constructor(props: Props) {
        super(props)
        this.state = props
    }

    getCount() {
        return this.state.results.length
    }

    configureElement(element: HTMLElement, index: number) {
        element.classList.add('ResultsItem')

        const result = this.state.results[index]
        element.innerHTML = result.label
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
            <div className="Results">
                <div ref={this.onRef} />
            </div>
        )
    }

    private onRef = (element: HTMLDivElement | null) => {
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
                                          itemSize: [230, 210],
                                          spacing: [30, 30]
                                      })
        const view = new CollectionView(element, layout, this)
        this.setState({view})
    }

    private uninstallView() {
        if (!this.state.view) {
            return
        }
        this.state.view.uninstall()
    }

    private update(results: Result[]) {

        this.setState(({results: oldResults}) => {
            this.setState({results}, () => {
                if (!this.state.view) {
                    return
                }

                const [removed, added, moved] =
                    diff(oldResults, results, result => result.uri)

                this.state.view.changeIndices(removed, added, moved)
            })
        })
    }
}
