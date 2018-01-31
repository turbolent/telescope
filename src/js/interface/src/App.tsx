import * as React from 'react';
import './App.css';
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
                <AppBar position="fixed">
                    <Toolbar>
                        
                    </Toolbar>
                </AppBar>                
            </MuiThemeProvider>
        );
    }
}

export default App;
