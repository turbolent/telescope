package com.turbolent.questionParser.ast

import com.turbolent.questionParser.Token


sealed trait Query extends Product with Serializable

case class AndQuery(queries: Seq[Query]) extends Query
case class RelationshipQuery(a: Query, b: Query, token: Token) extends Query
case class NamedQuery(name: Seq[Token]) extends Query
case class QueryWithProperty(query: Query, property: Property) extends Query
