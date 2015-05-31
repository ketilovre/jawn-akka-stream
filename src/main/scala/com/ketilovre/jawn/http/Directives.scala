package com.ketilovre.jawn.http

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import com.ketilovre.jawn.stream.JawnStreamParser

trait Directives {

  def extractJson[J](parser: JawnStreamParser[J]): Directive1[Source[J, Any]] = {
    textract(ctx => Tuple1(ctx.request.entity.dataBytes.via(parser.byteStringFlow)))
  }
}

object Directives extends Directives
