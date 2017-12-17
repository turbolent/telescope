package spacyThrift.client

import com.twitter.finagle.Thrift
import com.twitter.finagle.Thrift.param.Framed
import com.twitter.util.Future
import spacyThrift.SpacyThrift.FinagledClient
import spacyThrift.Token


class SpacyThriftClient(hostname: String, port: Int) {

  private val client = {
    val serviceFactory =
      Thrift.client.configured(Framed(false))
        .newClient(s"$hostname:$port")
    new FinagledClient(serviceFactory.toService)
  }

  def tag(sentence: String): Future[Seq[Token]] =
    client.tag(sentence)
}