import * as React from 'react';
import * as ReactDOM from 'react-dom';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import './index.css';
import { Provider } from 'react-redux';
import createStore from './store';

const store = createStore();
const root = document.getElementById('root') as HTMLElement;

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
    root
);

registerServiceWorker();
