package com.turbolent.questionParser

import com.turbolent.questionParser.ast.Question

object FullParser extends BaseParser {
  lazy val root: ListParser.PackratParser[Question] = ListParser.Question
}
