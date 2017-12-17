package com.turbolent.questionServer
import com.turbolent.questionParser.Token
import com.twitter.util.Future
import spacyThrift.client.SpacyThriftClient

class SpacyTagger(client: SpacyThriftClient) extends Tagger {

  override def tag(sentence: String): Future[Seq[Token]] =
    client.tag(sentence) map { _ map {
        case spacyThrift.Token(word, tag, lemma) =>
          Token(word, tag, lemma)
      }
    }
}
