import { Wikidata } from './wikidata';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/neo.css';
import 'codemirror/mode/sparql/sparql.js';
import './QueryComponent.css';
import * as React from 'react';
import { EditorConfiguration } from 'codemirror';
import OpenIcon from 'material-ui-icons/OpenInNew';
import IconButton from 'material-ui/IconButton';
import Tooltip from 'material-ui/Tooltip';

interface Props {
    readonly query: string;
}

export default class QueryComponent extends React.Component<Props, {}> {
    private static options: EditorConfiguration = {
        mode: 'application/sparql-query',
        readOnly: 'nocursor',
        theme: 'neo',
        lineNumbers: true
    };

    render() {
        const {query} = this.props;
        const link = Wikidata.getQueryURL(query);
        return (
            <div className="Query">
                <CodeMirror
                    className="QueryText"
                    value={query}
                    options={QueryComponent.options}
                    onBeforeChange={this.onBeforeChange}
                />
                <div className="QueryActions">
                    <Tooltip title="Open in Query Editor">
                        <IconButton
                            color="primary"
                            component={props => <a href={link} target="_blank" {...props} />}
                        >
                            <OpenIcon />
                        </IconButton>
                    </Tooltip>
                </div>
            </div>
        );
    }

    private readonly onBeforeChange = () => undefined;
}
