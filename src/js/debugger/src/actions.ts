import { Dispatch } from 'redux';
import { ThunkAction } from 'redux-thunk';
import { State } from './state';
import { QueriesResponse, requestQueries } from './api';

export const REQUEST_QUESTION = 'REQUEST_QUESTION';
export type REQUEST_QUESTION = typeof REQUEST_QUESTION;

export interface RequestQuestionAction {
    type: REQUEST_QUESTION;
}

export const requestQuestion = (): RequestQuestionAction => ({
    type: REQUEST_QUESTION
});

export const RECEIVE_ANSWER = 'RECEIVE_ANSWER';
export type RECEIVE_ANSWER = typeof RECEIVE_ANSWER;

export interface ReceiveAnswerAction {
    type: RECEIVE_ANSWER;
    response: QueriesResponse;
}

export const receiveAnswer = (response: QueriesResponse): ReceiveAnswerAction => ({
    type: RECEIVE_ANSWER,
    response
});

type Thunk = ThunkAction<void, State, void>;

export const fetchQuestion = (question: string): Thunk =>
    (dispatch: Dispatch<State>) => {
        dispatch(requestQuestion());
        const [promise] = requestQueries(question);
        promise.then(response => {
            dispatch(receiveAnswer(response));
        });
    };

export type Action =
    RequestQuestionAction
    | ReceiveAnswerAction;
