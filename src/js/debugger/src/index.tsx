import * as React from 'react';
import * as ReactDOM from 'react-dom';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import './index.css';
import { createStore, applyMiddleware } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import { Provider } from 'react-redux';
import { State } from './state';
import { reducer } from './reducer';
import thunkMiddleware from 'redux-thunk';
import { createLogger } from 'redux-logger';

const middleware = [thunkMiddleware, createLogger()];
const initialState: State = {requesting: false};
const store = createStore<State>(
    reducer,
    initialState,
    composeWithDevTools(applyMiddleware(...middleware))
);

const root = document.getElementById('root') as HTMLElement;

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
    root
);

registerServiceWorker();
