package com.turbolent.wikidataOntology

import com.turbolent.questionCompiler.graph.OutEdge
import com.turbolent.questionCompiler

trait PersonEdgeFactory
    extends questionCompiler.PersonEdgeFactory[NodeLabel, EdgeLabel, WikidataEnvironment] {

  def makePersonEdge(env: WikidataEnvironment): OutEdge[EdgeLabel, NodeLabel] =
    out(P.isA, Q.human)

}
