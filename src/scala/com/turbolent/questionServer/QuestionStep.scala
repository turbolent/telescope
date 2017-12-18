package com.turbolent.questionServer

import com.twitter.finagle.http.{Status, Request}
import com.twitter.util.Future


case class QuestionError(status: Status, content: AnyRef) extends Throwable


trait QuestionStep[T, R] extends ((Request, T, QuestionResponse) => Future[(R, QuestionResponse)]) {

  def compose[R2](next: QuestionStep[R, R2]): QuestionStep[T, R2] =
    new QuestionStep[T, R2] {
      override def apply(req: Request, input: T, response: QuestionResponse) =
        QuestionStep.this(req, input, response).flatMap {
          case (output, response2) =>
            next(req, output, response2)
        }
    }
}
