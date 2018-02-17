import * as React from 'react'
import './App.css'
import ResultsContainer from '../../containers/Results/ResultsContainer'
import FormContainer from '../../containers/Form/FormContainer'
import ExamplesContainer from '../../containers/Examples/ExamplesContainer'
import ErrorStatusContainer from '../../containers/Status/ErrorStatusContainer'
import NoResultsStatusContainer from '../../containers/Status/NoResultsStatusContainer'

class App extends React.Component {

    render() {
        return (
            <div className="App">
                <FormContainer />
                <div className="AppContent">
                    <ErrorStatusContainer />
                    <NoResultsStatusContainer />
                    <ExamplesContainer />
                    <ResultsContainer />
                </div>
            </div>
        )
    }
}

export default App
