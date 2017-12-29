import { createStore as createReduxStore, applyMiddleware, Store, Middleware } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import { State } from './state';
import { reducer } from './reducer';
import thunkMiddleware from 'redux-thunk';
import { createLogger } from 'redux-logger';

export default function createStore(
    withLogger: boolean = true,
    withDevtools: boolean = true
): Store<State> {
    const middleware: [Middleware] = [thunkMiddleware];
    if (withLogger) {
        middleware.push(createLogger());
    }

    const appliedMiddleware =
        applyMiddleware(...middleware);

    return createReduxStore<State>(
        reducer,
        new State(),
        withDevtools
            ? composeWithDevTools(appliedMiddleware)
            : appliedMiddleware
    );
}
