import * as React from 'react';
import TokenComponent from './TokenComponent';
import './TokensComponent.css';
import { Token } from './types';
import { State } from './state';
import { connect } from 'react-redux';

interface Props {
    tokens: Token[];
}

const TokensComponent = ({tokens}: Props) => (
    <div className="Tokens">
        {tokens.map((token, index) =>
            <TokenComponent token={token} key={index} />)}
    </div>
);

const mapStateToProps = (s: State): Props => ({
    tokens: s.response
        ? s.response.tokens
        : []
});

export default connect(mapStateToProps)(TokensComponent);
