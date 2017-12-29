import { State } from './state';
import { Action, parseActionCreator } from './actions';

export function reducer(state: State, action: Action<any>): State {
    switch (action.type) {
        case parseActionCreator.startedType: {
            const {cancel: currentCancel} = state;
            if (currentCancel)
                currentCancel();
            const newCancel = parseActionCreator.getStartedPayload(action);
            return state.withCancel(newCancel);
        }
        case parseActionCreator.succeededType: {
            const response = parseActionCreator.getSuccessPayload(action);
            return state.withMutations(mutableState =>
                mutableState.withCancel(undefined)
                    .withError(undefined)
                    .withParse(response));
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
