import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.RouteTest
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import ketilovre.jawn.akka.http.Directive
import Directive._
import helpers.{JsonGenerator, Specs2Interface}
import jawn.AsyncParser
import jawn.ast.{JArray, JValue}
import ketilovre.jawn.akka.stream.JawnStreamParser
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class DirectiveSpec extends Specification with ScalaCheck with RouteTest with Directives with Specs2Interface {

  implicit val ac = ActorSystem()

  implicit val ec = ac.dispatcher

  implicit val afm = ActorMaterializer()

  implicit val arrayGenerator = JsonGenerator.arbitraryArrayOfObjects

  implicit val ints = Arbitrary(Gen.choose(1, 1000))

  override def afterAll() = {
    ac.shutdown()
    super.afterAll()
  }

  "extractJson" should {

    "extract the request body as a Source[J]" in prop { json: JArray =>

      val parser = JawnStreamParser[JValue](AsyncParser.UnwrapArray)

      val route = {
        post {
          extractJson(parser) { jsonSource =>
            val jsonResult = jsonSource.grouped(Int.MaxValue).runWith(Sink.head[Seq[JValue]])
            onSuccess(jsonResult) { jsonSeq =>
              complete(JArray.fromSeq(jsonSeq).render())
            }
          }
        }
      }

      Post("/", json.render()) ~> route ~> check {
        responseAs[String] mustEqual json.render()
      }
    }.set(maxDiscardRatio = 10f)
  }
}
