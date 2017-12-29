import { connect, Dispatch } from 'react-redux';
import { parseQuestion } from './actions';
import * as React from 'react';
import { State } from './state';

interface DispatchProps {
    readonly request: (question: string) => void;
}

interface StateProps {
    readonly requesting: boolean;
}

type Props = StateProps & DispatchProps;

interface FormState {
    readonly value: string;
}

class Form extends React.Component<Props, FormState> {

    constructor(props: Props) {
        super(props);
        this.state = {value: ''};
    }

    readonly handleChange = (event: React.FormEvent<HTMLInputElement>) => {
        const target = event.target as HTMLInputElement;
        this.setState({value: target.value});
    }

    readonly handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const {value} = this.state;
        this.props.request(value);
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <input type="text" value={this.state.value} onChange={this.handleChange} />
                <input type="submit" value="Ask" />
                {this.props.requesting ? <span>Loading ...</span> : undefined}
            </form>
        );
    }
}

const mapStateToProps = ({requesting}: State): StateProps => ({
    requesting
});

const mapDispatchToProps = (dispatch: Dispatch<State>): DispatchProps =>
    ({
        request: (question: string) =>
            dispatch(parseQuestion(question)),
    });

export default connect(mapStateToProps, mapDispatchToProps)(Form);
