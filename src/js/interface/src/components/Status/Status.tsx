import * as React from 'react'
import './Status.css'

export interface Props {
    readonly message?: String
}

export default class Status extends React.Component<Props, {}> {

    render() {
        const {message} = this.props
        return (
            <div className={'Status' + (message === undefined ? ' Status-hidden' : '')}>
                <h3 className="StatusHeader">
                    {message}
                </h3>
            </div>
        )
    }
}
