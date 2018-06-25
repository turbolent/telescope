package com.turbolent.questionParser.ast

import com.turbolent.questionParser.Token

sealed trait Property extends Product with Serializable

case class AndProperty(properties: Seq[Property]) extends Property
case class OrProperty(properties: Seq[Property]) extends Property
case class NamedProperty(name: Seq[Token]) extends Property
case class PropertyWithFilter(name: Seq[Token], filter: Filter) extends Property
case class InversePropertyWithFilter(name: Seq[Token], filter: Filter) extends Property
case class AdjectivePropertyWithFilter(name: Seq[Token], filter: Filter) extends Property
