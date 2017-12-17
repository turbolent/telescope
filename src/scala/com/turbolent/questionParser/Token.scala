package com.turbolent.questionParser

case class Token(word: String, pennTag: String, lemma: String) {
  override def toString = s"${word}_${pennTag}_($lemma)"
}