import * as React from 'react';
import './App.css';
import { MuiThemeProvider, createMuiTheme } from 'material-ui/styles';
import Reboot from 'material-ui/Reboot';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import ContentComponent from './ResultsComponent';
import Button from 'material-ui/Button';

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

interface State {
    count: number
}

class App extends React.Component<{}, State> {

    constructor(props: {}) {
        super(props);

        this.state = {count: 2}
    }

    inc = () =>
        this.setState(prevState => ({count: prevState.count + 1}));

    render() {
        return (
            <MuiThemeProvider theme={theme}>
                <Reboot/>
                <AppBar position="fixed">
                    <Toolbar>
                        <Button onClick={this.inc}>
                            Add
                        </Button>
                    </Toolbar>
                </AppBar>
                <ContentComponent count={this.state.count} />
            </MuiThemeProvider>
        );
    }
}

export default App;
