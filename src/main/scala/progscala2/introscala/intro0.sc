// @author weaves
//
// Introductory examples.


// wildcard type.
def f0(m:Map[_ <: AnyRef,_ <: AnyRef]):Int = {
  println(m.size)
  m.size
}

1 + 2

val m = sys.env

m.foreach( x => println(s"$x._1 -> $x._2"))

for ((k,v) <- m) printf("key: %s, value: %s\n", k, v)

m foreach {case (key, value) => println (key + "-->" + value)}

// Rich operations

0 max 5

0 min 5

// The post-fix operators need to be enabled.

import scala.language.postfixOps

-2.7 abs

-2.7 round

1.5 isInfinity

(1.0 / 0) isInfinity

4 to 6

"bob" capitalize

"robert" drop 2

/*
 
// Rich types

scala.runtime.RichByte                Byte     
scala.runtime.RichShort		      Short  
scala.runtime.RichInt		      Int    
scala.runtime.RichChar		      Char   
scala.runtime.RichFloat		      Float  
scala.runtime.RichDouble	      Double 
scala.runtime.RichBoolean	      Boolean
scala.collection.immutable.StringOps  String 

http://www.scala-lang.org/api/2.11.8/index.html#scala.collection.immutable.StringOps



// Type variance: 

Name 	
Description 	
Scala Syntax

Invariant
C[T'] and C[T] are not related
C[T]

Covariant
C[T'] is a subclass of C[T]
C[+T]

Contravariant
C[T] is a subclass of C[T']
C[-T]

Most immutable collections are covariant, and most mutable collections are invariant

This is one of the reasons, it better to use immutable collections where possible.

*/

// In this example, the derived types can be appended to a new collection of the base type.


class Fruit

case class Apple() extends Fruit

case class Orange() extends Fruit

val l1: List[Apple] = Apple() :: Nil

val l2: List[Fruit] = Orange() :: l1

// and also, it's safe to prepend with "anything",
// as we're building a new list - not modifying the previous instance

val l3: List[AnyRef] = "" :: l2

// Array is mutable so this won't compile
val a: Array[Any] = Array[Int](1, 2, 3)

// But a wild card will allow it.
val a: Array[_ <: Any] = Array[Int](1, 2, 3)


// Identifiers paired back-ticks can make any token - even reserved words - identifiers.

val `yield`:Int = 1
println(`yield`)

// If it doesn't need backticks, but they're used
val `x0`:Int = 1
println(x0)

// Implicit conversions

class Rational(n: Int, d: Int) {
  require(d != 0)

  private val g = gcd(n.abs, d.abs)

  val numer = n / g
  val denom = d / g

  def this(n: Int) = this(n, 1)

  def + (that: Rational): Rational =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )

  def + (i: Int): Rational =
    new Rational(numer + i * denom, denom)

  def - (that: Rational): Rational =
    new Rational(
      numer * that.denom - that.numer * denom,
      denom * that.denom
    )

  def - (i: Int): Rational =
    new Rational(numer - i * denom, denom)

  def * (that: Rational): Rational =
    new Rational(numer * that.numer, denom * that.denom)

  def * (i: Int): Rational =
    new Rational(numer * i, denom)

  def / (that: Rational): Rational =
    new Rational(numer * that.denom, denom * that.numer)

  def / (i: Int): Rational =
    new Rational(numer, denom * i)

  override def toString = numer +"/"+ denom

  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}

val r = new Rational(66, 47)

r * 2

// but not

2 * r

// so use an implicit

import scala.language.implicitConversions

implicit def intToRational(x: Int) = new Rational(x)

2 * r

// Control structures and iteration
// listFiles is a Java method.

val filesHere = (new java.io.File(".")).listFiles

for (file <- filesHere)
  println(file)

// Using yield

def scalaFiles =
  for {
    file <- filesHere
    if file.getName.endsWith(".scala")
  } yield file

// try-catch

// Render an Array as a List
// And typing a var for a block.

import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException


val names = scalaFiles.toList.head :: List(new java.io.File("input.txt"))

var f:java.io.FileReader = null

for (fn <- names) 
  try {
    println("file: " + fn)
    f = new FileReader(fn)
    // Use and close file
  } catch {
    case ex: FileNotFoundException => println("no file: " + fn.toString)// Handle missing file
    case ex: IOException => println("io" + fn.toString) // IO error
  } finally {
    f.close
  }

