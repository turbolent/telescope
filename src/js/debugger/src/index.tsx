import * as React from 'react';
import * as ReactDOM from 'react-dom';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import './index.css';
import { Provider } from 'react-redux';
import createStore from './store';
import { parseQuestion, setQuestion } from './actions';

const store = createStore();
const root = document.getElementById('root') as HTMLElement;

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
    root
);

window.addEventListener('popstate', (event: PopStateEvent) => {
    if (!event.state) {
        return;
    }
    const {sentence} = event.state;
    if (!sentence) {
        return;
    }

    store.dispatch(setQuestion(sentence));
    store.dispatch(parseQuestion(sentence, false));
});

registerServiceWorker();
