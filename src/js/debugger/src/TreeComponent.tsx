import * as React from 'react';
import { TreeNode } from './types';
import { State } from './state';
import { connect } from 'react-redux';
import SubtreeComponent from './SubtreeComponent';
import './TreeComponent.css';

interface StateProps {
    readonly root?: TreeNode;
}

const TreeComponent = ({root}: StateProps) => {
    if (!root) {
        return null;
    }

    return (
        <div className="Tree">
            <SubtreeComponent
                type={root.type}
                children={root.children}
            />
        </div>
    );
};

const mapStateToProps = (s: State): StateProps => ({
    root: s.parse && s.parse.tree
});

export default connect(mapStateToProps)(TreeComponent);
