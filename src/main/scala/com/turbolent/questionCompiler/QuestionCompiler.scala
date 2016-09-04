package com.turbolent.questionCompiler

import graph.{Node, Edge, ConjunctionEdge, DisjunctionEdge}

import com.turbolent.questionParser.{ast, Token}


class QuestionCompiler[N, E, Env <: Environment[N, E]](ontology: Ontology[N, E, Env], env: Env) {

  type Node = graph.Node[N, E]
  type Edge = graph.Edge[E, N]
  type NodeFactory = (Node, Seq[Token]) => Node
  type EdgeContextFactory = (Subject) => EdgeContext
  type EdgeFactory = (Node, EdgeContextFactory) => Edge

  val identityNodeFactory: NodeFactory =
    (node, name) => node

  def compileQuestion(question: ast.Question): Seq[Node] =
    question match {
      case ast.PersonListQuestion(properties) =>
        val node = env.newNode()
            .and(ontology.makePersonEdge(env))
            .and(compileProperty(properties, PersonSubject))
        Seq(node)

      case ast.ThingListQuestion(properties) =>
        val node = env.newNode()
            .and(compileProperty(properties, ThingSubject))
        Seq(node)

      case ast.ListQuestion(query) =>
        compileQuery(query, identityNodeFactory)
    }

  def compileQuery(query: ast.Query, nodeFactory: NodeFactory): Seq[Node] =
    query match {
      case ast.QueryWithProperty(nestedQuery, property) =>
        val nodeFactory: NodeFactory = (node, name) =>
          node.and(compileProperty(property, NamedSubject(name)))
        compileQuery(nestedQuery, nodeFactory)

      case ast.NamedQuery(name) =>
        val node = ontology.makeValueNode(name, Nil, env)
        Seq(nodeFactory(node, name))

      case ast.AndQuery(queries) =>
        queries flatMap { compileQuery(_, nodeFactory) }

      case ast.RelationshipQuery(first, second, _) =>
        val nodes = compileQuery(second, nodeFactory)
        compileRelationshipSubquery(first, nodes)
    }

  def compileRelationshipSubquery(query: ast.Query, nodes: Seq[Node]): Seq[Node] =
    query match {
      case ast.NamedQuery(name) =>
        nodes map { node =>
          val edge = ontology.makeRelationshipEdge(name, node, env)
          env.newNode().and(edge)
        }

      case ast.AndQuery(queries) =>
        queries flatMap { compileRelationshipSubquery(_, nodes) }

      case ast.RelationshipQuery(first, second, _) =>
        val secondNodes = compileRelationshipSubquery(second, nodes)
        compileRelationshipSubquery(first, secondNodes)

      case ast.QueryWithProperty(_, _) => ???
    }

  def compileProperty(property: ast.Property, subject: Subject): Edge =
    property match {
      case ast.NamedProperty(name) =>
        ontology.makeNamedPropertyEdge(name, env.newNode(), subject, env)

      case ast.PropertyWithFilter(name, filter) =>
        compileFilter(filter, (node, contextFactory) => {
          val context = contextFactory(subject)
          filter match {
            case _: ast.FilterWithComparativeModifier =>
              ontology.makeComparativePropertyEdge(name, node, context, env)

            case _ =>
              ontology.makeValuePropertyEdge(name, node, context, env)
          }
        })

      case ast.InversePropertyWithFilter(name, filter) =>
        compileFilter(filter, (node, contextFactory) =>
          ontology.makeInversePropertyEdge(name, node,
            contextFactory(subject), env))

      case ast.AdjectivePropertyWithFilter(name, filter) =>
        compileFilter(filter, (node, contextFactory) =>
          ontology.makeAdjectivePropertyEdge(name, node,
            contextFactory(subject), env))

      case ast.AndProperty(properties) =>
        ConjunctionEdge(properties.map(compileProperty(_, subject)).toSet)

      case ast.OrProperty(properties) =>
        DisjunctionEdge(properties.map(compileProperty(_, subject)).toSet)
    }


  def compileFilter(filter: ast.Filter, edgeFactory: EdgeFactory): Edge =
    filter match {
      case ast.FilterWithModifier(modifier, value) =>
        compileValue(value, modifier, edgeFactory)

      case ast.FilterWithComparativeModifier(modifier, value) =>
        compileValue(value, modifier, edgeFactory)

      case ast.PlainFilter(value) =>
        compileValue(value, Nil, edgeFactory)

      case ast.AndFilter(filters) =>
        ConjunctionEdge(filters.map(compileFilter(_, edgeFactory)).toSet)

      case ast.OrFilter(filters) =>
        DisjunctionEdge(filters.map(compileFilter(_, edgeFactory)).toSet)
    }

  def compileValue(value: ast.Value, filter: Seq[Token], edgeFactory: EdgeFactory): Edge =
    value match {
      case ast.NamedValue(name) =>
        val node = ontology.makeValueNode(name, filter, env)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, name, Nil,
            valueIsNumber = false))

      case ast.NumberWithUnit(number, unit) =>
        val node = ontology.makeNumberNode(number, unit, filter, env)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, number, unit,
            valueIsNumber = true))

      case ast.Number(number) =>
        val node = ontology.makeNumberNode(number, Nil, filter, env)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, number, Nil,
            valueIsNumber = true))

      case ast.OrValue(values) =>
        DisjunctionEdge(values.map(compileValue(_, filter, edgeFactory)).toSet)

      case ast.AndValue(values) =>
        ConjunctionEdge(values.map(compileValue(_, filter, edgeFactory)).toSet)

      case ast.RelationshipValue(ast.NamedValue(name), second) =>
        val secondEdgeFactory: EdgeFactory =
          (node, contextFactory) =>
            ontology.makeRelationshipEdge(name, node, env)
        val edge = compileValue(second, filter, secondEdgeFactory)
        val node = env.newNode().and(edge)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, name, Nil,
            valueIsNumber = false))
    }
}