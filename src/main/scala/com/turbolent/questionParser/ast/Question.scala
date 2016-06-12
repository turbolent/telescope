package com.turbolent.questionParser.ast


sealed trait Question

case class ListQuestion(query: Query) extends Question
case class PersonListQuestion(properties: Property) extends Question
case class ThingListQuestion(properties: Property) extends Question

