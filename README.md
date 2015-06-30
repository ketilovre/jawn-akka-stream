## jawn-akka-stream

Integrates the [Jawn library](https://github.com/non/jawn) with Akka Streams, allowing you to transform instances of
`Source[String, _]` or `Source[ByteString, _]` into `Source[J, _]` using Jawn's AsyncParser. Mainly intended 
for use with akka-http, where the base representation of a request body is a `Source[ByteString, _]`.

This project is currently not published anywhere, as it's a total of 55 lines of code.

### Example

```scala
import jawn.AsyncParser
import ketilovre.jawn.akka.stream.JawnStreamParser

val parser = JawnStreamParser[J](AsyncParser.UnwrapArray)

// Stream containing an array of Json objects as String chunks.
val stringSource: Source[String, Unit] = ???
val json: Source[J, Unit] = stringSource.via(parser.stringFlow)
 
// Stream containing an array of Json objects as ByteString chunks.
val byteStringSource: Source[ByteString, Unit] = ???
val json: Source[J, Unit] = byteStringSource.via(parser.byteStringFlow)

```

### AST Support
The parser wraps Jawn, and by extension supports any ASTs supported by Jawn. 
[See the relevant docs in the Jawn repo](https://github.com/non/jawn#supporting-external-asts-with-jawn).
You can create a stream parser for any supported AST by providing an implicit `Facade[J]` when creating the parser.

```scala
import jawn.AsyncParser
import jawn.support.json4s.Parser._
import ketilovre.jawn.akka.stream.JawnStreamParser
import org.json4s.JValue

val json4sParser = JawnStreamParser[JValue](AsyncParser.SingleValue)
```

### Parsing modes

[All AsyncModes from Jawn are supported](https://github.com/non/jawn#parsing) and behave as you would expect.
`SingleValue` results in a source containing a single parsed value, while `UnwrapArray` and `ValueStream` push
elements downstream as they become available.

### Directives

If you're using akka-http, there are directives which take a `JawnStreamParser[J]` and extract the request
body as a `Source[J, _]`.

`Directives.extractJson` will optimistically parse the body to Json regardless of content type.

`Directives.expectJson` demands the correct content type, and will reject non-Json requests with a 
`UnsupportedRequestContentTypeRejection`.

```scala
import ...
import ketilovre.jawn.akka.http.Directives._

val parser = JawnStreamParser[J](AsyncParser.ValueStream)

val route = {
  post("/") {
    expectJson(parser) { jsonSource =>
      ...
    }
  }
}
```
