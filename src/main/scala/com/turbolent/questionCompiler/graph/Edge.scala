package com.turbolent.questionCompiler.graph


trait Edge[E, N] {

  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]

  def and(edge: EdgeT) =
    edge match {
      case ConjunctionEdge(edges) =>
        ConjunctionEdge(this +: edges)
      case _ =>
        ConjunctionEdge(Seq(this, edge))
    }

  def or(edge: EdgeT) =
    edge match {
      case DisjunctionEdge(edges) =>
        DisjunctionEdge(this +: edges)
      case _ =>
        DisjunctionEdge(Seq(this, edge))
    }
}

case class InEdge[E, N](source: Node[N, E], label: E) extends Edge[E, N]

case class OutEdge[E, N](label: E, target: Node[N, E]) extends Edge[E, N]


case class ConjunctionEdge[E, N](edges: Seq[Edge[E, N]]) extends Edge[E, N] {
  override def and(edge: EdgeT) =
    edge match {
      case ConjunctionEdge(otherEdges) =>
        ConjunctionEdge(this.edges ++ otherEdges)
      case _ =>
        ConjunctionEdge(this.edges :+ edge)
    }
}

case class DisjunctionEdge[E, N](edges: Seq[Edge[E, N]]) extends Edge[E, N] {
  override def or(edge: EdgeT) =
    edge match {
      case DisjunctionEdge(otherEdges) =>
        DisjunctionEdge(this.edges ++ otherEdges)
      case _ =>
        DisjunctionEdge(this.edges :+ edge)
    }
}

