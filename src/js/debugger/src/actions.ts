import { Dispatch } from 'redux';
import { ThunkAction } from 'redux-thunk';
import { State } from './state';
import { Cancel, parse } from './api';
import { Parse } from './types';

type Thunk = ThunkAction<void, State, void>;

interface BaseAction<T> {
    readonly type: string;
    readonly payload?: T;
}

interface ErrorAction {
    readonly type: string;
    readonly payload: Error;
    readonly error: true;
}

export type Action<T> = BaseAction<T> | ErrorAction;

class ActionCreator<T> {
    readonly type: string;

    readonly failedType: string;

    static create<T>(type: string, payload?: T): BaseAction<T> {
        return {type, payload};
    }

    constructor(type: string) {
        this.type = type;

        this.failedType = this.statusType(BaseActionStatus.Error);
    }

    protected statusType(status: string) {
        return this.type + '_' + status;
    }

    create(payload: T): BaseAction<T> {
        return ActionCreator.create(this.type, payload);
    }

    failed(error: Error): ErrorAction {
        return {
            type: this.statusType(BaseActionStatus.Error),
            payload: error,
            error: true
        };
    }

    getPayload(action: Action<{}>): T {
        return action.payload as T;
    }

    getError(action: Action<{}>): Error {
        return action.payload as Error;
    }
}

enum BaseActionStatus {
    Error = 'ERROR'
}

enum RequestActionStatus {
    Loading = 'LOADING',
    Success = 'SUCCESS',
}

class RequestActionCreator<T = void, U = void, V = void> extends ActionCreator<V> {

    readonly startedType: string;
    readonly succeededType: string;

    readonly started: (payload: T) => BaseAction<T>;
    readonly succeeded: (payload: U) => BaseAction<U>;

    constructor(type: string) {
        super(type);

        this.started = this.creator<T>(RequestActionStatus.Loading);
        this.succeeded = this.creator<U>(RequestActionStatus.Success);

        this.startedType = this.statusType(RequestActionStatus.Loading);
        this.succeededType = this.statusType(RequestActionStatus.Success);
    }

    getStartedPayload(action: Action<{}>): T {
        return action.payload as T;
    }

    getSuccessPayload(action: Action<{}>): U {
        return action.payload as U;
    }

    private creator<X>(status: RequestActionStatus): (payload: X) => BaseAction<X> {
        return (payload: X): BaseAction<X> =>
            ActionCreator.create(this.statusType(status), payload);
    }
}

interface ParseStartedPayload {
    readonly cancel: Cancel;
    readonly question: string;
    readonly save: boolean;
};

export const parseActionCreator = new RequestActionCreator<ParseStartedPayload, Parse, void>('PARSE');

export const parseQuestion = (question: string, save: boolean): Thunk =>
    (dispatch: Dispatch<State>) => {
        const [promise, cancel] = parse(question);
        dispatch(parseActionCreator.started({cancel, question, save}));
        promise.then(response => {
            dispatch(parseActionCreator.succeeded(response));
        }).catch(reason => {
            dispatch(parseActionCreator.failed(new Error(reason)));
        });
    };


export const setQuestionActionCreator = new ActionCreator<string>('QUESTION_SET');

export const setQuestion =
    setQuestionActionCreator.create.bind(setQuestionActionCreator);
