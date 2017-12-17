package com.turbolent.questionServer

import com.turbolent.questionParser.Token
import com.twitter.util.Future
import com.turbolent.spacyThrift.SpacyThriftClient
import com.turbolent.spacyThrift.{Token => SpacyToken}

class SpacyTagger(client: SpacyThriftClient) extends Tagger {

  override def tag(sentence: String): Future[Seq[Token]] =
    client.tag(sentence) map { _ map {
        case SpacyToken(word, tag, lemma) =>
          Token(word, tag, lemma)
      }
    }
}
