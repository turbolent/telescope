package com.turbolent.questionCompiler.graph


trait Filter[N, E] {

  type FilterT = Filter[N, E]

  def and(filter: FilterT) =
    filter match {
      case ConjunctionFilter(filters) =>
        ConjunctionFilter(this +: filters)
      case _ =>
        ConjunctionFilter(Seq(this, filter))
    }
}

case class ConjunctionFilter[N, E](filters: Seq[Filter[N, E]]) extends Filter[N, E] {
  override def and(filter: FilterT) =
    filter match {
      case ConjunctionFilter(otherFilters) =>
        ConjunctionFilter(this.filters ++ otherFilters)
      case _ =>
        ConjunctionFilter(this.filters :+ filter)
    }
}

case class LessThanFilter[N, E](node: Node[N, E]) extends Filter[N, E]

case class GreaterThanFilter[N, E](node: Node[N, E]) extends Filter[N, E]