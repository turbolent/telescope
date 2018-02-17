import * as React from 'react'
import './Progress.css'
import * as classNames from 'classnames'

interface Props {
    readonly active: boolean
    readonly hidden: boolean
}

interface State {
    readonly active: boolean
    readonly hidden: boolean
}

export default class Progress extends React.Component<Props, State> {

    private animationElement: Element | null

    constructor(props: Props) {
        super(props)

        this.state = {
            active: props.active,
            hidden: props.hidden
        }
    }

    componentWillReceiveProps(nextProps: Props) {
        if (nextProps.active === this.props.active
            && nextProps.hidden === this.props.hidden) {
            return
        }

        // NOTE: only start. stopping is handled in onAnimationIteration
        if (!this.state.active) {
            if (nextProps.active) {
                this.setState({active: true, hidden: false})
            } else if (nextProps.hidden) {
                this.setState({hidden: true})
            }
        }
    }

    render() {
        const {hidden} = this.state
        return (
            <div className={classNames('Progress', {'Progress-hidden': hidden})} ref={this.onRef}>
                {this.renderItems()}
            </div>
        )
    }

    private onRef = (ref: HTMLDivElement | null) => {
        if (!ref) {
            this.removeAnimationListener()
            return
        }

        this.animationElement = ref.firstElementChild
        this.addAnimationListener()
    }

    private addAnimationListener() {
        let element = this.animationElement
        if (!element) {
            return
        }
        element.addEventListener('animationiteration', this.onAnimationIteration)
    }

    private removeAnimationListener() {
        let element = this.animationElement
        if (!element) {
            return
        }
        element.removeEventListener('animationiteration', this.onAnimationIteration)
    }

    private onAnimationIteration = () => {
        if (this.state.active && !this.props.active) {
            this.setState({active: false, hidden: this.props.hidden})
        }
    }

    private renderItems() {
        const {active} = this.state
        return new Array(9)
            .fill(undefined)
            .map((_, index) => (
                <div
                    className={classNames(
                        'ProgressItem',
                        {'ProgressItem-active': active}
                    )}
                    key={index}
                />)
            )
    }
}
