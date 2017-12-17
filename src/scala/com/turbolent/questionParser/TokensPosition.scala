package com.turbolent.questionParser

import scala.util.parsing.input.Position


class TokensPosition(val index: Int, tokens: Seq[Token]) extends Position {

  private val separator = ", "

  override def column =
    separate(tokens.take(index)).length +
    (if (index > 0) separator.length + 1 else 0)

  override def line = 1

  def separate(tokens: Seq[Token]) =
    tokens.mkString(separator)

  override protected def lineContents =
    separate(tokens)

  override def toString() =
    (index + 1).toString
}
