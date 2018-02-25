import * as React from 'react'
import './Result.css'
import * as classNames from 'classnames'

export interface Props {
    readonly uri: string
    readonly wikipediaTitle?: string
    readonly imageURL: string
    readonly label: string
    readonly description: string
    readonly extractHTML: string
}

export default class Result extends React.Component<Props, {}> {

    static capitalizeInitial(text: string): string {
        if (!text.length) {
            return text
        }

        return text[0].toUpperCase() + text.substring(1)
    }

    render() {
        const {uri, imageURL, label, description, extractHTML, wikipediaTitle} = this.props

        const link = wikipediaTitle
            ? 'https://en.wikipedia.org/wiki/' + encodeURIComponent(wikipediaTitle)
            : uri

        const labelClassName = classNames('ResultElement', 'ResultLabel', {
            'ResultElement-hidden': !label
        })
        const descriptionClassName = classNames('ResultElement', 'ResultDescription', {
            'ResultElement-hidden': !description})
        const extractClassName = classNames('ResultElement', 'ResultExtract', {
            'ResultElement-hidden': !extractHTML
        })
        const imageClassName = classNames('ResultElement', 'ResultImage', {
            'ResultElement-hidden': !imageURL
        })
        return (
            <div className="Result">
                <img className={imageClassName} src={imageURL}/>
                <div className="ResultContent">
                    <a className={labelClassName} href={link} target="_blank">{label}</a>
                    <div className={descriptionClassName}>
                        {Result.capitalizeInitial(description)}
                    </div>
                    <div
                        className={extractClassName}
                        dangerouslySetInnerHTML={{__html: extractHTML}}
                    />
                </div>
            </div>
        )
    }
}
