package com.turbolent.questionParser

import scala.util.parsing.input.Position


class TokensPosition(val index: Int, tokens: Seq[Token]) extends Position {

  private val separator = ", "

  override def column: Int =
    separate(tokens.take(index)).length +
    (if (index > 0) separator.length + 1 else 0)

  override def line = 1

  def separate(tokens: Seq[Token]): String =
    tokens.mkString(separator)

  override protected def lineContents: String =
    separate(tokens)

  override def toString(): String =
    (index + 1).toString
}
