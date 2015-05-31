import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.ketilovre.jawn.stream.JawnStreamParser
import helpers.JsonGenerator
import jawn.AsyncParser
import jawn.ast.{JArray, JValue}
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import org.specs2.specification.AfterAll

class JawnStreamSpec extends Specification with ScalaCheck with AfterAll {

  implicit val ac = ActorSystem()

  implicit val ec = ac.dispatcher

  implicit val afm = ActorFlowMaterializer()

  implicit val ee = ExecutionEnv.fromExecutionContext(ec)

  implicit val arrayGenerator = JsonGenerator.arbitraryArrayOfObjects

  implicit val objectGenerator = JsonGenerator.arbitraryJsonObject

  implicit val ints = Arbitrary(Gen.choose(1, 1000))

  def afterAll() = {
    ac.shutdown()
  }

  "JawnStream" should {

    "SingleValue" should {

      "convert a source of json chunks to a source containing a single json value" ! prop { (json: JValue, n: Int) =>

        val parser = JawnStreamParser[JValue](AsyncParser.SingleValue)

        val strings = json.render().grouped(n)

        Source(() => strings)
          .via(parser.stringFlow)
          .runWith(Sink.head)
          .map(j => j mustEqual json)
          .await
      }.set(maxDiscardRatio = 10f)
    }

    "UnwrapArray" should {

      "convert a source of json chunks to a source of json objects" ! prop { (json: JArray, n: Int) =>

        val parser = JawnStreamParser[JValue](AsyncParser.UnwrapArray)

        val strings = json.render().grouped(n)

        Source(() => strings)
          .via(parser.stringFlow)
          .grouped(Int.MaxValue)
          .runWith(Sink.head)
          .map(j => JArray.fromSeq(j) mustEqual json)
          .await
      }.set(maxDiscardRatio = 10f)
    }

    "ValueStream" should {

      "convert a stream of whitespace-separated json to a source of json values" ! prop { (json: JArray, n: Int) =>

        val parser = JawnStreamParser[JValue](AsyncParser.ValueStream)

        val strings = json.vs.map(_.render()).flatMap(_.grouped(n))

        Source(() => strings.toIterator)
          .via(parser.stringFlow)
          .grouped(Int.MaxValue)
          .runWith(Sink.head)
          .map(j => JArray.fromSeq(j) mustEqual json)
          .await
      }.set(maxDiscardRatio = 10f)
    }
  }
}
