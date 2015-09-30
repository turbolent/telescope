package com.turbolent.questionCompiler.graph


case class Node[N, E](label: N,
                      edge: Option[Edge[E, N]] = None,
                      filter: Option[Filter[N, E]] = None,
                      aggregate: Option[AggregateFunction] = None)
{
  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]
  type FilterT = Filter[N, E]

  def filter(filter: FilterT): NodeT =
    copy(filter = filter.combine(this.filter))

  def out(label: E, target: NodeT) =
    connect(OutEdge(label, target))

  def in(source: NodeT, label: E) =
    connect(InEdge(source, label))

  def connect(edge: EdgeT) =
    copy(edge = edge.combine(this.edge))

  def or(edges: EdgeT*) =
    if (edges.isEmpty) this
    else connect(DisjunctionEdge(edges))

  def and(edges: EdgeT*) =
    if (edges.isEmpty) this
    else connect(ConjunctionEdge(edges))

  def aggregate(aggregate: AggregateFunction): NodeT =
    copy(aggregate = Some(aggregate))
}
