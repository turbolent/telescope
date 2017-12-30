import { connect, Dispatch } from 'react-redux';
import { parseQuestion, setQuestion } from './actions';
import * as React from 'react';
import { State } from './state';

interface DispatchProps {
    readonly request: (question: string) => void;
    readonly update: (question: string) => void;
}

interface StateProps {
    readonly value: string;
    readonly requesting: boolean;
}

type Props = StateProps & DispatchProps;

class FormComponent extends React.Component<Props, {}> {

    readonly handleChange = (event: React.FormEvent<HTMLInputElement>) => {
        const target = event.target as HTMLInputElement;
        this.props.update(target.value);
    }

    readonly handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        this.props.request(this.props.value);
    }

    render() {
        const {value} = this.props;
        return (
            <form onSubmit={this.handleSubmit}>
                <input
                    type="text"
                    value={value}
                    onChange={this.handleChange} />
                <input
                    type="submit"
                    value="Ask"
                />
                {
                    this.props.requesting
                        ? <span>Loading ...</span>
                        : undefined
                }
            </form>
        );
    }
}

const mapStateToProps = ({requesting, sentence}: State): StateProps => ({
    value: sentence,
    requesting
});

const mapDispatchToProps = (dispatch: Dispatch<State>): DispatchProps =>
    ({
        request: (question: string) =>
            dispatch(parseQuestion(question, true)),
        update: (question: string) =>
            dispatch(setQuestion(question))
    });

export default connect(mapStateToProps, mapDispatchToProps)(FormComponent);
