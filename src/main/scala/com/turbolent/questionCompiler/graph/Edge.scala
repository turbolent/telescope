package com.turbolent.questionCompiler.graph


trait Edge[E, N] {

  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]

  def combine(edge: Option[EdgeT]): Some[EdgeT] =
    Some(edge map {
      case ConjunctionEdge(current) =>
        ConjunctionEdge(current :+ this)
      case current =>
        ConjunctionEdge(Seq(current, this))
    } getOrElse this)

  def and(edges: EdgeT*) =
    if (edges.isEmpty) this
    else ConjunctionEdge(edges).combine(Some(this)).get
}

case class InEdge[E, N](source: Node[N, E], label: E) extends Edge[E, N]

case class OutEdge[E, N](label: E, target: Node[N, E]) extends Edge[E, N]


case class ConjunctionEdge[E, N](edges: Seq[Edge[E, N]]) extends Edge[E, N] {
  override def combine(edge: Option[EdgeT]): Some[EdgeT] =
    Some(edge map {
      case ConjunctionEdge(current) =>
        ConjunctionEdge(current ++ this.edges)
      case current =>
        ConjunctionEdge(current +: this.edges)
    } getOrElse this)
}

case class DisjunctionEdge[E, N](edges: Seq[Edge[E, N]]) extends Edge[E, N]

