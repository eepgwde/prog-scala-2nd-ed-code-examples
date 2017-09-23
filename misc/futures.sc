// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/concurrency/futures/future-fold.sc
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

val futures = (0 to 9) map {                                         // <1>
  i => Future { 
    val s = i.toString                                               // <2>
    print("s: " + s)
    s
  }
}
 
val f = Future.reduce(futures)((s1, s2) => s1 + s2)                  // <3>
 
val n = Await.result(f, Duration.Inf)                                // <4>


// ** src/main/scala/progscala2/concurrency/futures/future-callbacks.sc
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

case class ThatsOdd(i: Int) extends RuntimeException(                // <1>
  s"odd $i received!")

import scala.util.{Try, Success, Failure}                            // <2>

val doComplete: PartialFunction[Try[String],Unit] = {                // <3>
  case s @ Success(_) => println(s)                                  // <4>
  case f @ Failure(_) => println(f)
}

// This doesn't do the processing in the background.
def op(i:Int) = { print(s"i: $i; "); Thread.sleep((math.random * 1000).toLong); i.toString }

val futures = (0 to 9) map {                                         // <5>
  case i if i % 2 == 0 => Future.successful(op(i))
  case i => Future.failed(ThatsOdd(i))
}

futures map (_ onComplete doComplete)                                // <6>

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

