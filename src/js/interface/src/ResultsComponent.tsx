import * as React from 'react';
import { CollectionView, CollectionViewDelegate, GridLayout } from 'collection-view'
import './ResultsComponent.css';


interface ContentComponentState {
    view?: CollectionView;
    count: number
}

interface ContentComponentProps {
    count: number
}

export default class ContentComponent
    extends React.Component<ContentComponentProps, ContentComponentState>
    implements CollectionViewDelegate {

    constructor(props: ContentComponentProps) {
        super(props);
        this.state = props
    }

    private onRef = (element: HTMLDivElement | null) => {
        if (!element) {
            this.uninstallView();
            return;
        }

        this.installView(element)
    };

    private installView(element: HTMLDivElement) {
        const layout = new GridLayout();
        const view = new CollectionView(element, layout, this);
        this.setState({view})
    }

    private uninstallView() {
        if (!this.state.view)
            return;
        this.state.view.uninstall()
    }

    private update(count: number) {
        this.setState({count}, () => {
            if (!this.state.view)
                return;
            this.state.view.changeIndices([], [count - 1], new Map())
        })
    }

    getCount() {
        return this.state.count
    }

    configureElement(element: HTMLElement, index: number) {
        element.classList.add('ResultsItem');
        element.innerText = String(index);
    }

    shouldComponentUpdate() {
        return false
    }

    componentWillReceiveProps(nextProps: ContentComponentProps) {
        if (nextProps.count === this.props.count)
            return;
        this.update(nextProps.count);
    }

    componentWillUnmount() {
        this.uninstallView()
    }

    render() {
        return <div className="Results">
            <div ref={this.onRef}>
            </div>
        </div>
    }
}
