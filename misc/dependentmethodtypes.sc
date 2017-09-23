// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/typesystem/dependentmethodtypes/dep-method.sc
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class LocalResponse(statusCode: Int)
case class RemoteResponse(message: String)

sealed trait Computation {
  type Response
  val work: Future[Response]
}

case class LocalComputation(
    work: Future[LocalResponse]) extends Computation {
  type Response = LocalResponse
}
case class RemoteComputation(
    work: Future[RemoteResponse]) extends Computation {
  type Response = RemoteResponse
}

object Service {
  def handle(computation: Computation): computation.Response = {
    val duration = Duration(2, SECONDS)
    Await.result(computation.work, duration)
  }
}

// *** Activate

Service.handle(LocalComputation(Future(LocalResponse(0))))
// Result: LocalResponse = LocalResponse(0)

Service.handle(RemoteComputation(Future(RemoteResponse("remote call"))))
// Result: RemoteResponse = RemoteResponse(remote call)

// * Postamble

// The following are the file variables.

// Local Variables:
// mode:scala
// scala-edit-mark-re: "^// [\\*]+ "
// comment-column:50 
// comment-start: "// "  
// comment-end: "" 
// eval: (outline-minor-mode)
// outline-regexp: "// [*]+"
// eval: (auto-fill-mode)
// fill-column: 85 
// End: 

