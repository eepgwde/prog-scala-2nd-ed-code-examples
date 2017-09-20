// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/fp/categories/for-options-steps.sc

// Example of using option handling on an arbitrarily-long 
// sequence of functions that return Option[Int]. When the sequence
// is arbitrary, you can't use a for comprehension.
  
// Alias the function signature:
type Step = Int => Option[Int]

// A sequence of functions for the process steps. Each takes the result of
// the previous step (or a seed value to start) and returns a new option.
val successfulSteps = List(
  (i:Int) => Some(i + 5), 
  (i:Int) => Some(i + 10), 
  (i:Int) => Some(i + 25))

val partiallySuccessfulSteps = List(
  (i:Int) => Some(i + 5), 
  // A step that fails and indicates the failure by returning +None+.
  (i:Int) => None,   // indicates a fail, not a compiler error.
  (i:Int) => Some(i + 25))

// Fold over the steps, starting with the seed value +0+, wrapped in a +Some+.
// Use +flatMap+ to extract the current sum value and pass it to the current 
// step, which returns a new +Option+ that becomes the next +sumOpt+. 
// If +sumOpt+ is actually a +None+, the anonymous function isn't called and
// +None+ is returned.
def sumCounts1(countSteps: Seq[Step]): Option[Int] = 
  (countSteps foldLeft Option(0)) {
    (sumOpt, step) => sumOpt flatMap (i => step(i))
  }

sumCounts1(successfulSteps)
// Returns: Option[Int] = Some(40)

sumCounts1(partiallySuccessfulSteps)
// Returns: Option[Int] = None

// More verbose, but it stops the "counts" iteration at the first None
// and it doesn't create intermediate Options:
// We sequence through the steps and sum the values returned. 
// A nested, tail-recursive function +sum+ is used.
def sumCounts2(countSteps: Seq[Step]): Option[Int] = {
  @annotation.tailrec
  def sum(accum: Int, countSteps2: Seq[Step]): Option[Int] = 
    countSteps2 match {
      // Terminate the recursion when we hit the end of the steps +Seq+.
      case Nil          => Some(accum)
      // Otherwise, the head of the list is a step, so we call it
      // with the current +accum+ value. We pattern match on the 
      // returned +Option+.
      case step +: tail => step(accum) match {
        // If +None+, terminate the recursion and return +None+.
        case None     => None
        // Otherwise, call +sum+ again with the new accumulated value 
        // and the steps tail.
        case Some(i2) => sum(i2, tail)
      }
    } 
  // Call the nested function with the seed sum +0+ and the sequence of steps.
  sum(0, countSteps)
}

sumCounts2(successfulSteps)
// Returns: Option[Int] = Some(40)

sumCounts2(partiallySuccessfulSteps)
// Returns: Option[Int] = None

// ** src/main/scala/progscala2/fp/categories/for-eithers-steps.sc
import scala.util.{ Either, Left, Right }

// Example of using Either handling on an arbitrarily-long
// sequence of functions that return Either[X,Int]. When the sequence
// is arbitrary, you can't use a for comprehension.

// Alias the long function signature:
type Step = Int => Either[RuntimeException,Int]

val successfulSteps: Seq[Step] = List(
  (i:Int) => Right(i + 5),
  (i:Int) => Right(i + 10),
  (i:Int) => Right(i + 25))
val partiallySuccessfulSteps: Seq[Step] = List(
  (i:Int) => Right(i + 5),
  (i:Int) => Left(new RuntimeException("FAIL!")),
  (i:Int) => Right(i + 25))

def sumCounts1(countSteps: Seq[Step]): Either[RuntimeException,Int] = {
  val zero: Either[RuntimeException,Int] = Right(0)
  (countSteps foldLeft zero) {
    (sumEither, step) => sumEither.right flatMap (i => step(i))
  }
}

sumCounts1(successfulSteps)
// Returns: .Either[RuntimeException,Int] = Right(40)

sumCounts1(partiallySuccessfulSteps)
// Returns: Either[RuntimeException,Int] = Left(RuntimeException: FAIL!)

// More verbose, but it stops the "counts" iteration at the first Left.
// and it doesn't create intermediate Rights:
def sumCounts2(countSteps: Seq[Step]): Either[RuntimeException,Int] = {
  @annotation.tailrec
  def sum(accum: Int, countSteps2: Seq[Step]): Either[RuntimeException,Int] =
    countSteps2 match {
      case Nil          => Right(accum)
      case step +: tail => step(accum) match {
        case l @ Left(x) => l
        case Right(i2)   => sum(i2, tail)
      }
    }
  sum(0, countSteps)
}

sumCounts2(successfulSteps)
// Returns: .Either[RuntimeException,Int] = Right(40)

sumCounts2(partiallySuccessfulSteps)
// Returns: Either[RuntimeException,Int] = Left(RuntimeException: FAIL!)

// ** src/main/scala/progscala2/fp/categories/Functor.sc
import progscala2.fp.categories._
import scala.language.higherKinds

val fii: Int => Int       = i => i * 2
val fid: Int => Double    = i => 2.1 * i
val fds: Double => String = d => d.toString

SeqF.map(List(1,2,3,4))(fii)                // Seq[Int]: List(2, 4, 6, 8)
SeqF.map(List.empty[Int])(fii)              // Seq[Int]: List()

OptionF.map(Some(2))(fii)                   // Option[Int]: Some(4)
OptionF.map(Option.empty[Int])(fii)         // Option[Int]: None

val fa = FunctionF.map(fid)(fds)                                     // <1>
fa(2)                                       // String: 4.2

// val fb = FunctionF.map(fid)(fds)                                     <2>
val fb = FunctionF.map[Int,Double,String](fid)(fds)
fb(2)

val fc = fds compose fid                                             // <3>
fc(2)                                       // String: 4.2

// ** src/main/scala/progscala2/fp/categories/for-tries-steps.sc
import scala.util.{ Try, Success, Failure }

// Example of using try handling on an arbitrarily-long
// sequence of functions that return Try[Int]. When the sequence
// is arbitrary, you can't use a for comprehension.

// Alias the function signature:
type Step = Int => Try[Int]

val successfulSteps: Seq[Step] = List(
  (i:Int) => Success(i + 5),
  (i:Int) => Success(i + 10),
  (i:Int) => Success(i + 25))
val partiallySuccessfulSteps: Seq[Step] = List(
  (i:Int) => Success(i + 5),
  (i:Int) => Failure(new RuntimeException("FAIL!")),
  (i:Int) => Success(i + 25))

def sumCounts1(countSteps: Seq[Step]): Try[Int] = {
  val zero: Try[Int] = Success(0)
  (countSteps foldLeft zero) {
    (sumTry, step) => sumTry flatMap (i => step(i))
  }
}

sumCounts1(successfulSteps)
// Returns: scala.util.Try[Int] = Success(40)

sumCounts1(partiallySuccessfulSteps)
// Returns: scala.util.Try[Int] = Failure(java.lang.RuntimeException: FAIL!)

// More verbose, but it stops the "counts" iteration at the first Failure.
// and it doesn't create intermediate Successes:
def sumCounts2(countSteps: Seq[Step]): Try[Int] = {
  @annotation.tailrec
  def sum(accum: Int, countSteps2: Seq[Step]): Try[Int] =
    countSteps2 match {
      case Nil          => Success(accum)
      case step +: tail => step(accum) match {
        case f @ Failure(ex) => f
        case Success(i2)     => sum(i2, tail)
      }
    }
  sum(0, countSteps)
}

sumCounts2(successfulSteps)
// Returns: scala.util.Try[Int] = Success(40)

sumCounts2(partiallySuccessfulSteps)
// Returns: scala.util.Try[Int] = Failure(java.lang.RuntimeException: FAIL!)

// ** src/main/scala/progscala2/fp/categories/Monad.sc
import progscala2.fp.categories._
import scala.language.higherKinds

val seqf: Int => Seq[Int] = i => 1 to i
val optf: Int => Option[Int] = i => Option(i + 1)

SeqM.flatMap(List(1,2,3))(seqf)             // Seq[Int]: List(1,1,2,1,2,3)
SeqM.flatMap(List.empty[Int])(seqf)         // Seq[Int]: List()

OptionM.flatMap(Some(2))(optf)              // Option[Int]: Some(3)
OptionM.flatMap(Option.empty[Int])(optf)    // Option[Int]: None

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

