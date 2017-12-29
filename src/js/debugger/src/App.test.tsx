import * as React from 'react';
import * as ReactDOM from 'react-dom';
import App from './App';
import { Provider } from 'react-redux';
import createStore from './store';

it('renders without crashing', () => {
    const div = document.createElement('div');
    const store = createStore(false, false);
    ReactDOM.render(<Provider store={store}><App /></Provider>, div);
});
