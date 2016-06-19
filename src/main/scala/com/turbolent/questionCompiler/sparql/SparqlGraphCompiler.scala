package com.turbolent.questionCompiler.sparql

import com.turbolent.questionCompiler.Environment
import com.turbolent.questionCompiler.graph._
import org.apache.jena.sparql.algebra.optimize.TransformMergeBGPs
import org.apache.jena.sparql.algebra.{Op, OpAsQuery, Transformer}
import org.apache.jena.sparql.algebra.op._
import org.apache.jena.sparql.core.{BasicPattern, TriplePath, Var}
import org.apache.jena.graph.{Node => JenaNode, Triple => JenaTriple}
import org.apache.jena.query.{SortCondition, Query => JenaQuery}
import org.apache.jena.sparql.expr._

import scala.collection.JavaConversions._


sealed trait EdgeDirection
case object Forward extends EdgeDirection
case object Backward extends EdgeDirection


class SparqlGraphCompiler[N, E, EnvT <: Environment[N, E]]
  (backend: SparqlBackend[N, E, EnvT], env: EnvT)
{

  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]
  type FilterT = Filter[N, E]
  type OpResultMerger = (OpResult, OpResult) => OpResult
  type Function2ExprFactory = (Expr, Expr) => Expr
  type OpResultFactory = (Option[OpResult]) => OpResult
  type OpResult = (Op, Seq[SortCondition])

  def compileNodeJoining(node: NodeT, opResult: OpResult, context: NodeCompilationContext) = {
    val (op, sortings) = opResult
    def join(opResult: OpResult): OpResult = {
      val (otherOp, otherSortings) = opResult
      val newOp = OpJoin.create(op, otherOp)
      (newOp, sortings ++ otherSortings)
    }
    def factory(optOpResult: Option[OpResult]) = optOpResult map join getOrElse opResult
    compileNode(node, factory, context)
  }

  def compileFunction2Filter(compiledNode: JenaNode, otherNode: NodeT,
                             opResult: OpResult, exprFactory: Function2ExprFactory): OpResult =
  {
    val (compiledOtherNode, (filteredOp, sortings)) =
      compileNodeJoining(otherNode, opResult,
        FilterNodeCompilationContext)

    val leftExpr =
      backend.prepareLeftFunctionExpression(new ExprVar(compiledNode), otherNode)

    val rightExpr =
      if (compiledOtherNode.isVariable)
        new ExprVar(compiledOtherNode)
      else
        NodeValue.makeNode(compiledOtherNode)


    val expr = exprFactory(leftExpr, rightExpr)
    val filterOp = OpFilter.filter(expr, filteredOp)
    (filterOp, sortings)
  }

  def compileFilter(node: JenaNode, opResult: OpResult)(filter: FilterT): OpResult = {
    def compileComparison(otherNode: NodeT, exprFactory: Function2ExprFactory) =
      compileFunction2Filter(node, otherNode, opResult, exprFactory)

    filter match {
      case EqualsFilter(otherNode) =>
        compileComparison(otherNode, new E_Equals(_, _))

      case LessThanFilter(otherNode) =>
        compileComparison(otherNode, new E_LessThan(_, _))

      case GreaterThanFilter(otherNode) =>
        compileComparison(otherNode, new E_GreaterThan(_, _))

      case ConjunctionFilter(filters) =>
        filters.foldLeft(opResult)(compileFilter(node, _)(_))
    }
  }

  def compileOrder(order: Order): Int = order match {
    case Ascending => JenaQuery.ORDER_ASCENDING
    case Descending => JenaQuery.ORDER_DESCENDING
  }

  def compileNode(node: NodeT, opFactory: OpResultFactory,
                  context: NodeCompilationContext): (JenaNode, OpResult) =
  {
    val expandedNode = backend.expandNode(node, context, env)
    val compiledNode = backend.compileNodeLabel(expandedNode.label, env)
    val optEdgeOp = expandedNode.edge.map(compileEdge(compiledNode))
    val filteredOp = opFactory(optEdgeOp)
    val sorting = expandedNode.order
      .filter(_ => compiledNode.isVariable)
      .map(compileOrder)
      .map(new SortCondition(compiledNode.asInstanceOf[Var], _))

    val (op, filterSortings) = expandedNode.filter map {
      compileFilter(compiledNode, filteredOp)
    } getOrElse filteredOp

    (compiledNode, (op, filterSortings ++ sorting))
  }

  def compileEdges(edges: Set[EdgeT], node: JenaNode, merge: OpResultMerger): OpResult =
    edges.map(compileEdge(node))
        .reduce(merge)

  def compileEdge(node: JenaNode)(edge: EdgeT): OpResult = {
    edge match {
      case OutEdge(label, target) =>
        compileEdgeLabel(label, node, target, Forward)

      case InEdge(source, label) =>
        compileEdgeLabel(label, node, source, Backward)

      case ConjunctionEdge(edges) =>
        compileEdges(edges, node, { case ((op1, sortings1), (op2, sortings2)) =>
          val op = OpJoin.create(op1, op2)
          val sortings = sortings1 ++ sortings2
          (op, sortings)
        })

      case DisjunctionEdge(edges) =>
        compileEdges(edges, node, { case ((op1, sortings1), (op2, sortings2)) =>
          val op = OpUnion.create(op1, op2)
          val sortings = sortings1 ++ sortings2
          (op, sortings)
        })
    }
  }

  val triplePathField = {
    val field = classOf[OpPath].getDeclaredField("triplePath")
    field.setAccessible(true)
    field
  }

  def compileEdgeLabel(label: E, compiledNode: JenaNode,
                       otherNode: NodeT, direction: EdgeDirection): OpResult =
  {
    backend.compileEdgeLabel(label, env) match {
      case Left(property) =>
        val pattern = new BasicPattern()
        val patternOp = new OpBGP(pattern)
        val (compiledOtherNode, (op, sortings)) =
          compileNodeJoining(otherNode, (patternOp, Seq()),
            TripleNodeCompilationContext)
        val triple = direction match {
          case Forward =>
            new JenaTriple(compiledNode, property, compiledOtherNode)

          case Backward =>
            new JenaTriple(compiledOtherNode, property, compiledNode)
        }
        pattern.add(triple)
        (op, sortings)

      case Right(path) =>
        // reference to op needed, so temp. initialize with null and fix up afterwards
        val pathOp = new OpPath(null)
        val (compiledOtherNode, (op, sortings)) =
          compileNodeJoining(otherNode, (pathOp, Seq()),
            TripleNodeCompilationContext)
        val triplePath = direction match {
          case Forward =>
            new TriplePath(compiledNode, path, compiledOtherNode)

          case Backward =>
            new TriplePath(compiledOtherNode, path, compiledNode)
        }

        // use reflection as OpPath has no setter
        triplePathField.set(pathOp, triplePath)

        (op, sortings)
    }
  }

  // TODO: unable to use TransformPathFlatten, as these won't be
  //       comparable to parsed compiled algebra of expected queries
  //       (due to PathCompiler allocating variables with a P prefix)

  def transformations =
    List(new TransformMergeBGPs)

  def optimize(op: Op): Op =
    transformations.foldLeft(op)((op, transform) =>
      Transformer.transform(transform, op))

  def compileQuery(node: Node[N, E]): JenaQuery = {
    require(node.edge.isDefined,
      "root node needs to have edges")

    val (compiledNode, (op, sortings)) =
      compileNode(node, _.get,
        TripleNodeCompilationContext)
    assert(compiledNode.isInstanceOf[Var],
      "root node needs to be compiled to a variable")

    val preparedOp = backend.prepareOp(op, env)
    val variable = compiledNode.asInstanceOf[Var]
    val variables = List(variable) ++
                    backend.additionalResultVariables(variable, env)

    val ordered = new OpOrder(preparedOp, sortings)

    val projection = new OpProject(ordered, variables)
    val distinct = new OpDistinct(projection)

    val optimized = optimize(distinct)

    val query = OpAsQuery.asQuery(optimized)
    query.setQuerySelectType()
    backend.prepareQuery(query, env)

    query
  }
}