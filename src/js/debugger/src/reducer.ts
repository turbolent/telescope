import { State } from './state';
import { Action, parseActionCreator } from './actions';

export function reducer(state: State, action: Action<{}>): State {
    switch (action.type) {
        case parseActionCreator.startedType: {
            const {cancel: currentCancel} = state;
            if (currentCancel) {
                currentCancel();
            }
            const newCancel = parseActionCreator.getStartedPayload(action);
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
        default:
            return state;
    }
}
