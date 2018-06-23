package com.turbolent.questionCompiler.graph

import com.turbolent.questionCompiler.graph

sealed trait Edge[E, N] {

  type Node = graph.Node[N, E]
  type Edge = graph.Edge[E, N]

  def and(edge: Edge): ConjunctionEdge[E, N] =
    edge match {
      case ConjunctionEdge(edges) =>
        ConjunctionEdge(edges + this)
      case _ =>
        ConjunctionEdge(Set[Edge](this, edge))
    }

  def or(edge: Edge): DisjunctionEdge[E, N] =
    edge match {
      case DisjunctionEdge(edges) =>
        DisjunctionEdge(edges + this)
      case _ =>
        DisjunctionEdge(Set[Edge](this, edge))
    }
}

case class InEdge[E, N](source: Node[N, E], label: E) extends Edge[E, N]

case class OutEdge[E, N](label: E, target: Node[N, E]) extends Edge[E, N]

case class ConjunctionEdge[E, N](edges: Set[Edge[E, N]]) extends Edge[E, N] {

  override def and(edge: Edge): ConjunctionEdge[E, N] =
    edge match {
      case ConjunctionEdge(otherEdges) =>
        ConjunctionEdge(edges ++ otherEdges)
      case _ =>
        ConjunctionEdge(edges + edge)
    }
}

case class DisjunctionEdge[E, N](edges: Set[Edge[E, N]]) extends Edge[E, N] {

  override def or(edge: Edge): DisjunctionEdge[E, N] =
    edge match {
      case DisjunctionEdge(otherEdges) =>
        DisjunctionEdge(edges ++ otherEdges)
      case _ =>
        DisjunctionEdge(edges + edge)
    }
}
