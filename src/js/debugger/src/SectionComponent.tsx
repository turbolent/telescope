import * as React from 'react';
import { ReactNode } from 'react';
import { connect } from 'react-redux';
import { State } from './state';
import Typography from 'material-ui/Typography';
import Card, { CardContent } from 'material-ui/Card';
import './SectionComponent.css';

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
        <Card className="Section">
            <CardContent>
                <Typography type="headline" component="h2">
                    {title}
                </Typography>
                {children}
            </CardContent>
        </Card>
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
