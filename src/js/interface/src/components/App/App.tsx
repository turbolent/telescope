import * as React from 'react'
import './App.css'
import ResultsContainer from '../../containers/Results/ResultsContainer'
import FormContainer from '../../containers/Form/FormContainer'

class App extends React.Component {

    render() {
        return (
            <div className="App">
                <FormContainer />
                <div className="AppContent">
                    <ResultsContainer />
                </div>
            </div>
        )
    }
}

export default App
