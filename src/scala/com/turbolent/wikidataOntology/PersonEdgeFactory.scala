package com.turbolent.wikidataOntology

import com.turbolent.questionCompiler.graph.OutEdge


trait PersonEdgeFactory {

  def makePersonEdge(env: WikidataEnvironment): OutEdge[EdgeLabel, NodeLabel] =
    out(P.isA, Q.human)

}
