// * @author weaves

// * Introductory examples

// Original first steps example.

// ** src/main/scala/progscala2/typelessdomore/method-recursive-return.sc

// ERROR: Won't compile until you put an Int return type on "fact".
import scala.annotation.tailrec

def factorial(i: Int) = {
  @tailrec
  def fact(i: Int, accumulator: Int) = {
    if (i <= 1)
      accumulator
    else
      fact(i - 1, i * accumulator)  // COMPILATION ERROR
  }

  fact(i, 1)
}

// ** src/main/scala/progscala2/typelessdomore/factorial-tailrec.sc

import scala.annotation.tailrec

def factorial(i: Int): Long = {
  @tailrec
  def fact(i: Int, accumulator: Int): Long = {
    if (i <= 1) accumulator
    else fact(i - 1, i * accumulator)
  }

  fact(i, 1)
}

(0 to 5) foreach ( i => println(factorial(i)) )

// ** src/main/scala/progscala2/typelessdomore/multiline-strings.sc

def hello(name: String) = s"""Welcome!
  Hello, $name!
  * (Gratuitous Star!!)
  |We're glad you're here.
  |  Have some extra whitespace.""".stripMargin

hello("Programming Scala")

// ** src/main/scala/progscala2/typelessdomore/multiline-strings2.sc

def goodbye(name: String) = 
  s"""xxxGoodbye, ${name}yyy
  xxxCome again!yyy""".stripPrefix("xxx").stripSuffix("yyy")

goodbye("Programming Scala")

// ** src/main/scala/progscala2/typelessdomore/method-broad-inference-return.sc

def makeList(strings: String*) = {
  if (strings.length == 0)
    Nil  // #1
  else
    strings.toList
}

val list: List[String] = makeList()  // ERROR

// *** Note
// This is no longer an error.

// ** src/main/scala/progscala2/typelessdomore/implicit-strings.sc

val s = "Programming Scala"
s.reverse
s.capitalize
s.foreach(c => print(s"$c-"))

// ** src/main/scala/progscala2/typelessdomore/semicolon-example.sc

// *** Quirk of function definition the Unit=void return type.

// Trailing equals sign indicates more code on the next line.
def equalsign(s: String) =
  println("equalsign: " + s)

// Trailing opening curly brace indicates more code on the next line.
def equalsign2(s: String) = {
  println("equalsign2: " + s)
}

// Trailing opening curly brace indicates more code on the next line.
def equalsign3(s: String) println("equalsign3: " + s) // Error

def equalsign3(s: String) { println("equalsign3: " + s) } 
// But type inference is now "void", it is now an unapplied function.

equalsign3("nothing")

// compare the signatures like this

equalsign _
equalsign2 _
equalsign3 _

// **** Note
// Tricky one to track down. All the functions have the same signature, but
// equalsign3 doesn't run the code.

/// http://docs.scala-lang.org/overviews/reflection/overview.html

// *** Line continuations and no need for semi-colon.

// Trailing commas, periods, and operators indicate more code on the next line.
def commas(s1: String,
           s2: String) = Console.
  println("comma: " + s1 + 
          ", " + s2)

// ** src/main/scala/progscala2/typelessdomore/count-to.sc

def countTo(n: Int): Unit = {
  def count(i: Int): Unit = {
    if (i <= n) { println(i); count(i + 1) }
  }
  count(1)
}

countTo(10)

// *** Note
// This demonstrates internal functions capture the holding function's state and can
// be recursive.

// ** src/main/scala/progscala2/typelessdomore/factorial.sc

def factorial(i: Int): Long = {
  def fact(i: Int, accumulator: Long): Long = {
    if (i <= 1) accumulator
    else fact(i - 1, i * accumulator)
  }
    
  fact(i, 1L)
}

(0 to 5) foreach ( i => println(factorial(i)) )

// ** src/main/scala/progscala2/typelessdomore/parameterized-types.sc
import java.io._

abstract class BulkReader[In] {
  val source: In
  def read: String
}

class StringBulkReader(val source: String) extends BulkReader[String] {
  def read = source
}

class FileBulkReader(val source: File) extends BulkReader[File] {
  def read = {
    val in = new BufferedInputStream(new FileInputStream(source))
    val numBytes = in.available()
    val bytes = new Array[Byte](numBytes)
    in.read(bytes, 0, numBytes)
    new String(bytes)
  }
}

println( new StringBulkReader("Hello Scala!").read )

// Assumes the current directory is parent of misc/.
println( new FileBulkReader(
  new File("misc/typelessdomore.sc")).read )

// *** Note
// Abstract class and override example.

// ** src/main/scala/progscala2/typelessdomore/map-get.sc

// Very limited version of a map; it can hold only one key-value
// pair! The "get" method is used in the text, by itself...

class MyMap[A,B](var _key: A, var _value: B) {
  def get(key: A): Option[B] = {
    if (contains(key))
      Some(getValue(key))
    else
      None
  }
  def contains(key: A) = key == _key
  def getValue(key: A) = _value
  def put(key: A, value: B) = _value = value
}

val m = new MyMap(1, "one")
println( m.get(1) )

// *** Note
// Demonstrates type inference.

// ** src/main/scala/progscala2/typelessdomore/partial-functions.sc

val pf1: PartialFunction[Any,String] = { case s:String => "YES" }    // <1>
val pf2: PartialFunction[Any,String] = { case d:Double => "YES" }    // <2>

val pf = pf1 orElse pf2                                              // <3>

def tryPF(x: Any, f: PartialFunction[Any,String]): String =          // <4>
  try { f(x).toString } catch { case _: MatchError => "ERROR!" }

def d(x: Any, f: PartialFunction[Any,String]) =                      // <5>
  f.isDefinedAt(x).toString

println("      |   pf1 - String  |   pf2 - Double  |    pf - All")   // <6>
println("x     | def?  |  pf1(x) | def?  |  pf2(x) | def?  |  pf(x)")
println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
List("str", 3.14, 10) foreach { x =>
  printf("%-5s | %-5s | %-6s  | %-5s | %-6s  | %-5s | %-6s\n", x.toString, 
    d(x,pf1), tryPF(x,pf1), d(x,pf2), tryPF(x,pf2), d(x,pf), tryPF(x,pf))
}

// *** Note

// Partial function means that the function is only defined for certain values of the
// defined type. Here, we define pf as pf1 or pf2, so only allow the first Any-typed
// argument to be a string or a Double.
//
// So part of function composition.

/*

      |   pf1 - String  |   pf2 - Double  |    pf - All
x     | def?  |  pf1(x) | def?  |  pf2(x) | def?  |  pf(x)

 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

str   | true  | YES     | false | ERROR!  | true  | YES   
3.14  | false | ERROR!  | true  | YES     | true  | YES   
10    | false | ERROR!  | false | ERROR!  | false | ERROR!

*/

// ** src/main/scala/progscala2/typelessdomore/method-overloaded-return-v2.sc
// Version 2 of "StringUtil" (with a fixed compilation error).

object StringUtilV2 {
  def joiner(strings: String*): String = strings.mkString("-")

  def joiner(strings: List[String]): String = joiner(strings :_*)
}

println( StringUtilV2.joiner(List("Programming", "Scala")) )

// ** src/main/scala/progscala2/typelessdomore/method-overloaded-return-v3.sc

// Version 3 of "StringUtil" (New variable argument list methods).

object StringUtilV3 {
  def joiner(strings: List[String], separator: String): String = 
    strings.mkString(separator)

  def joiner(strings: List[String]): String = joiner(strings, " ") 
  def joiner(strings: String*): String      = joiner(strings.toList)

}

println( StringUtilV3.joiner(List("Programming", "Scala")) )

/// *** Note
/// This gets around adding a default separator String to a function that takes
/// String*. I think.


// ** src/main/scala/progscala2/typelessdomore/state-capitals-map-decl.sc

val stateCapitals = Map(
  "Alabama" -> "Montgomery",
  "Alaska"  -> "Juneau",
  "Washington"  -> "Olympia",
  // ...
  "Wyoming" -> "Cheyenne")
// ...

val capitals0 = stateCapitals

/// *** Note
/// Just a check to show that re-assigning to a val does not lose the original
/// object.

// ** src/main/scala/progscala2/typelessdomore/state-capitals-subset.sc

val stateCapitals = Map(
  "Alabama" -> "Montgomery",
  "Alaska"  -> "Juneau",
  // ...
  "Wyoming" -> "Cheyenne")
    
println( "Get the capitals wrapped in Options:" )
println( "Alabama: " + stateCapitals.get("Alabama") )
println( "Wyoming: " + stateCapitals.get("Wyoming") )
println( "Unknown: " + stateCapitals.get("Unknown") )

println( "Get the capitals themselves out of the Options:" )
println( "Alabama: " + stateCapitals.get("Alabama").get )
println( "Wyoming: " + stateCapitals.get("Wyoming").getOrElse("Oops!") )
println( "Unknown: " + stateCapitals.get("Unknown").getOrElse("Oops2!") )

/// *** Note
// In the first, you only see None - which you may not recognize as a fail.
// scala> Alabama: Some(Montgomery)
// scala> Wyoming: Some(Cheyenne)
// scala> Unknown: None


// ** src/main/scala/progscala2/typelessdomore/tuple-example.sc

val t = ("Hello", 1, 2.3)                                            // <1>
println( "Print the whole tuple: " + t )   
println( "Print the first item:  " + t._1 )                          // <2>
println( "Print the second item: " + t._2 )
println( "Print the third item:  " + t._3 )

val t0 = "World"

val (t1, t2, t3) = ("World", '!', 0x22)                              // <3>

val (t4, t5, t6) = Tuple3("World", '!', 0x22)                        // <4>

println( t1 + ", " + t2 + ", " + t3 )   
println( t4 + ", " + t5 + ", " + t6 )   

val t0 = ( () => "Wor" + "ld")()

t1 == t4
t1.equals(t4)

t0 == t4
t0.equals(t4)
t0 eq t4

t1 eq t4
t4 eq t1

// *** Note
// The Tuple3 doesn't type coerce and it can split up to different values
// And a quick comparison of equals, reference equals
// http://www.scala-lang.org/api/current/scala/AnyRef.html

// ** src/main/scala/progscala2/typelessdomore/abstract-types.sc
import java.io._

abstract class BulkReader {
  type In
  val source: In
  def read: String  // Read source and return a String
}

class StringBulkReader(val source: String) extends BulkReader {
  type In = String
  def read: String = source
}

class FileBulkReader(val source: File) extends BulkReader {
  type In = File
  def read: String = {
    val in = new BufferedInputStream(new FileInputStream(source))
    val numBytes = in.available()
    val bytes = new Array[Byte](numBytes)
    in.read(bytes, 0, numBytes)
    new String(bytes)
  }
}

println(new StringBulkReader("Hello Scala!").read)
// Assumes the current directory is src/main/scala:
println(new FileBulkReader(
  new File("TypeLessDoMore/abstract-types.sc")).read)

// ** src/main/scala/progscala2/typelessdomore/ranges.sc

1 to 10                   // Int range inclusive, interval of 1, (1 to 10)

1 until 10                // Int range exclusive, interval of 1, (1 to 9)

1 to 10 by 3              // Int range inclusive, every third.

1L to 10L by 3            // Long

1.1f to 10.3f by 3.1f     // Float with an interval != 1

1.1f to 10.3f by 0.5f     // Float with an interval < 1

1.1 to 10.3 by 3.1        // Double

'a' to 'g' by 3            // Char

BigInt(1) to BigInt(10) by 3

BigDecimal(1.1) to BigDecimal(10.3) by 3.1 




// ** src/main/scala/progscala2/typelessdomore/person.sc

class Person(val name: String, var age: Int)

val p = new Person("Dean Wampler", 29)

p.name
p.age

p.name = "Buck Trends"
p.age = 30

p

// ** src/main/scala/progscala2/typelessdomore/futures.sc

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def sleep(millis: Long) = {
  Thread.sleep(millis)
}

// Busy work ;)
def doWork(index: Int) = {
  sleep((math.random * 1000).toLong)
  index
}

(1 to 5) foreach { index =>
  val future = Future {
    doWork(index)
  }
  future onSuccess {
    case answer: Int => println(s"Success! returned: $answer")
  }
  future onFailure {
    case th: Throwable => println(s"FAILURE! returned: $th")
  }
}

sleep(1000)  // Wait long enough for the "work" to finish.
println("Finito!")

/// *** Note
/// Background task execution is very easy to do. Using Future { }.
/// This is a block expression like break {}.
/// The use of the Implicits package is transparent.


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

