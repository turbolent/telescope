package com.turbolent.questionCompiler.graph

import com.turbolent.questionCompiler.graph

sealed trait Filter[N, E] {

  type Filter = graph.Filter[N, E]

  def and(filter: Filter): ConjunctionFilter[N, E] =
    filter match {
      case ConjunctionFilter(filters) =>
        ConjunctionFilter(this +: filters)
      case _ =>
        ConjunctionFilter(Seq(this, filter))
    }
}

case class ConjunctionFilter[N, E](filters: Seq[Filter[N, E]])
    extends Filter[N, E] {

  override def and(filter: Filter): ConjunctionFilter[N, E] =
    filter match {
      case ConjunctionFilter(otherFilters) =>
        ConjunctionFilter(filters ++ otherFilters)
      case _ =>
        ConjunctionFilter(filters :+ filter)
    }
}

case class EqualsFilter[N, E](node: Node[N, E]) extends Filter[N, E]
case class LessThanFilter[N, E](node: Node[N, E]) extends Filter[N, E]
case class GreaterThanFilter[N, E](node: Node[N, E]) extends Filter[N, E]
