import { State } from './state';
import { Action, parseActionCreator, setQuestionActionCreator } from './actions';
import { encodeSentence, getSavedSentence } from './history';

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
                saveSentence(question);
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
            return state.withSentence(question);
        }
        default:
            return state;
    }
}

function saveSentence(sentence: string) {
    const url = encodeSentence(sentence);
    const currentURL = encodeSentence(getSavedSentence());
    if (url === currentURL) {
        return
    }
    history.pushState({sentence}, document.title, url)
}
