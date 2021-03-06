import { connect } from 'react-redux';
import { State } from './state';
import * as React from 'react';
import QueryComponent from './QueryComponent';
import './QueriesComponent.css';

interface StateProps {
    readonly queries?: string[];
}

const QueriesComponent = ({queries}: StateProps) => {
    return (
        <div className="Queries">
            {(queries || [])
                .map((query: string, index: number) =>
                    <QueryComponent query={query} key={index} />)}
        </div>
    );
};

const mapStateToProps = (s: State): StateProps => ({
    queries: s.parse && s.parse.queries
});

export default connect(mapStateToProps)(QueriesComponent);
