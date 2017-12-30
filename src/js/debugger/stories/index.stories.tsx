import * as React from 'react';

import { storiesOf } from '@storybook/react';
import TokenComponent from '../src/TokenComponent';
import { Token, TreeLeaf, TreeNode } from '../src/types';
import { TokensComponent } from '../src/TokensComponent';
import { TreeComponent } from '../src/TreeComponent';
import QueryComponent from '../src/QueryComponent';

storiesOf('Token', module)
    .add('noun', () => {
        const token = new Token('book', 'NNS', 'books');
        return <TokenComponent token={token} />
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
        return <TokensComponent tokens={tokens} />
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
        return <TreeComponent root={root} />
    })
    .add('complex', () => {
        const root =
            new TreeNode('ListQuestion',
                         [
                             new TreeNode('QueryWithProperty',
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
                                                           'query'),
                                              new TreeNode('PropertyWithFilter',
                                                           [
                                                               new TreeLeaf('name',
                                                                            [
                                                                                new Token('bear',
                                                                                          'VBN',
                                                                                          'born')
                                                                            ]),
                                                               new TreeNode('FilterWithModifier',
                                                                            [
                                                                                new TreeLeaf('modifier',
                                                                                             [
                                                                                                 new Token('before',
                                                                                                           'IN',
                                                                                                           'before')
                                                                                             ]),
                                                                                new TreeNode('Number',
                                                                                             [
                                                                                                 new TreeLeaf('number',
                                                                                                              [
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


        return <TreeComponent root={root} />
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
        return <QueryComponent query={query}/>
});