package com.turbolent.questionParser.ast

import com.turbolent.questionParser.Token


sealed trait Value

case class AndValue(values: Seq[Value]) extends Value
case class OrValue(values: Seq[Value]) extends Value
case class RelationshipValue(a: NamedValue, b: Value) extends Value
case class NamedValue(name: Seq[Token]) extends Value
case class Number(number: Seq[Token]) extends Value
case class NumberWithUnit(number: Seq[Token], unit: Seq[Token]) extends Value


