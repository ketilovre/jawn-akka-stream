package ketilovre.jawn.akka.http

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl._
import ketilovre.jawn.akka.stream.JawnStreamParser

trait Directive {

  def extractJson[J](parser: JawnStreamParser[J]): Directive1[Source[J, Any]] = {
    extract(ctx => ctx.request.entity.dataBytes.via(parser.byteStringFlow))
  }
}

object Directive extends Directive
