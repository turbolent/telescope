import * as React from 'react';
import * as ReactDOM from 'react-dom';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import './index.css';
import { Provider } from 'react-redux';
import createStore from './store';
import { parseQuestion, setQuestion } from './actions';
import { getSavedQuestion } from './history';

const store = createStore();
const root = document.getElementById('root') as HTMLElement;

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
    root
);

registerServiceWorker();


function loadState(state: any) {
    const question =
        state && state.question || getSavedQuestion();
    if (!question) {
        return;
    }
    store.dispatch(setQuestion(question));
    store.dispatch(parseQuestion(question, false));
}

window.addEventListener('load', () => {
    loadState(history.state);
});

window.addEventListener('popstate', (event: PopStateEvent) => {
    loadState(event.state);
});
