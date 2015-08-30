package com.turbolent.questionParser

import com.turbolent.aptagger.Tagger
import com.turbolent.lemmatizer.{Lemmatizer, PartOfSpeech}
import com.turbolent.tokenizer.Tokenizer

import scala.collection.JavaConverters._

case class Token(word: String, pennTag: String)(implicit lemmatizer: Lemmatizer) {
  lazy val pos = PartOfSpeech.fromPennTreebankTag(pennTag)
  lazy val lemmas: Set[String] = {
    if (pos == null) Set()
    else lemmatizer.morphy(word.toLowerCase, pos).toSet
  }

  override def toString = s"${word}_$pennTag"
}

object Token {
  def tokensFromSentence(sentence: String)
                        (implicit tagger: Tagger, lemmatizer: Lemmatizer): Seq[Token] =
  {
    val words = Tokenizer.tokenize(sentence)
    val tags = tagger.tag(words).asScala
    (words.asScala, tags).zipped map {
      case (word, tag) => Token(word, tag)
    }
  }
}
