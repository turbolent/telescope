package com.turbolent.questionServer

import com.twitter.util.{Future, Throw, Return}
import org.scalatest.concurrent.Futures


trait TwitterFutures extends Futures {

  import scala.language.implicitConversions

  implicit def convertTwitterFuture[T](future: Future[T]): FutureConcept[T] =
    new FutureConcept[T] {
      override def eitherValue: Option[Either[Throwable, T]] = {
        future.poll.map {
          case Return(o) => Right(o)
          case Throw(e) => Left(e)
        }
      }
      override def isCanceled: Boolean = false
      override def isExpired: Boolean = false
    }
}
