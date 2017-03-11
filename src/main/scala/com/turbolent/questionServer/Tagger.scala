package com.turbolent.questionServer

import com.turbolent.questionParser.Token
import com.twitter.util.Future

trait Tagger {
  def tag(sentence: String): Future[Seq[Token]]
}
