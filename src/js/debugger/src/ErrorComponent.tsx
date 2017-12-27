import * as React from 'react';
import './ErrorComponent.css';
import { connect } from 'react-redux';
import { State } from './state';

interface Props {
    message: string;
}

const FailureComponent = ({message}: Props) =>
    <div className="Error">{message}</div>;

const mapStateToProps = (s: State): Props => ({
    message: s.response && s.response.error || ''
});

export default connect(mapStateToProps)(FailureComponent);
