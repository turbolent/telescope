import * as React from 'react'
import './Form.css'
import Progress from '../Progress/Progress'

export interface InputProps {
    readonly value: string
    readonly requesting: boolean
    readonly empty: boolean
}

export interface OutputProps {
    readonly save: (question: string) => void
    readonly request: (question: string) => void
    readonly update: (question: string) => void
}

type Props = InputProps & OutputProps

export default class Form extends React.Component<Props, {}> {

    readonly handleChange = (event: React.FormEvent<HTMLTextAreaElement | HTMLInputElement>) => {
        const target = event.target as HTMLInputElement
        this.props.update(target.value)
    }

    readonly handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()
        const question = this.props.value
        this.props.save(question)
        this.props.request(question)
    }

    render() {
        const {value, requesting, empty} = this.props
        const hidden = empty && !requesting
        return (
            <form
                className="Form"
                onSubmit={this.handleSubmit}
            >
                <Progress
                    active={requesting}
                    hidden={hidden}
                />
                <input
                    className="FormInput"
                    type="text"
                    value={value}
                    placeholder="Query"
                    onChange={this.handleChange}
                />
                <button type="submit">
                    Search
                </button>
            </form>
        )
    }
}
