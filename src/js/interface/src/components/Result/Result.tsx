import * as React from 'react'
import './Result.css'

export interface Props {
    readonly imageURL: string
    readonly label: string
    readonly description: string
    readonly extractHTML: string
}

export default class Result extends React.Component<Props, {}> {

    render() {
        const {imageURL, label, description, extractHTML} = this.props
        return (
            <div className="Result">
                <img className="ResultImage" src={imageURL} />
                <div className="ResultContent">
                    <div className="ResultLabel">{label}</div>
                    <div className="ResultDescription">{description}</div>
                    <div className="ResultExtract" dangerouslySetInnerHTML={{__html: extractHTML}} />
                </div>
            </div>
        )
    }
}
