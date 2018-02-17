import * as React from 'react'
import './Examples.css'
import Example from '../Example/Example'
import * as classNames from 'classnames'

export interface Props {
    readonly hidden: boolean
}

const examples = [
    'Which books were written by Jane Austen?',
    'What are the biggest cities in Canada?',
    'List albums of Pink Floyd',
    'cities located in California',
    'Give me movies directed by Quentin Tarantino',
    'Which cities are bigger than New York City?',
    'Who is older than Barack Obama?',
    'What actor married John F. Kennedy\'s sister?',
    'Which cities have more than two million inhabitants?',
    'books by George Orwell',
    'Which presidents were born before 1900?',
    'authors which died in Berlin or Paris'
]

export default class Examples extends React.Component<Props, {}> {

    render() {
        const {hidden} = this.props
        return (
            <div className={classNames('Examples', {'Examples-hidden': hidden})}>
                <h3 className="ExamplesHeader">
                    Examples
                </h3>
                <ul>
                    {examples.map((example: string, index: number) =>
                                      <Example question={example} key={index} />)}
                </ul>
            </div>
        )
    }
}
