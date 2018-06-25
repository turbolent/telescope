package com.turbolent.wikidataOntology

import com.turbolent.questionParser.Token

object Tokens {

  def mkLemmaString(tokens: Seq[Token]): String =
    tokens.map(_.lemma).mkString(" ")

  def mkWordString(name: Seq[Token]): String =
    name.map(_.word).mkString(" ")

  def stripInitialDeterminer(name: Seq[Token]): Seq[Token] =
    name match {
      case Token(_, "DT", _) :: rest => rest
      case _                         => name
    }

  def splitName(name: Seq[Token]): (Seq[Token], Seq[Token]) =
    stripInitialDeterminer(name)
      .span(_.pennTag.startsWith("JJ"))

}
