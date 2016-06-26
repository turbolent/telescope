package com.turbolent.questionParser.ast

import com.turbolent.questionParser.Token


sealed trait Query

sealed trait SubQuery extends Query

case class AndQuery(queries: Seq[Query]) extends SubQuery
case class RelationshipQuery(a: SubQuery, b: Query, token: Token) extends SubQuery
case class NamedQuery(name: Seq[Token]) extends SubQuery
case class QueryWithProperty(query: Query, property: Property) extends Query
