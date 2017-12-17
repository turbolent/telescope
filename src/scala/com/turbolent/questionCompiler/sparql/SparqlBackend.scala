package com.turbolent.questionCompiler.sparql

import com.turbolent.questionCompiler.graph
import com.turbolent.questionCompiler.Environment
import org.apache.jena.graph.{Node => JenaNode}
import org.apache.jena.query.{Query => JenaQuery}
import org.apache.jena.sparql.algebra.Op
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.path.Path
import org.apache.jena.sparql.expr.Expr


trait SparqlBackend[N, E, Env <: Environment[N, E]] {

  type Node = graph.Node[N, E]
  type Edge = graph.Edge[E, N]

  /** Return a Jena node for the given node label */
  def compileNodeLabel(label: N, env: Env): JenaNode

  /** Return a Jena node or path for the given edge label */
  def compileEdgeLabel(label: E, env: Env): Either[JenaNode, Path]

  //// optional hooks

  /** Expand the given node into another, possibly more complex node, if needed */
  def expandNode(node: Node, context: NodeCompilationContext, env: Env): Node = node

  def prepareLeftFunctionExpression(leftExpr: Expr, otherNode: Node): Expr = leftExpr

  def prepareOp(op: Op, env: Env) = op

  def additionalResultVariables(variable: Var, env: Env): List[Var] = Nil

  def prepareQuery(query: JenaQuery, env: Env) {}

}