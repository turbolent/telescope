package com.turbolent.questionCompiler.sparql

import com.turbolent.questionCompiler.graph._
import org.apache.jena.sparql.algebra.optimize.TransformMergeBGPs
import org.apache.jena.sparql.algebra.{Op, OpAsQuery, Transformer}
import org.apache.jena.sparql.algebra.op._
import org.apache.jena.sparql.core.{Var, BasicPattern}
import org.apache.jena.graph.{Triple, Node => JenaNode}
import org.apache.jena.query.Query
import org.apache.jena.sparql.expr._

import scala.collection.JavaConversions._


class SparqlGraphCompiler[N, E](backend: SparqlBackend[N, E]) {

  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]
  type FilterT = Filter[N, E]
  type TripleFactory = (JenaNode, JenaNode) => Triple
  type OpMerger = (Op, Op) => Op
  type Function2ExprFactory = (Expr, Expr) => Expr
  type OpFactory = (Option[Op]) => Op

  def compileFunction2Filter(compiledNode: JenaNode, otherNode: NodeT,
                             op: Op, exprFactory: Function2ExprFactory): Op =
  {
    val (compiledOtherNode, filteredOp) = compileNode(otherNode,
      _ map {
        OpJoin.create(op, _)
      } getOrElse op)

    val otherExpr =
      if (compiledOtherNode.isVariable)
        new ExprVar(compiledOtherNode)
      else
        NodeValue.makeNode(compiledOtherNode)

    val expr = exprFactory(new ExprVar(compiledNode), otherExpr)
    OpFilter.filter(expr, filteredOp)
  }

  def compileFilter(node: JenaNode, op: Op)(filter: FilterT): Op = {
    filter match {
      case LessThanFilter(otherNode) =>
        compileFunction2Filter(node, otherNode, op,
          new E_LessThan(_, _))

      case GreaterThanFilter(otherNode) =>
        compileFunction2Filter(node, otherNode, op,
          new E_GreaterThan(_, _))

      case ConjunctionFilter(filters) =>
        filters.foldLeft(op)(compileFilter(node, _)(_))
    }
  }

  def compileNode(node: NodeT, opFactory: OpFactory): (JenaNode, Op) = {
    val compiledNode = backend.compileNodeLabel(node.label)
    val optEdgeOp = node.edge.map(compileEdge(compiledNode))
    val filteredOp = opFactory(optEdgeOp)

    val op = node.filter map {
      compileFilter(compiledNode, filteredOp)
    } getOrElse filteredOp

    (compiledNode, op)
  }

  def compileEdges(edges: Seq[EdgeT], node: JenaNode, merge: OpMerger): Op =
    edges.map(compileEdge(node))
        .reduce(merge)

  def compileEdge(node: JenaNode)(edge: EdgeT): Op = {
    edge match {
      case OutEdge(label, target) =>
        compileEdgeLabel(label, target,
          (property, compiledTarget) =>
            new Triple(node, property, compiledTarget))

      case InEdge(source, label) =>
        compileEdgeLabel(label, source,
          (property, compiledSource) =>
            new Triple(compiledSource, property, node))

      case ConjunctionEdge(edges) =>
        compileEdges(edges, node, OpJoin.create)

      case DisjunctionEdge(edges) =>
        compileEdges(edges, node, OpUnion.create)
    }
  }

  def compileEdgeLabel(label: E, node: NodeT, factory: TripleFactory): Op = {
    val pattern = new BasicPattern()
    val patternOp = new OpBGP(pattern)

    val (compiledNode, op) = compileNode(node,
      _ map {
        OpJoin.create(patternOp, _)
      } getOrElse patternOp)

    val property = backend.compileEdgeLabel(label)
    val triple = factory(property, compiledNode)
    pattern.add(triple)

    op
  }

  def compileQuery(node: Node[N, E]): Query = {
    require(node.edge.isDefined,
      "root node needs to have edges")

    val (compiledNode, op) = compileNode(node, _.get)
    assert(compiledNode.isInstanceOf[Var],
      "root node needs to be compiled to a variable")

    val projection = new OpProject(op, List(compiledNode.asInstanceOf[Var]))

    val optimized = Transformer.transform(new TransformMergeBGPs, projection)

    val query = OpAsQuery.asQuery(optimized)
    query.setQuerySelectType()
    backend.prepareQuery(query)

    query
  }
}
