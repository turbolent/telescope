package com.turbolent.questionServer

import com.twitter.finagle.httpx.{Status, Request}
import com.twitter.util.Future


case class ParseError(status: Status, content: AnyRef) extends Throwable


trait ParseStep[T, R] extends ((Request, T, ParseResponse) => Future[(R, ParseResponse)]) {

  def compose[R2](next: ParseStep[R, R2]) =
    new ParseStep[T, R2] {
      override def apply(req: Request, input: T, response: ParseResponse) =
        ParseStep.this(req, input, response).flatMap {
          case (output, response2) =>
            next(req, output, response2)
        }
    }
}
