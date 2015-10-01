package com.turbolent.questionCompiler

import graph.{Node, Edge}

import com.turbolent.questionParser.Token


trait Ontology[N, E, EnvT <: Environment[N, E]] {

  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]

  def makePersonEdge(env: EnvT): EdgeT

  def makeNamedPropertyEdge(name: Seq[Token], node: NodeT,
                            subject: Subject, env: EnvT): EdgeT

  def makeInversePropertyEdge(name: Seq[Token], node: NodeT,
                              context: EdgeContext, env: EnvT): EdgeT

  def makeAdjectivePropertyEdge(name: Seq[Token], node: NodeT,
                                context: EdgeContext, env: EnvT): EdgeT

  def makeComparativePropertyEdge(name: Seq[Token], node: NodeT,
                                  context: EdgeContext, env: EnvT): EdgeT

  def makeValuePropertyEdge(name: Seq[Token], node: NodeT,
                            context: EdgeContext, env: EnvT): EdgeT

  def makeRelationshipEdge(name: Seq[Token], node: NodeT, env: EnvT): EdgeT

  def makeValueNode(name: Seq[Token], filter: Seq[Token], env: EnvT): NodeT

  def makeNumberNode(number: Seq[Token], unit: Seq[Token], filter: Seq[Token], env: EnvT): NodeT

}
