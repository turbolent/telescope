import * as React from 'react';
import './App.css';
import Form from './FormComponent';
import Body from './BodyComponent';

class App extends React.Component {
    render() {
        return (
            <div className="App">
                <div className="App-header">
                    <Form />
                </div>
                <div className="App-body">
                    <Body />
                </div>
            </div>
        );
    }
}

export default App;
