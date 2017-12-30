import { State } from './state';
import { Action, parseActionCreator, setQuestionActionCreator } from './actions';
import { encodeQuestion, getSavedQuestion } from './history';

export function reducer(state: State, action: Action<{}>): State {
    switch (action.type) {
        case parseActionCreator.startedType: {
            const {cancel: currentCancel} = state;
            if (currentCancel) {
                currentCancel();
            }
            const {cancel: newCancel, question, save} =
                parseActionCreator.getStartedPayload(action);
            if (save) {
                saveQuestion(question);
            }
            return state.withCancel(newCancel);
        }
        case parseActionCreator.succeededType: {
            const parse = parseActionCreator.getSuccessPayload(action);
            return state.withMutations(mutableState =>
                mutableState.withCancel(undefined)
                    .withError(parse.error)
                    .withParse(parse));
        }
        case parseActionCreator.failedType: {
            const error = parseActionCreator.getError(action);
            return state.withMutations(mutableState =>
                mutableState.withCancel(undefined)
                    .withError(error.message)
                    .withParse(undefined));
        }
        case setQuestionActionCreator.type: {
            const question = setQuestionActionCreator.getPayload(action);
            return state.withQuestion(question);
        }
        default:
            return state;
    }
}

function saveQuestion(question: string) {
    const url = encodeQuestion(question);
    const currentURL = encodeQuestion(getSavedQuestion());
    if (url === currentURL) {
        return
    }
    history.pushState({question}, document.title, url)
}
