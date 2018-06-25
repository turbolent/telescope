package com.turbolent.questionCompiler.graph

import com.turbolent.questionCompiler.graph

case class Node[N, E](label: N,
                      edge: Option[Edge[E, N]] = None,
                      filter: Option[Filter[N, E]] = None,
                      aggregates: Seq[AggregateFunction] = Nil,
                      order: Option[Order] = None) {

  type Node = graph.Node[N, E]
  type Edge = graph.Edge[E, N]
  type Filter = graph.Filter[N, E]

  def filter(filter: Filter): Node =
    copy(filter = this.filter.map { _.and(filter) }.orElse(Some(filter)))

  def out(label: E, target: Node): Node =
    and(OutEdge(label, target))

  def in(source: Node, label: E): Node =
    and(InEdge(source, label))

  def and(edge: Edge): Node =
    copy(edge = this.edge.map { _.and(edge) }.orElse(Some(edge)))

  def or(edge: Edge): Node =
    copy(edge = this.edge.map { _.or(edge) }.orElse(Some(edge)))

  def aggregate(aggregate: AggregateFunction): Node =
    copy(aggregates = this.aggregates :+ aggregate)

  def order(order: Order): Node =
    copy(order = Some(order))
}
