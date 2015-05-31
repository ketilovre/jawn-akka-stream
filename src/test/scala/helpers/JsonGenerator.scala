package helpers

import jawn.ast._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck._

import scala.util.Random

object JsonGenerator {

  implicit def arbitraryArrayOfObjects: Arbitrary[JArray] = Arbitrary {
    for {
      n <- choose(10, 20)
      objects <- listOfN(n, jsonObject(1))
    } yield {
      JArray.fromSeq(objects)
    }
  }

  implicit def arbitraryJsonObject: Arbitrary[JValue] = Arbitrary {
    jsonObject(Random.nextInt(8) + 1)
  }

  private def jsonType(depth: Int): Gen[JValue] = oneOf(jsonArray(depth), jsonObject(depth))

  private def jsonArray(depth: Int): Gen[JArray] = for {
    n    <- choose(1, 4)
    vals <- oneOf(listOfN(n, jsonObject(depth)), primitiveArray(n))
  } yield JArray.fromSeq(vals)

  private def jsonObject(depth: Int): Gen[JObject] = for {
    n    <- choose(1, 4)
    ks   <- keys(n)
    vals <- values(n, depth)
  } yield {
      JObject.fromSeq(Map(ks zip vals:_*).toList)
    }

  private def keys(n: Int) = listOfN(n, alphaStr suchThat(_.nonEmpty))

  private def values(n: Int, depth: Int) = listOfN(n, value(depth))

  private def value(depth: Int) = {
    if (depth < 1) {
      primitive
    } else {
      oneOf(jsonType(depth - 1), primitive)
    }
  }

  private def primitive: Gen[JValue] = {
    oneOf(
      alphaStr.map(JString(_)),
      arbitrary[Long].map(LongNum),
      arbitrary[Double].map(DoubleNum),
      arbitrary[Boolean].map(JBool(_))
    )
  }

  private def primitiveArray(length: Int): Gen[List[JValue]] = {
    oneOf(
      listOfN(length, alphaStr.map(JString(_))),
      listOfN(length, arbitrary[Long].map(LongNum)),
      listOfN(length, arbitrary[Double].map(DoubleNum)),
      listOfN(length, arbitrary[Boolean].map(JBool(_)))
    )
  }
}
