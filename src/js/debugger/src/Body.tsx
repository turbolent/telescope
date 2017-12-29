import * as React from 'react';
import TokensComponent from './TokensComponent';
import SectionComponent from './SectionComponent';
import ErrorComponent from './ErrorComponent';

export default () => (
    <div>
        <SectionComponent title="Error" path={['error']}>
            <ErrorComponent />
        </SectionComponent>
        <SectionComponent title="Tokens" path={['parse', 'tokens']}>
            <TokensComponent />
        </SectionComponent>
        <SectionComponent title="Error" path={['parse', 'error']}>
            <ErrorComponent />
        </SectionComponent>
    </div>
);
