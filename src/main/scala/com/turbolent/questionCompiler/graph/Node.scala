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
    copy(filter = this.filter map { _.and(filter) } orElse Some(filter))

  def out(label: E, target: NodeT) =
    and(OutEdge(label, target))

  def in(source: NodeT, label: E) =
    and(InEdge(source, label))

  def and(edge: EdgeT) =
    copy(edge = this.edge map { _.and(edge) } orElse Some(edge))

  def or(edge: EdgeT) =
    copy(edge = this.edge map { _.or(edge) } orElse Some(edge))

  def aggregate(aggregate: AggregateFunction): NodeT =
    copy(aggregate = Some(aggregate))
}
