package com.turbolent.questionCompiler.graph


trait Filter[N, E] {

  type FilterT = Filter[N, E]

  def combine(filter: Option[FilterT]): Some[FilterT] =
    Some(filter map {
      case ConjunctionFilter(current) =>
        ConjunctionFilter(current :+ this)
      case current =>
        ConjunctionFilter(Seq(current, this))
    } getOrElse this)
}

case class ConjunctionFilter[N, E](filters: Seq[Filter[N, E]]) extends Filter[N, E] {
  override def combine(filter: Option[FilterT]): Some[FilterT] =
    Some(filter map {
      case ConjunctionFilter(current) =>
        ConjunctionFilter(current ++ this.filters)
      case current =>
        ConjunctionFilter(current +: this.filters)
    } getOrElse this)
}

case class LessThanFilter[N, E](node: Node[N, E]) extends Filter[N, E]

case class GreaterThanFilter[N, E](node: Node[N, E]) extends Filter[N, E]