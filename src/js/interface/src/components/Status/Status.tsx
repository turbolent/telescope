import * as React from 'react'
import * as classNames from 'classnames'
import './Status.css'

export interface Props {
    readonly message?: String
}

export default class Status extends React.Component<Props, {}> {

    render() {
        const {message} = this.props
        return (
            <div className={classNames('Status', {'Status-hidden': message === undefined})}>
                <h3 className="StatusHeader">
                    {message}
                </h3>
            </div>
        )
    }
}
