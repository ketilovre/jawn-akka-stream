package ketilovre.jawn.akka.http

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, UnsupportedRequestContentTypeRejection}
import akka.stream.scaladsl._
import ketilovre.jawn.akka.stream.JawnStreamParser

trait Directives {

  def extractJson[J](parser: JawnStreamParser[J]): Directive1[Source[J, Any]] = {
    extract(ctx => ctx.request.entity.dataBytes.via(parser.byteStringFlow))
  }

  def expectJson[J](parser: JawnStreamParser[J]): Directive1[Source[J, Any]] = {
    val filter = extract(ctx => ctx.request.entity.contentType()).flatMap[Unit] {
      case `application/json` => pass
      case _                  => reject(UnsupportedRequestContentTypeRejection(Set(`application/json`)))
    }
    (filter & cancelRejections(classOf[UnsupportedRequestContentTypeRejection])) & extractJson(parser)
  }
}

object Directives extends Directives
