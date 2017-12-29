import { Wikidata } from './wikidata';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/neo.css';
import 'codemirror/mode/sparql/sparql.js';
import './QueryComponent.css';
import * as React from 'react';
import { EditorConfiguration } from 'codemirror';

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
                    <a href={link} target="_blank">Open in Query Editor</a>
                </div>
            </div>
        );
    }

    private readonly onBeforeChange = () => undefined;
}
