import * as React from 'react';
import './App.css';
import Form from './FormComponent';
import Body from './BodyComponent';
import { MuiThemeProvider, createMuiTheme } from 'material-ui/styles';
import Reboot from 'material-ui/Reboot';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';

const theme = createMuiTheme({
                                 typography: {
                                     fontFamily: 'Roboto, sans-serif',
                                 },
                                 palette: {
                                     primary: {
                                         light: '#6ab7ff',
                                         main: '#1e88e5',
                                         dark: '#005cb2',
                                         contrastText: '#fff',
                                     }
                                 },
                             });

class App extends React.Component {
    render() {
        return (
            <MuiThemeProvider theme={theme}>
                <Reboot/>
                <div className="App">
                    <AppBar position="fixed">
                        <Toolbar>
                            <Form />
                        </Toolbar>
                    </AppBar>
                    <div className="App-body">
                        <Body />
                    </div>
                </div>
            </MuiThemeProvider>
        );
    }
}

export default App;
