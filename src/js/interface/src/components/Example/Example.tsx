import * as React from 'react'
import { encodeQuestion } from '../../history'

interface Props {
    readonly question: string
}

export default class Example extends React.Component<Props, {}> {

    render() {
        const {question} = this.props
        return (
            <li className="Example">
                <a
                    className="ExampleLink"
                    href={encodeQuestion(question)}
                >
                    {question}
                </a>
            </li>
        )
    }
}
