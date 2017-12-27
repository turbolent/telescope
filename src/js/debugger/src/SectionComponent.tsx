import * as React from 'react';
import './SectionComponent.css';
import { ReactNode } from 'react';
import { connect } from 'react-redux';
import { State } from './state';

interface StateProps {
    show: boolean;
}

interface OwnProps {
    children?: ReactNode;
    title: string;
    property: string;
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

const mapStateToProps = (state: State, ownProps: OwnProps): StateProps => ({
    show: state.response ? state.response.hasOwnProperty(ownProps.property) : false
});

export default connect(mapStateToProps)(SectionComponent);
