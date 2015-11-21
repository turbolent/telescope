package com.turbolent.questionCompiler.sparql

import com.turbolent.questionCompiler.graph.Node
import com.turbolent.questionCompiler.Environment
import org.apache.jena.graph.{Node => JenaNode}
import org.apache.jena.query.{Query => JenaQuery}
import org.apache.jena.sparql.algebra.Op
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.expr.Expr
import org.apache.jena.sparql.path.Path



trait SparqlBackend[N, E, EnvT <: Environment[N, E]] {

  type NodeT = Node[N, E]

  def compileNodeLabel(label: N, env: EnvT): JenaNode

  def compileEdgeLabel(label: E, env: EnvT): Either[JenaNode, Path]

  //// optional hooks
  
  def prepareOp(op: Op, env: EnvT) = op

  def additionalResultVariables(variable: Var, env: EnvT): List[Var] = Nil

  def prepareQuery(query: JenaQuery, env: EnvT) {}

}