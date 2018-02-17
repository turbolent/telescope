import * as React from 'react'
import * as ReactDOM from 'react-dom'
import App from './components/App/App'
import registerServiceWorker from './registerServiceWorker'
import './index.css'
import { Provider } from 'react-redux'
import createStore from './store'
import { requestParse, saveQuestion, setQuestion } from './actions'
import { getSavedQuestion } from './history'
import 'typeface-fira-sans'

const store = createStore()
const root = document.getElementById('root') as HTMLElement

ReactDOM.render(
    <Provider store={store}>
        <App />
    </Provider>,
    root
)

registerServiceWorker()

function loadState(state: any) {
    const question = state && state.question || getSavedQuestion()
    store.dispatch(setQuestion(question))
    store.dispatch(saveQuestion(question))
    store.dispatch(requestParse(question))
}

window.addEventListener('load', () => {
    loadState(history.state)
})

window.addEventListener('popstate', (event: PopStateEvent) => {
    loadState(event.state)
})
