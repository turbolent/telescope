package com.turbolent.questionCompiler.sparql

import com.turbolent.questionCompiler.graph._
import org.apache.jena.sparql.algebra.optimize.TransformMergeBGPs
import org.apache.jena.sparql.algebra.{Op, OpAsQuery, Transformer}
import org.apache.jena.sparql.algebra.op._
import org.apache.jena.sparql.core.{TriplePath, Var, BasicPattern}
import org.apache.jena.graph.{Node => JenaNode, Triple => JenaTriple}
import org.apache.jena.query.{Query => JenaQuery}
import org.apache.jena.sparql.expr._

import scala.collection.JavaConversions._


sealed trait EdgeDirection
case object Forward extends EdgeDirection
case object Backward extends EdgeDirection


class SparqlGraphCompiler[N, E](backend: SparqlBackend[N, E]) {

  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]
  type FilterT = Filter[N, E]
  type OpMerger = (Op, Op) => Op
  type Function2ExprFactory = (Expr, Expr) => Expr
  type OpFactory = (Option[Op]) => Op

  def compileNodeJoining(node: NodeT, op: Op) =
    compileNode(node, _ map {
      OpJoin.create(op, _)
    } getOrElse op)

  def compileFunction2Filter(compiledNode: JenaNode, otherNode: NodeT,
                             op: Op, exprFactory: Function2ExprFactory): Op =
  {
    val (compiledOtherNode, filteredOp) = compileNodeJoining(otherNode, op)

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

      case EqualsFilter(otherNode) =>
        compileFunction2Filter(node, otherNode, op,
          new E_Equals(_, _))

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

  def compileEdges(edges: Set[EdgeT], node: JenaNode, merge: OpMerger): Op =
    edges.map(compileEdge(node))
        .reduce(merge)

  def compileEdge(node: JenaNode)(edge: EdgeT): Op = {
    edge match {
      case OutEdge(label, target) =>
        compileEdgeLabel(label, node, target, Forward)

      case InEdge(source, label) =>
        compileEdgeLabel(label, node, source, Backward)

      case ConjunctionEdge(edges) =>
        compileEdges(edges, node, OpJoin.create)

      case DisjunctionEdge(edges) =>
        compileEdges(edges, node, OpUnion.create)
    }
  }

  def compileEdgeLabel(label: E, compiledNode: JenaNode,
                       otherNode: NodeT, direction: EdgeDirection): Op =
  {
    backend.compileEdgeLabel(label) match {
      case Left(property) =>
        val pattern = new BasicPattern()
        val patternOp = new OpBGP(pattern)
        val (compiledOtherNode, op) = compileNodeJoining(otherNode, patternOp)
        val triple = direction match {
          case Forward =>
            new JenaTriple(compiledNode, property, compiledOtherNode)

          case Backward =>
            new JenaTriple(compiledOtherNode, property, compiledNode)
        }
        pattern.add(triple)
        op

      case Right(path) =>
        // reference to op needed, so temp. initialize with null and fix up afterwards
        val pathOp = new OpPath(null)
        val (compiledOtherNode, op) = compileNodeJoining(otherNode, pathOp)
        val triplePath = direction match {
          case Forward =>
            new TriplePath(compiledNode, path, compiledOtherNode)

          case Backward =>
            new TriplePath(compiledOtherNode, path, compiledNode)
        }
        // use reflection as OpPath has no setter
        val triplePathField = pathOp.getClass.getDeclaredField("triplePath")
        triplePathField.setAccessible(true)
        triplePathField.set(pathOp, triplePath)

        op
    }
  }

  // TODO: unable to use TransformPathFlattern, as these won't be
  //       comparable to parsed compiled algerba of expected queries
  //       (due to PathCompiler allocating variables with a P prefixe)

  def transformations =
    List(new TransformMergeBGPs)

  def optimize(op: Op): Op =
    transformations.foldLeft(op)((op, transform) =>
      Transformer.transform(transform, op))

  def compileQuery(node: Node[N, E]): JenaQuery = {
    require(node.edge.isDefined,
      "root node needs to have edges")

    val (compiledNode, op) = compileNode(node, _.get)
    assert(compiledNode.isInstanceOf[Var],
      "root node needs to be compiled to a variable")

    val preparedOp = backend.prepareOp(op)
    val variable = compiledNode.asInstanceOf[Var]
    val variables = List(variable) ++
                    backend.additionalResultVariables(variable)
    val projection = new OpProject(preparedOp, variables)
    val distinct = new OpDistinct(projection)
    val optimized = optimize(distinct)

    val query = OpAsQuery.asQuery(optimized)
    query.setQuerySelectType()
    backend.prepareQuery(query)

    query
  }
}
