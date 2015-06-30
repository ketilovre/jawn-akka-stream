import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.{Directive1, Directives, Route, UnsupportedRequestContentTypeRejection}
import akka.http.scaladsl.testkit.RouteTest
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import helpers.{JsonGenerator, Specs2Interface}
import jawn.AsyncParser
import jawn.ast.{JArray, JValue}
import ketilovre.jawn.akka.http.Directives._
import ketilovre.jawn.akka.stream.JawnStreamParser
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class DirectivesSpec extends Specification with ScalaCheck with RouteTest with Directives with Specs2Interface {

  implicit val ac = ActorSystem()

  implicit val ec = ac.dispatcher

  implicit val afm = ActorMaterializer()

  implicit val arrayGenerator = JsonGenerator.arbitraryArrayOfObjects

  implicit val ints = Arbitrary(Gen.choose(1, 1000))

  override def afterAll() = {
    ac.shutdown()
    super.afterAll()
  }

  val parser = JawnStreamParser[JValue](AsyncParser.UnwrapArray)

  def route(jsonDirective: Directive1[Source[JValue, Any]]): Route = {
    post {
      jsonDirective { jsonSource =>
        val jsonResult = jsonSource.grouped(Int.MaxValue).runWith(Sink.head[Seq[JValue]])
        onSuccess(jsonResult) { jsonSeq =>
          complete(JArray.fromSeq(jsonSeq).render())
        }
      }
    }
  }

  "extractJson" should {

    "extract the request body as a Source[J]" in prop { json: JArray =>

      Post("/", json.render()) ~> route(extractJson(parser)) ~> check {
        responseAs[String] mustEqual json.render()
      }
    }.set(maxDiscardRatio = 10f)
  }

  "expectJson" should {

    "reject requests with a non-`application/json` content type" in {

      Post("/", "text!") ~> route(expectJson(parser)) ~> check {
        rejection must beAnInstanceOf[UnsupportedRequestContentTypeRejection]
      }
    }

    "accept requests with an `application/json` content type" in prop { json: JArray =>

      val entity = HttpEntity(`application/json`, json.render())

      Post("/", entity) ~> route(expectJson(parser)) ~> check {
        handled must beTrue
        responseAs[String] mustEqual json.render()
      }
    }.set(maxDiscardRatio = 10f)
  }
}
