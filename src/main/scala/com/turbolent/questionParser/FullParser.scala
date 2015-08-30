package com.turbolent.questionParser

object FullParser extends BaseParser {
  lazy val root = ListParser.Question
}
