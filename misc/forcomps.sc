// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/forcomps/for-options-bad2.sc

val successfulCounts = Seq(Some(5), Some(21), Some(8))
val partiallySuccessfulCounts = Seq(Some(5), None, Some(8))

def sumCountsBad(counts: Seq[Option[Int]]): Option[Int] = 
  (counts foldLeft Option(0)) {
    (countOption, count) => 
      if (countOption == None || count == None) None
      else Some(countOption.get + count.get)
  }

sumCountsBad(successfulCounts)
sumCountsBad(partiallySuccessfulCounts)

// ** src/main/scala/progscala2/forcomps/for-eithers-good.sc

def positive(i: Int): Either[String,Int] = 
  if (i > 0) Right(i) else Left(s"nonpositive number $i")

for {
  i1 <- positive(5).right
  i2 <- positive(10 * i1).right
  i3 <- positive(25 * i2).right
  i4 <- positive(2  * i3).right
} yield (i1 + i2 + i3 + i4)
// Returns: scala.util.Either[String,Int] = Right(3805)

for {
  i1 <- positive(5).right
  i2 <- positive(-1 * i1).right   // EPIC FAIL!
  i3 <- positive(25 * i2).right
  i4 <- positive(-2 * i3).right   // EPIC FAIL!
} yield (i1 + i2 + i3 + i4)
// Returns: scala.util.Either[String,Int] = Left(nonpositive number -5)

// ** src/main/scala/progscala2/forcomps/for-eithers-seq.sc
import scala.util.{ Either, Left, Right }

val results: Seq[Either[RuntimeException,Int]] =
  Vector(Right(10), Left(new RuntimeException("boo!")), Right(20))

val results2a = for {
  Right(i) <- results
} yield (2 * i)
// Returns: results2a: Seq[Int] = Vector(20, 40)

// ** src/main/scala/progscala2/forcomps/for-options-good.sc

def positive(i: Int): Option[Int] = 
  if (i > 0) Some(i) else None

for {
  i1 <- positive(5)
  i2 <- positive(10 * i1)
  i3 <- positive(25 * i2)
  i4 <- positive(2  * i3)
} yield (i1 + i2 + i3 + i4)
// Returns: Option[Int] = Some(3805)

for {
  i1 <- positive(5)
  i2 <- positive(-1 * i1)              // <1>   EPIC FAIL!
  i3 <- positive(25 * i2)              // <2>
  i4 <- positive(-2 * i3)              // EPIC FAIL!
} yield (i1 + i2 + i3 + i4)
// Returns: Option[Int] = None

// ** src/main/scala/progscala2/forcomps/for-options-seq.sc

val results: Seq[Option[Int]] = Vector(Some(10), None, Some(20))

val results2a = for {
  Some(i) <- results
} yield (2 * i)
// Returns: results2a: Seq[Int] = Vector(20, 40)

// Translation step #1
val results2b = for {
  Some(i) <- results withFilter { 
    case Some(i) => true
    case None => false 
  }
} yield (2 * i)
// Returns: results2b: Seq[Int] = Vector(20, 40)

// Translation step #2
val results2c = results withFilter { 
  case Some(i) => true
  case None => false 
} map {
  case Some(i) => (2 * i)
}
// Returns: results2c: Seq[Int] = Vector(20, 40)

// ** src/main/scala/progscala2/forcomps/for-validations-good.sc
import scalaz._, std.AllInstances._

def positive(i: Int): Validation[List[String], Int] = {
  if (i > 0) Success(i)                                              // <1>
  else Failure(List(s"Nonpositive integer $i"))
}

for {
  i1 <- positive(5)
  i2 <- positive(10 * i1)
  i3 <- positive(25 * i2)
  i4 <- positive(2  * i3)
} yield (i1 + i2 + i3 + i4)
// Returns: scalaz.Validation[List[String],Int] = Success(3805)

for {
  i1 <- positive(5)
  i2 <- positive(-1 * i1)              // EPIC FAIL!
  i3 <- positive(25 * i2)
  i4 <- positive(-2 * i3)              // EPIC FAIL!
} yield (i1 + i2 + i3 + i4)
// Returns: scalaz.Validation[List[String],Int] =
//   Failure(List(Nonpositive integer -5))                           // <2>

positive(5) +++ positive(10) +++ positive(25)                        // <3>
// Returns: scalaz.Validation[String,Int] = Success(40)

positive(5) +++ positive(-10) +++ positive(25) +++ positive(-30)     // <4>
// Returns: scalaz.Validation[String,Int] =
//   Failure(Nonpositive integer -10, Nonpositive integer -30)

// ** src/main/scala/progscala2/forcomps/ref-transparency.sc

def addInts(s1: String, s2: String): Int = s1.toInt + s2.toInt

for {
  i <- 1 to 3
  j <- 1 to i
} println(s"$i+$j = ${addInts(i.toString, j.toString)}")

def addInts2(s1: String, s2: String): Either[NumberFormatException,Int] = 
  try { 
    Right(s1.toInt + s2.toInt)
  } catch { 
    case nfe: NumberFormatException => Left(nfe)
  }

println(addInts2("1", "2"))
println(addInts2("1", "x"))
println(addInts2("x", "2"))

// ** src/main/scala/progscala2/forcomps/for-tries-seq.sc
import scala.util.{ Try, Success, Failure }

val results: Seq[Try[Int]] =
  Vector(Success(10), Failure(new RuntimeException("boo!")), Success(20))

val results2a = for {
  Success(i) <- results
} yield (2 * i)
// Returns: results2a: Seq[Int] = Vector(20, 40)

// ** src/main/scala/progscala2/forcomps/for-variable-translated.sc

val map = Map("one" -> 1, "two" -> 2)

val list1 = for {
  (key, value) <- map   // How is this line and the next translated?
  i10 = value + 10
} yield (i10)
// Result: list1: scala.collection.immutable.Iterable[Int] = List(11, 12)

// Translation:
val list2 = for {
  (i, i10) <- for {
    x1 @ (key, value) <- map
  } yield {
    val x2 @ i10 = value + 10
    (x1, x2)
  } 
} yield (i10)
// Result: list2: scala.collection.immutable.Iterable[Int] = List(11, 12)
// ** src/main/scala/progscala2/forcomps/for-flatmap.sc

val states = List("Alabama", "Alaska", "Virginia", "Wyoming")

for {
  s <- states
  c <- s
} yield s"$c-${c.toUpper}"
// Results: List("A-A", "l-L", "a-A", "b-B", ...)

states flatMap (_.toSeq map (c => s"$c-${c.toUpper}"))
// Results: List("A-A", "l-L", "a-A", "b-B", ...)

// ** src/main/scala/progscala2/forcomps/for-validations-good-form.sc
import scalaz._, std.AllInstances._

/** Validate a user's name; nonempty and alphabetic characters, only. */
def validName(key: String, name: String):
    Validation[List[String], List[(String,Any)]] = {
  val n = name.trim  // remove whitespace
  if (n.length > 0 && n.matches("""^\p{Alpha}$""")) Success(List(key -> n))
  else Failure(List(s"Invalid $key <$n>"))
}

/** Validate that the string is an integer and greater than zero. */
def positive(key: String, n: String):
    Validation[List[String], List[(String,Any)]] = {
  try {
    val i = n.toInt
    if (i > 0) Success(List(key -> i))
    else Failure(List(s"Invalid $key $i"))
  } catch {
    case _: java.lang.NumberFormatException =>
      Failure(List(s"$n is not an integer"))
  }
}

def validateForm(firstName: String, lastName: String, age: String):
    Validation[List[String], List[(String,Any)]] = {
  validName("first-name", firstName) +++ validName("last-name", lastName) +++
    positive("age", age)
}

validateForm("Dean", "Wampler", "29")
// Returns: Success(List((first-name,Dean), (last-name,Wampler), (age,29)))
validateForm("", "Wampler", "29")
// Returns: Failure(List(Invalid first-name <>))
validateForm("D e a n", "Wampler", "29")
// Returns: Failure(List(Invalid first-name <D e a n>))
validateForm("D1e2a3n_", "Wampler", "29")
// Returns: Failure(List(Invalid first-name <D1e2a3n_>))
validateForm("Dean", "", "29")
// Returns: Failure(List(Invalid last-name <>))
validateForm("Dean", "Wampler", "0")
// Returns: Failure(List(Invalid age 0))
validateForm("Dean", "Wampler", "xx")
// Returns: Failure(List(xx is not an integer))
validateForm("", "Wampler", "0")
// Returns: Failure(List(Invalid first-name <>, Invalid age 0))
validateForm("Dean", "", "0")
// Returns: Failure(List(Invalid last-name <>, Invalid age 0))
validateForm("D e a n", "", "29")
// Returns: Failure(List(Invalid first-name <D e a n>, Invalid last-name <>))

// ** src/main/scala/progscala2/forcomps/for-guard.sc

val states = List("Alabama", "Alaska", "Virginia", "Wyoming")

for {
  s <- states
  c <- s
  if c.isLower
} yield s"$c-${c.toUpper} "
// Results: List("l-L", "a-A", "b-B", ...)

states flatMap (_.toSeq withFilter (_.isLower) map (c => s"$c-${c.toUpper}"))
// Results: List("l-L", "a-A", "b-B", ...)

// ** src/main/scala/progscala2/forcomps/for-options-bad.sc

// An ugly way to process a sequence of options where we stop 
// on the first None. Compare with the for-option-good.sc implementation.
// Note that the +return+ keyword is rarely used in Scala code.
// When you see one, treat it as a _design smell_, a possible candidate
// for refactoring. Often, it can be eliminated by decomposing a function
// into smaller functions.

def doThreeSteps(
    step1: Int => Option[Int], 
    step2: Int => Option[Int], 
    step3: Int => Option[Int]): Option[Int] = {
  val result1 = step1(0) match {
    case None => return None
    case Some(result) => result
  }
  val result2 = step2(result1) match {
    case None => return None
    case Some(result) => result
  }
  step3(result2)
}

doThreeSteps(
  i1 => Some(i1 + 5),
  i2 => Some(i2 + 10),
  i3 => Some(i3 + 25))
// Returns: Option[Int] = Some(40)

doThreeSteps(
  i1 => Some(i1 + 5),
  i2 => None,   // EPIC FAIL!
  i3 => Some(i3 + 25))
// Returns: Option[Int] = None

// ** src/main/scala/progscala2/forcomps/for-tries-good.sc
import scala.util.{ Try, Success, Failure }

def positive(i: Int): Try[Int] = Try {
  assert (i > 0, s"nonpositive number $i")
  i
}

for {
  i1 <- positive(5)
  i2 <- positive(10 * i1)
  i3 <- positive(25 * i2)
  i4 <- positive(2  * i3)
} yield (i1 + i2 + i3 + i4)
// Returns: scala.util.Try[Int] = Success(3805)

for {
  i1 <- positive(5)
  i2 <- positive(-1 * i1)              // EPIC FAIL!
  i3 <- positive(25 * i2)
  i4 <- positive(-2 * i3)              // EPIC FAIL!
} yield (i1 + i2 + i3 + i4)
// Returns: scala.util.Try[Int] = Failure(
//   java.lang.AssertionError: assertion failed: nonpositive number -5)

// ** src/main/scala/progscala2/forcomps/for-map.sc

val states = List("Alabama", "Alaska", "Virginia", "Wyoming")

for {
  s <- states
} yield s.toUpperCase
// Results: List(ALABAMA, ALASKA, VIRGINIA, WYOMING)

states map (_.toUpperCase)
// Results: List(ALABAMA, ALASKA, VIRGINIA, WYOMING)

// ** src/main/scala/progscala2/forcomps/for-variable.sc

val states = List("Alabama", "Alaska", "Virginia", "Wyoming")

for {
  s <- states
  c <- s
  if c.isLower
  c2 = s"$c-${c.toUpper} "
} yield c2
// Results: List("l-L", "a-A", "b-B", ...)

states flatMap (_.toSeq withFilter (_.isLower) map { c => 
  val c2 = s"$c-${c.toUpper} "
  c2
})
// Results: List("l-L", "a-A", "b-B", ...)

// ** src/main/scala/progscala2/forcomps/for-foreach.sc

val states = List("Alabama", "Alaska", "Virginia", "Wyoming")

for {
  s <- states
} println(s)
// Results:
// Alabama
// Alaska
// Virginia
// Wyoming

states foreach println
// Results the same as before.

// ** src/main/scala/progscala2/forcomps/for-patterns.sc

val ignoreRegex = """^\s*(#.*|\s*)$""".r                             // <1>
val kvRegex = """^\s*([^=]+)\s*=\s*([^#]+)\s*.*$""".r                // <2>

val properties = """
  |# Book properties
  |
  |book.name = Programming Scala, Second Edition # A comment 
  |book.authors = Dean Wampler and Alex Payne
  |book.publisher = O'Reilly
  |book.publication-year = 2014
  |""".stripMargin                                                   // <3>

val kvPairs = for {
  prop <- properties.split("\n")                                     // <4>
  if ignoreRegex.findFirstIn(prop) == None                           // <5>
  kvRegex(key, value) = prop                                         // <6>
} yield (key.trim, value.trim)                                       // <7>
// Returns: kvPairs: Array[(String, String)] = Array( 
//   (book.name,Programming Scala, Second Edition), 
//   (book.authors,Dean Wampler and Alex Payne), 
//   (book.publisher,O'Reilly), 
//   (book.publication-year,2014))

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

