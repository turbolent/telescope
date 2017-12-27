import { connect, Dispatch } from 'react-redux';
import { fetchQuestion } from './actions';
import * as React from 'react';
import { State } from './state';

interface FormProps {
    request: (question: string) => void;
}

interface FormState {
    value: string;
}

class Form extends React.Component<FormProps, FormState> {

    constructor(props: FormProps) {
        super(props);
        this.state = {value: ''};
    }

    handleChange = (event: React.FormEvent<HTMLInputElement>) => {
        const target = event.target as HTMLInputElement;
        this.setState({value: target.value});
    }

    handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const {value} = this.state;
        this.props.request(value);
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <input type="text" value={this.state.value} onChange={this.handleChange} />
                <input type="submit" value="Ask" />
            </form>
        );
    }
}

const mapDispatchToProps = (dispatch: Dispatch<State>): FormProps =>
    ({
        request: (question: string) =>
            dispatch(fetchQuestion(question)),
    });

export default connect(undefined, mapDispatchToProps)(Form);
