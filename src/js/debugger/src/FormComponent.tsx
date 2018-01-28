import { connect, Dispatch } from 'react-redux';
import { parseQuestion, setQuestion } from './actions';
import * as React from 'react';
import { State } from './state';
import './FormComponent.css';
import IconButton from 'material-ui/IconButton';
import BugReportIcon from 'material-ui-icons/BugReport';

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

    readonly handleChange = (event: React.FormEvent<HTMLTextAreaElement | HTMLInputElement>) => {
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
                    className="FormInput"
                    type="text"
                    value={value}
                    placeholder="Query"
                    onChange={this.handleChange}
                />
                <IconButton
                    type="submit"
                >
                    <BugReportIcon nativeColor="white"/>
                </IconButton>
            </form>
        );
    }
}

const mapStateToProps = ({requesting, question}: State): StateProps => ({
    value: question,
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
