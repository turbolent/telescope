package com.turbolent.spacyThrift

import com.twitter.finagle.Thrift
import com.twitter.finagle.Thrift.param.Framed
import com.twitter.util.Future


class SpacyThriftClient(hostname: String, port: Int) {

  private val client = Thrift.client
    .configured(Framed(false))
    .withSessionQualifier.noFailFast
    .withSessionQualifier.noFailureAccrual
    .build[SpacyThriftService.MethodPerEndpoint](s"$hostname:$port")

  def tag(sentence: String): Future[Seq[Token]] =
    client.tag(sentence)
}