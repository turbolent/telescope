import * as React from 'react';
import './SectionComponent.css';
import { ReactNode } from 'react';
import { connect } from 'react-redux';
import { State } from './state';

interface StateProps {
    readonly show: boolean;
}

interface OwnProps {
    readonly children?: ReactNode;
    readonly title: string;
    readonly path: string[];
}

type Props = StateProps & OwnProps;

const SectionComponent = ({title, show, children}: Props) => {
    if (!show) {
        return null;
    }

    return (
        <div className="Section">
            <h2>{title}</h2>
            {children}
        </div>
    );
};

const mapStateToProps = (state: State, ownProps: OwnProps): StateProps => {
    const reducedState = ownProps.path.reduce((currentState, property) => {
        if (currentState === null || currentState === undefined) {
            return null;
        }
        return currentState[property];
    },                                        state);

    return {
        show: reducedState !== null && reducedState !== undefined
    };
};

export default connect(mapStateToProps)(SectionComponent);
