package com.turbolent.questionCompiler.graph


sealed trait AggregateFunction

case object Avg extends AggregateFunction
case object Sum extends AggregateFunction
case object Min extends AggregateFunction
case object Max extends AggregateFunction
case object Count extends AggregateFunction