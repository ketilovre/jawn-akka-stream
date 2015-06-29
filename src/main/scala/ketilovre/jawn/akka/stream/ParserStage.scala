package ketilovre.jawn.akka.stream

import akka.stream.stage.{Context, PushPullStage}
import jawn.{AsyncParser, Facade}

class ParserStage[J](mode: AsyncParser.Mode)(implicit facade: Facade[J]) extends PushPullStage[String, Seq[J]] {

  private val parser = jawn.Parser.async[J](mode)

  def onPush(chunk: String, ctx: Context[Seq[J]]) = {
    parser.absorb(chunk) match {
      case Right(json) => if (json.nonEmpty) ctx.push(json) else ctx.pull()
      case Left(error) => ctx.fail(error)
    }
  }

  def onPull(ctx: Context[Seq[J]]) = {
    if (ctx.isFinishing) {
      parser.finish() match {
        case Right(json) => if (json.nonEmpty) ctx.pushAndFinish(json) else ctx.finish()
        case Left(error) => ctx.fail(error)
      }
    } else {
      ctx.pull()
    }
  }

  override def onUpstreamFinish(ctx: Context[Seq[J]]) = {
    ctx.absorbTermination()
  }
}
