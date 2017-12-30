import { Token, Tree, TreeLeaf, TreeNode } from './types';
import * as React from 'react';
import './SubtreeComponent.css';
import { ReactNode } from 'react';
import TokenComponent from './TokenComponent';
import { schemeCategory10, scaleOrdinal } from 'd3-scale';

const types = {
    question: [
        'ListQuestion', 'PersonListQuestion', 'ThingListQuestion'
    ],
    value: [
        'AndValue', 'OrValue', 'RelationshipValue', 'NamedValue',
        'Number', 'NumberWithUnit'
    ],
    query: [
        'AndQuery', 'RelationshipQuery', 'NamedQuery', 'QueryWithProperty',
    ],
    property: [
        'AndProperty', 'OrProperty', 'NamedProperty',
        'PropertyWithFilter', 'InversePropertyWithFilter', 'AdjectivePropertyWithFilter'
    ],
    filter: [
        'AndFilter', 'OrFilter', 'PlainFilter',
        'FilterWithModifier', 'FilterWithComparativeModifier'
    ]
};

const scale = scaleOrdinal(schemeCategory10)
    .domain(Object.keys(types));

const typeCategories = {};
Object.keys(types).forEach(category =>
    types[category].forEach((type: string) =>
        typeCategories[type] = category));

interface Props {
    readonly name?: string;
    readonly type?: string;
    readonly children: (Tree | Token)[];
}

class SubtreeComponent extends React.Component<Props, {}> {

    static renderTreeNode(node: TreeNode, key: number): ReactNode {
        return (
            <SubtreeComponent
                name={node.name}
                type={node.type}
                children={node.children}
                key={key}
            />
        );
    }

    static renderTreeLeaf(leaf: TreeLeaf, key: number): ReactNode {
        return (
            <SubtreeComponent
                name={leaf.name}
                children={leaf.tokens}
                key={key}
            />
        );
    }

    static renderToken(token: Token, key: number): ReactNode {
        return (
            <TokenComponent
                token={token}
                key={key}
            />
        );
    }

    render() {
        const {name, type, children} = this.props;

        let nameNode;
        if (name) {
            nameNode = <div className="SubtreeName">{name}</div>;
        }

        let typeNode;
        if (type) {
            const color = scale(typeCategories[type]);
            let style;
            if (color) {
                style = {color};
            }
            typeNode = <div className="SubtreeType" style={style}>{type}</div>;
        }

        const childNodes = children.map((child, key) => {
            if (child instanceof TreeNode) {
                return SubtreeComponent.renderTreeNode(child, key);
            } else if (child instanceof TreeLeaf) {
                return SubtreeComponent.renderTreeLeaf(child, key);
            } else if (child instanceof Token) {
                return SubtreeComponent.renderToken(child, key);
            }

            throw new Error('Unsupported child: ' + child);
        });

        return (
            <div className="Subtree">
                {nameNode}
                {typeNode}
                <div className="SubtreeChildren">
                    {childNodes}
                </div>
            </div>
        );
    }
}

export default SubtreeComponent;
