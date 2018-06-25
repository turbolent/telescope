package com.turbolent.wikidataOntology

import com.turbolent.questionCompiler.Ontology
import com.turbolent.questionParser.Token

class WikidataOntology(numberParser: NumberParser)
    extends Ontology[NodeLabel, EdgeLabel, WikidataEnvironment]
    with InversePropertyEdgeFactory
    with RelationshipEdgeFactory
    with AdjectivePropertyEdgeFactory
    with ComparativePropertyEdgeFactory
    with ValuePropertyEdgeFactory
    with ValueNodeFactory
    with PersonEdgeFactory
    with NamedPropertyEdgeFactory {

  val numberNodeFactory = new NumberNodeFactory(numberParser)

  override def makeNumberNode(number: Seq[Token],
                              unit: Seq[Token],
                              filter: Seq[Token],
                              env: WikidataEnvironment): WikidataNode =
    numberNodeFactory.makeNumberNode(number, unit, filter, env)
}
