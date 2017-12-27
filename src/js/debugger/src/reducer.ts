import { State } from './state';
import { Action, RECEIVE_ANSWER, ReceiveAnswerAction, REQUEST_QUESTION } from './actions';

export function reducer(state: State, action: Action): State {
    switch (action.type) {
        case REQUEST_QUESTION:
            return {
                ...state,
                requesting: true
            };
        case RECEIVE_ANSWER:
            const {response} = action as ReceiveAnswerAction;
            return {
                ...state,
                requesting: false,
                response
            };
        default:
            return state;
    }
}
