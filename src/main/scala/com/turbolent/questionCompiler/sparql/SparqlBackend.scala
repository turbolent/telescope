package com.turbolent.questionCompiler.sparql

import org.apache.jena.graph.{Node => JenaNode}
import org.apache.jena.query.{Query => JenaQuery}
import org.apache.jena.sparql.algebra.Op
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.path.Path

trait SparqlBackend[N, E] {

  def makeAnonymousVariable(): JenaNode

  def compileNodeLabel(label: N): JenaNode

  def compileEdgeLabel(label: E): Either[JenaNode, Path]

  //// optional hooks

  def prepareOp(op: Op) = op

  def additionalResultVariables(variable: Var): List[Var] = Nil

  def prepareQuery(query: JenaQuery) {}

}