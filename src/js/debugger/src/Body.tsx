import * as React from 'react';
import TokensComponent from './TokensComponent';
import SectionComponent from './SectionComponent';
import ErrorComponent from './ErrorComponent';

export default () => (
    <div>
        <SectionComponent title="Tokens" property="tokens">
            <TokensComponent />
        </SectionComponent>
        <SectionComponent title="Error" property="error">
            <ErrorComponent />
        </SectionComponent>
    </div>
);
