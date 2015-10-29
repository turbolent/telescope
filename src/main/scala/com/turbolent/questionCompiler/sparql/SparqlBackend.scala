package com.turbolent.questionCompiler.sparql

import org.apache.jena.graph.{Node => JenaNode}
import org.apache.jena.query.Query

trait SparqlBackend[N, E] {
  def makeAnonymousVariable(): JenaNode
  def prepareQuery(query: Query)
  def compileNodeLabel(label: N): JenaNode
  def compileEdgeLabel(label: E): JenaNode
}