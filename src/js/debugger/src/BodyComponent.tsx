import * as React from 'react';
import TokensComponent from './TokensComponent';
import SectionComponent from './SectionComponent';
import ErrorComponent from './ErrorComponent';
import TreeComponent from './TreeComponent';
import QueriesComponent from './QueriesComponent';
import GraphsComponent from './GraphsComponent';

export default () => (
    <div>
        <SectionComponent title="Error" path={['error']}>
            <ErrorComponent />
        </SectionComponent>
        <SectionComponent title="Tokens" path={['parse', 'tokens']}>
            <TokensComponent />
        </SectionComponent>
        <SectionComponent title="Tree" path={['parse', 'tree']}>
            <TreeComponent/>
        </SectionComponent>
        <SectionComponent title="Queries" path={['parse', 'queries']}>
            <QueriesComponent />
        </SectionComponent>
        <SectionComponent title="Graph" path={['parse', 'nodes']}>
            <GraphsComponent />
        </SectionComponent>
    </div>
);
