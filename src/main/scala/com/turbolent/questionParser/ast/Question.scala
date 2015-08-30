package com.turbolent.questionParser.ast


abstract class Question

case class ListQuestion(query: Query) extends Question
case class PersonListQuestion(properties: Property) extends Question
case class ThingListQuestion(properties: Property) extends Question

