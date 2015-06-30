package ketilovre.jawn.akka.stream

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import jawn.{AsyncParser, Facade}

class JawnStreamParser[J](mode: AsyncParser.Mode)(implicit facade: Facade[J]) {

  private def parserStage = new ParserStage[J](mode)

  val stringFlow: Flow[String, J, Unit] = {
    Flow[String].transform(() => parserStage).mapConcat(_.toList)
  }

  val byteStringFlow: Flow[ByteString, J, Unit] = {
    Flow[ByteString].map(_.utf8String).via(stringFlow)
  }
}

object JawnStreamParser {

  def apply[J](mode: AsyncParser.Mode)(implicit facade: Facade[J]): JawnStreamParser[J] = {
    new JawnStreamParser[J](mode)
  }
}
