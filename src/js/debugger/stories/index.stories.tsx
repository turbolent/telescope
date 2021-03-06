import * as React from 'react';

import { storiesOf } from '@storybook/react';
import TokenComponent from '../src/TokenComponent';
import {
    GraphItemLabel, GraphPropertyLabel, GraphVarLabel, Token, TreeLeaf,
    TreeNode
} from '../src/types';
import { TokensComponent } from '../src/TokensComponent';
import { TreeComponent } from '../src/TreeComponent';
import QueryComponent from '../src/QueryComponent';
import { GraphNode } from '../src/types';
import { graph1, graph2 } from './graph-data';
import { GraphComponentDirectedEdge, GraphComponentLabelNode, parseGraphNode } from '../src/graph/types';
import GraphComponent from '../src/graph/GraphComponent';

storiesOf('Token', module)
    .add('noun', () => {
        const token = new Token('book', 'NNS', 'books');
        return <TokenComponent token={token} />;
    });

storiesOf('Tokens', module)
    .add('empty', () =>
        <TokensComponent tokens={[]} />)
    .add('sentence', () => {
        const tokens = [
            new Token('president', 'NNS', 'presidents'),
            new Token('bear', 'VBN', 'born'),
            new Token('before', 'IN', 'before'),
            new Token('1900', 'CD', '1900')
        ];
        return <TokensComponent tokens={tokens} />;
    });

storiesOf('Tree', module)
    .add('simple', () => {
        const root =
            new TreeNode('ListQuestion',
                         [
                             new TreeNode('NamedQuery',
                                          [
                                              new TreeLeaf('name',
                                                           [
                                                               new Token('president',
                                                                         'NNS',
                                                                         'presidents')
                                                           ])
                                          ],
                                          'query')
                         ]);
        return <TreeComponent root={root} />;
    })
    .add('complex', () => {
        const root =
            new TreeNode('ListQuestion', [
                new TreeNode('QueryWithProperty', [
                                 new TreeNode('NamedQuery', [
                                                  new TreeLeaf('name', [
                                                      new Token('president',
                                                                'NNS',
                                                                'presidents')
                                                  ])
                                              ],
                                              'query'),
                                 new TreeNode('PropertyWithFilter', [
                                                  new TreeLeaf('name', [
                                                      new Token('bear',
                                                                'VBN',
                                                                'born')
                                                  ]),
                                                  new TreeNode('FilterWithModifier', [
                                                                   new TreeLeaf('modifier', [
                                                                       new Token('before',
                                                                                 'IN',
                                                                                 'before')
                                                                   ]),
                                                                   new TreeNode('Number', [
                                                                                    new TreeLeaf('number', [
                                                                                        new Token('1900',
                                                                                                  'CD',
                                                                                                  '1900')
                                                                                    ])
                                                                                ],
                                                                                'value')
                                                               ],
                                                               'filter')
                                              ],
                                              'property')
                             ],
                             'query')
            ]);

        return <TreeComponent root={root} />;
    });

storiesOf('Query', module)
    .add('simple', () => {
        const query = `
PREFIX  p:    <http://www.wikidata.org/prop/>
PREFIX  bd:   <http://www.bigdata.com/rdf#>
PREFIX  wdt:  <http://www.wikidata.org/prop/direct/>
PREFIX  v:    <http://www.wikidata.org/prop/statement/>
PREFIX  wikibase: <http://wikiba.se/ontology#>
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  wd:   <http://www.wikidata.org/entity/>

SELECT DISTINCT  ?1 ?1Label
WHERE
  { { ?1 p:P39/(v:P39/(wdt:P279)*) wd:Q30461
      { ?1  wdt:P569  ?2
        FILTER ( year(?2) < 1900 )
      }
    }
    SERVICE wikibase:label
      { bd:serviceParam
                  wikibase:language  "en"}
  }
`;
        return <QueryComponent query={query}/>;
});

storiesOf('Types', module)
    .add('GraphNode', () => {
        const node = GraphNode.decode(graph1);
        return (
            <code style={{ whiteSpace: 'pre'}}>
                {JSON.stringify(node, null, 4)}
            </code>
        );
    });

class GraphComponentWrapper extends React.Component<{}, {first: boolean}> {
    private static graphs = [
        ['presidents born before 1900', graph1],
        ['books written by George Orwell', graph2]
    ];

    constructor(props: {}) {
        super(props);
        this.state = {
           first: true
        };
    }

    onToggle = () => {
        this.setState(state => ({
            first: !state.first
        }));
    }

    render() {
        const index = this.state.first ? 0 : 1;
        const [name, graph] = GraphComponentWrapper.graphs[index];
        const node = GraphNode.decode(graph);
        const [ nodes, edges ] = parseGraphNode(node, true);
        return (
            <div>
                <p><b>Showing:</b> {name}</p>
                <p><button onClick={this.onToggle}>Toggle</button></p>
                <GraphComponent
                    nodes={nodes}
                    links={edges}
                />
            </div>
        );
    }
}

storiesOf('Graph', module)
    .add('ComponentLabelNode', () => {
        const nodeLabel =
            new GraphItemLabel({item: {id: 30461, name: 'president'}}, 'ItemLabel');
        const componentNode = GraphComponentLabelNode.fromGraphNodeLabel(nodeLabel);
        return (
            <code style={{ whiteSpace: 'pre'}}>
                {JSON.stringify(componentNode, null, 4)}
            </code>
        );
    })
    .add('ComponentDirectedEdge', () => {
        const edgeLabel =
            new GraphPropertyLabel({property: {id: 31, name: 'is instance of'}}, 'PropertyLabel');
        const sourceNodeLabel = new GraphVarLabel({id: 0}, 'VarLabel');
        const sourceComponentNode =
            GraphComponentLabelNode.fromGraphNodeLabel(sourceNodeLabel) as GraphComponentLabelNode;
        const targetNodeLabel = new GraphItemLabel({item: {id: 6256, name: 'country'}}, 'ItemLabel');
        const targetComponentNode =
            GraphComponentLabelNode.fromGraphNodeLabel(targetNodeLabel) as GraphComponentLabelNode;
        const componentNode =
            GraphComponentDirectedEdge.fromGraphEdgeLabel(edgeLabel, sourceComponentNode, targetComponentNode);
        return (
            <code style={{ whiteSpace: 'pre'}}>
                {JSON.stringify(componentNode, null, 4)}
            </code>
        );
    })
    .add('Parse', () => {
        const node = GraphNode.decode(graph1);
        const parsed = parseGraphNode(node, true);
        return (
            <code style={{ whiteSpace: 'pre'}}>
                {JSON.stringify(parsed, null, 4)}
            </code>
        );
    })
    .add('GraphComponent', () =>
        <GraphComponentWrapper />);
