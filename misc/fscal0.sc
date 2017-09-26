// * @author weaves

// * Introductory examples

// ** Type annotations and ascription

val x = 1

val x0:Byte = 0

val x1 = 0:Byte				// preferred - 0 is set as a Byte, x1 takes that type.

// *** Ascription

// This is essentially an up-cast.  

// **** Upcast and downcast

// Upcasting means casting the object to a supertype, while
// downcasting means casting to a subtype. In java, upcasting is not
// necessary as it's done automatically. And it's usually referred as
// implicit casting. ... Downcasting is instead necessary because you
// defined a as object of Animal.

// **** Narrowing and widening. 

// This was originally defined on on just primitive types. narrowing:
// converting a large (primitive) type to a smaller one - int to byte,
// for example.  widening is the opposite - byte to int - and doesn't
// lose information.

// It is now be used to refer to reference types and is similar to 
//
//  widening ~ upcast
//  narrowing ~ downcast

// A simple type ascription, we want to have an Object, not a String refer to the string
// s.

val s = "Dave"

val p = s: Object

// This then shows
p.getClass

// res3: Class[_ <: Object] = class java.lang.String
s.getClass
// 
// res4: Class[_ <: String] = class java.lang.String

// Similarly for these

Nil: List[String]

Set(values: _*)

"Daniel": AnyRef

// ** Variance

// Class hierarchies allow the expression of subtype relationships. A
// central question that comes up when mixing OO with polymorphism is: if
// T’ is a subclass of T, is Container[T’] considered a subclass of
// Container[T]? Variance annotations allow you to express the following
// relationships between class hierarchies & polymorphic types:

//  	Meaning 	Scala notation
 
// covariant 	        C[T’] is a subclass of C[T] 	[+T]
// contravariant 	C[T] is a subclass of C[T’] 	[-T]
// invariant 	        C[T] and C[T’] are not related 	[T]

// Immutable collections can and should be covariant. Methods that receive the
// contained type should “downgrade” the collection appropriately.

// Mutable collections should not be covariant. 

// ** Type Bounds

// https://twitter.github.io/scala_school/advanced-types.html#otherbounds

// For Scala 2.9 and before we had these

// A =:= B  A be equal to B
// A <:< B  A be a subtype of B
// A <%< B  A be viewable as B

// They have now been replaced =:= is still needed, but A<% B is the notation now.

// ** Upper and lower type bounds

// In Scala, type parameters and abstract types may be constrained by a
// type bound. Such type bounds limit the concrete values of the type
// variables and possibly reveal more information about the members of
// such types. 

// *** Upper type bound

// An upper type bound T <: A declares that type variable T
// must be derived from a type A, ie. T must be a sub-type of A.

// Here is an example that demonstrates
// upper type bound for a type parameter of class Cage:

abstract class Animal {
 def name: String
}

abstract class Pet extends Animal {}

class Cat extends Pet {
  override def name: String = "Cat"
}

class Dog extends Pet {
  override def name: String = "Dog"
}

class Lion extends Animal {
  override def name: String = "Lion"
}

// Introduce an upper type bound: P must be a sub-class of Pet.

class PetContainer[P <: Pet](p: P) {
  def pet: P = p
}

val dogContainer = new PetContainer[Dog](new Dog)
val catContainer = new PetContainer[Cat](new Cat)

val lionContainer = new PetContainer[Lion](new Lion)

// <console>:17: error: type arguments [Lion] do not conform to class PetContainer's type parameter bounds [P <: Pet]


// *** Lower type bound

// While upper type bounds limit a type to a subtype of another type,
// lower type bounds declare a type to be a supertype of another
// type. 

// The term B >: A expresses that the type parameter B or the abstract
// type B refer to a supertype of type A. 
//
// In most cases, A will be the type parameter of the class and B will
// be the type parameter of a method.

// Here's a bad example of a partial implementation of a singly-linked list.

trait Node[+B] {
  def prepend(elem: B): Unit
}

// <console>:12: error: covariant type B occurs in contravariant position in type B of value elem
//          def prepend(elem: B): Unit

case class ListNode[+B](h: B, t: Node[B]) extends Node[B] {
  def prepend(elem: B) = ListNode[B](elem, this)
  def head: B = h
  def tail = t
}

case class Nil[+B]() extends Node[B] {
  def prepend(elem: B) = ListNode[B](elem, this)
}

// However, this program does not compile because the parameter elem in
// prepend is of type B, which we declared covariant. This doesn’t work
// because functions are contravariant in their parameter types and
// covariant in their result types.

// Here is that example corrected

trait Node[+B] {
  def prepend[U >: B](elem: U)
}

case class ListNode[+B](h: B, t: Node[B]) extends Node[B] {
  def prepend[U >: B](elem: U) = ListNode[U](elem, this)
  def head: B = h
  def tail = t
}

case class Nil[+B]() extends Node[B] {
  def prepend[U >: B](elem: U) = ListNode[U](elem, this)
}

// Now, we can use this singly-linked list.

trait Mammal
case class AfricanSwallow() extends Mammal
case class EuropeanSwallow() extends Mammal


val africanSwallowList= ListNode[AfricanSwallow](AfricanSwallow(), Nil())
val mammalList: Node[Mammal] = africanSwallowList

mammalList.prepend(new EuropeanSwallow)

// ***** Note
// This isn't a very good example of a list. It doesn't actually prepend, but it does
// demonstrate that we can accept another sub-type of Mammal in a list that was originally 
// defined as another type of ListNode.

// ** src/main/scala/effective/variance

trait Collection[+T] {
  def add[U >: T](other: U): Collection[U]
}

// Mutable collections should be invariant because it isn't possible to maintain
// referential integrity.

class HashSet[+T] {
  def add[U >: T](item: U) = {}
}

class Mammal
class Dog extends Mammal
class Cat extends Mammal

// If I now have a hash set of dogs

val dogs: HashSet[Dog]

// treat it as a set of Mammals and add a cat.

val mammals: HashSet[Mammal] = dogs
mammals.add(new Cat{})

// *** Note
// This code doesn't run. It is only a partial implementation.

// ** View Bounds

// https://twitter.github.io/scala_school/advanced-types.html#viewbounds

// Using implicit 

// Another feature of Scala the "Pimp my library" pattern to extend library implementations
// This is C++ friend notion with the implicit cast.

import scala.language.implicitConversions

class BlingString(string: String) {
    def bling = "*" + string + "*"
}

implicit def blingYoString(string: String) = new BlingString(string)

"Let's get blinged out!".bling

// Relaxing a type constraint with a a View bound.

import scala.language.implicitConversions

implicit def strToInt(x: String) = x.toInt

// This sort of thing can be tested with
// implicitly defined as 
// def implicitly[T](implicit e: T): T = e


// There are a lot of subtleties here. 

// This doesn't bind to a type.
class Container[A] { def addIt[B <: A](x: B) = 123 + x }

// Here we have a class that is defined by its template.
// We put a template on the method and that prevents the implicit.

// The implicit evidence does not 

// The class hierarchy for Scala *does not* relate Int as a sub-type of Long 
// It does allow conversion.

class Container[A] { def addIt[B](x: B)(implicit evidence: B <:< Int) = 123 + x }

(new Container[Int]).addIt(123)

(new Container[Int]).addIt(123:Short)

(new Container[Int]).addIt("123")	// this fails because of the evidence

// Remove the type constraint on the method and use the evidence implicit.

class Container[A] { def addIt(x: A)(implicit evidence: A <:< Int) = 123 + x }

(new Container[Int]).addIt(123)

(new Container[Int]).addIt("123")	// this succeeds by applying the conversion

(new Container[BigInt]).addIt("123")	// this fails too

(new Container[Double]).addIt(123.0)	// this fails too.

// Using direct inheritance Not at all useful with Scala, it has a
// View hierarchy and not a type hierarchy for Value types.

class Container[A <: Int] { def addIt(x: A) = 123 + x }

(new Container[Int]).addIt(123)

// But this uses the implicit conversion and doesn't need an evidence implicit.

(new Container[Int]).addIt("123")

// But still nothing for Float

(new Container[Int]).addIt(123.0)

// But does support Short, so there must be an implicit in operation.

(new Container[Int]).addIt(123:Short)

// Use the view hierachy - still uses implicit conversions.

class Container[A <% Int] { def addIt(x: A) = 123 + x }

(new Container[Int]).addIt(123)

// And it uses the implicit conversion.

(new Container[Int]).addIt("123")

// But still nothing for Float

(new Container[Int]).addIt(123:Short)

// Use the Numeric trait, but problems with + overloading if 123 is used.

class Container[A: Numeric] { def addIt[B <% A](x: B) = x }

(new Container[Double]).addIt(123:Short)

// case class doesn't work because B type is still generic and may not support toInt.

class Container[A: Numeric] {
  def addIt[B <% A](x: B) = x match {
    case Int => 123 + x.toInt
    case _ => x
  }
}

// Overloads will work, but you must define the return type.

class Container[A: Numeric] {
  def addIt(x: Int) : Int = 123 + x
  def addIt(x: Double) : Int = 123 + x.toInt
  def addIt(x: Short) : Int = 123 + x.toInt
  def addIt(x: String) : Int = 123 + x
}

// But now, it doesn't use the implicit, it applies string concatenation.

(new Container[Double]).addIt(123:Short)
(new Container[Double]).addIt("123")

// More overloads.

class Container[A: Numeric] {
  def addIt(x: Int) : Int = 123 + x
  def addIt(x: Double) : Int = 123 + x.toInt
  def addIt(x: Short) : Int = 123 + x.toInt
  // This won't work now, because the implicit gets in the way.
  // def addIt(x: String) : Int = 123 + x.toInt
  // So use it explicitly
  def addIt(x: String) : Int = 123 + strToInt(x)
}

(new Container[Double]).addIt(123:Short)
(new Container[Double]).addIt("123")

// But still stuck with the fixed return type.

// In List we see
// sum[B >: A](implicit num: Numeric[B]): B


// ** collection methods

val input = List(3, 5, 7, 11)

input.reduce((total, cur) => total + cur)

// More explicitly, initially total is set to head and cur the successor.

input.reduce((total, cur) => { println("t: " + total); println("c: " + cur); total+cur } )

// Define as a function and use infix

def op(total: Int, cur: Int) = total + cur

// Note the use of the 's' to define the string to "interpolate", as they say.
def op1(total: Int, cur: Int) = { println(s"t: $total; c: $cur"); total + cur }

input reduce op

input reduce op1

// This is in fact

op(op(op(3, 5), 7), 11)

// And we are adding up 

(((3 + 5) + 7) + 11)

// But this is simpler

input.sum 

// But reduce can be used more innovatively with its magic _ parameters

def factorial(x: Int) = (2 to x).reduce(_ * _)

factorial(10)

// The same can be achieved with foldLeft using an initial value 

input.foldLeft(0)(op)

(0 :: input).reduce(op)

// empty list is prepended by 0
(0 :: Nil).reduce(op)

// This is slightly different, the 0 is prepended.
input.foldLeft(0)(op1)

// There is a more operator based invocation of foldLeft
(0 /: input)(op)

// Introduce more operators

val reverse = (s: String) => s.reverse
val toUpper = (s: String) => s.toUpperCase
val appendBar = (s: String) => s + "bar"

// Coding imperatively

def applyTransformations(initial: String, transformations: Seq[String => String]) = {
 var cur = initial
 for(transformation <- transformations) {
  cur = transformation(cur)
 }
 cur
}
 
applyTransformations("foo", List(reverse, toUpper, appendBar))

applyTransformations("foo", List(appendBar, reverse, toUpper))

// And here we present a list of appendBar operators
applyTransformations("foo", List.fill(7)(appendBar))

// Using tail-recursion
import scala.annotation.tailrec

@tailrec
def applyTransformations(initial: String, transformations: Seq[String => String]): String =
    transformations match {
        case head :: tail => applyTransformations(head(initial), tail)
        case Nil => initial
    }

// and then

applyTransformations("foo", List(appendBar, reverse, toUpper))


class Factorial2 {
  def factorial(n: Int): Int = {
    @tailrec 
    def factorialAcc(acc: Int, n: Int): Int = {
      if (n <= 1) acc
      else factorialAcc(n * acc, n - 1)
    }
    factorialAcc(1, n)
  }
  def apply(n: Int) = factorial(n)
}

// No companion object to avoid using new
val f2 = new Factorial2
f2.factorial(10)

// But with the apply we can do this
f2(10)


applyTransformations("foo", List(appendBar, reverse, toUpper))

// Signature of foldLeft
// def foldLeft[B](initial: B)(op: (B, A) => B): B

List(reverse, toUpper, appendBar).foldLeft("foo") {
    (cur, transformation) => transformation(cur)
}

// With some trace

def op2(cur: String, f: (String => String)) = { println(s"c: $cur"); f(cur) }

List(reverse, toUpper, appendBar).foldLeft("foo") {
    (cur, transformation) => op2(cur, transformation)
}

// With some trace: using an anonymous function.

List(reverse, toUpper, appendBar).foldLeft("foo") {
    (cur, transformation) => ( (x: String, y: (String => String) ) => { println(x); y(x) } )(cur, transformation)
}

( (x: Int, y: String) => (new StringBuilder()).append(x).append(" ").append(y) ) (1, "this")

// Using compose and initializing foldLeft with the identity function.

def composeAll[A](ts: Seq[A => A]): A => A = ts.foldLeft(identity[A] _)(_ compose _)
 
def applyTransformations(init: String, ts: Seq[String => String]): String = 
  composeAll(ts.reverse)(init)

applyTransformations("foo", List(reverse, toUpper, appendBar))

// Partial function as opposed to using Function1

// These are intended to support failure, unlike Function1. The concept of failure is
// that the function has no mapping for the given failure.

val sample = 1 to 10
val isEven: PartialFunction[Int, String] = {
  case x if x % 2 == 0 => x+" is even"
  // when it falls through here, then you it is has failed.
}

isEven isDefinedAt 3
isEven isDefinedAt 2

// the method collect can use isDefinedAt to select which members to collect
val evenNumbers = sample collect isEven

val isOdd: PartialFunction[Int, String] = {
  case x if x % 2 == 1 => x+" is odd"
}

// the method orElse allows chaining another partial function to handle
// input outside the declared domain
val numbers = sample map (isEven orElse isOdd)

// You can use lift() to make it a function that returns a Some

val isEvenV = isEven.lift

isEvenV(3)

isEvenV(2)

// Run with is a more efficient form of applyOrElse.

def f(s: String) = println("that's true")

isEven.runWith(f)(3)
isEven.runWith(f)(2)

// List work

List('a', 'b', 'c').aggregate(0)({ (sum, ch) => sum + ch.toInt }, { (p1, p2) => p1 + p2 })

val xs = Map("a" -> List(11,111), "b" -> List(22,222)).flatMap(_._2)

val ys = Map("a" -> List(1 -> 11,1 -> 111), "b" -> List(2 -> 22,2 -> 222)).flatMap(_._2)

List(1, 2, 3, 4).fold(0)( (x, y) => x+y)


import scala.collection.mutable
import Numeric._

type NumericList[T] = mutable.MutableList[Numeric[T]] 

var l0 = new NumericList[Int]

// * Paradigmatic 

// ** Imperative style

def fibImper(n: Int): Int = {
  var i = 0
  var j = 1

  for (k <- 0 until n) {
    val l = i + j
    i = j
    j = l
  }
  i
}

// ** Recursive - stack-based, no tail recursion optimization.

def fibRec(n: Int): Int = {
  n match {
    case 0 => 0
    case 1 => 1
    case _ => fib1(n-1) + fib1(n-2)
  }
}

// ** Recursive - with tail recursion optimization.

def fibTailRec(n: Int): Int = {
  @annotation.tailrec 
  def f(a: Int, b: Int, c: Int): Int = if (a == 0) 0 else if(a < 2) c else f(a-1, c, b + c)

  f(n, 0, 1)
}

// ** Recursive - stream implementation

val fibStream:Stream[Int] = 0 #:: 1 #:: (fib4 zip fibs.tail).map{ t => t._1 + t._2 }

lazy val fibs: Stream[Int] = 0 #:: 1 #:: fibs.zip(fibs.tail).map { n => n._1 + n._2 }

// * Pimp my library

// This allows a library implementation to be extended using implicit methods.
// https://alvinalexander.com/scala/scala-2.10-implicit-class-example

// implicit cannot be used for top-level objects.

val result = "HAL".increment

implicit class StringImprovements(s: String) {
  def increment = s.map(c => (c + 1).toChar)
}

val result = "HAL".increment

// ** Using Option

// You may have a larger object, request, that has optional parameters

val name: Option[String] = /* request getParameter */ Option[String]("name")
val upper = name map { _.trim } filter { _.length != 0 } map { _.toUpperCase }
println(upper getOrElse "")

// This can be expressed with a for comprehension

val upper = for {
  name <- /* request getParameter */ Option[String]("name")
  trimmed <- Some(name.trim)
  upper <- Some(trimmed.toUpperCase) if trimmed.length != 0
} yield upper
println(upper getOrElse "")

// Or using match.

val nameMaybe = /* request getParameter */ Option[String]("name")
nameMaybe match {
  case Some(name) =>
    println(name.trim.toUpperCase)
  case None =>
    println("No name value")
}

// ** Polymorphism

// A useful property of Option is to be able to return None of a particular type.

Option.empty[Byte]

trait Container[M[_]] { def put[A](x: A): M[A]; def get[A](m: M[A]): A }

implicit val listContainer = new Container[List] { 
  def put[A](x: A) = List(x); def get[A](m: List[A]) = m.head 
}

implicit val optionContainer = new Container[Option] { 
  def put[A](x: A) = Option(x)
  def get[A](m: Option[A]) = m.get
}

def tupleize[M[_]: Container, A, B](fst: M[A], snd: M[B]) = {
  val c = implicitly[Container[M]]
  c.put(c.get(fst), c.get(snd))
}

// *** Notes
// The Container trait supports put and get of a contained object.
// This means the type can be held in the container.
//
// The listContainer and optionContainer are effectively factory objects (singletons).
//
// The Container is needed because type inference will simplify structure types.

// This returns List[Short], forcing Byte to be a short and losing the type information.
List(1:Short, 1:Byte)

// The idea of the container is to hold the typed data unchanged.
{
  // Using List as a container we can preserve type info.
  val v1 = List(List(1:Short), List(1:Byte))
  println(v1(0)(0).getClass.getName + "; " + v1(1)(0).getClass.getName)
}

// tupleize removes a level of composition.
// and we can use Option 
tupleize(List(1), List(2))

tupleize(Option(1), Option(2))

{
  val v0 = tupleize(Option(1:Short), Option(2:Byte))
  println(v0.get._1.getClass.getName + "; " + v0.get._2.getClass.getName)

}

tupleize(List(1), List(2:Short))

tupleize(Option(1), Option(2:Short))

// To store an empty.

tupleize(Option(1:Short), Option(Option.empty[Byte]) )

tupleize(Option(1:Short), Option(None))

// And not - I have tried to adjust definitions.

tupleize(Option(1:Short), Option.empty[Byte] )

import scala.util.control.Exception._

def toByteOpt(s: String) = optionContainer put catching(classOf[NumberFormatException]) opt s.toByte
def toShortOpt(s: String) = optionContainer put catching(classOf[NumberFormatException]) opt s.toShort
def toIntOpt(s: String) = optionContainer put catching(classOf[NumberFormatException]) opt s.toInt

val l0 = List(toByteOpt _, toShortOpt _, toIntOpt _)

l0.map( _("1234") )

l0.find( !_("1234").isEmpty )


// But this could be used, but the type is inferred as Option[(Any, Any)]

{
  val x1 = tupleize(toByteOpt("1234") orElse Option(None), 
		    toByteOpt("123") orElse Option(None) )
  x1.get._2.getClass.getName
}

// * Typing strings

def toByteOpt(s: String) = catching(classOf[NumberFormatException]) opt s.toByte
def toShortOpt(s: String) = catching(classOf[NumberFormatException]) opt s.toShort
def toIntOpt(s: String) = catching(classOf[NumberFormatException]) opt s.toInt
def toLongOpt(s: String) = catching(classOf[NumberFormatException]) opt s.toLong
def toDoubleOpt(s: String) = catching(classOf[NumberFormatException]) opt s.toDouble
def toStringOpt(s: String) = Option[String](s)

val l0 = List(toByteOpt _, toShortOpt _, toIntOpt _, toLongOpt _, toDoubleOpt _, toStringOpt _)

val v0 = l0.map( _("1234")).find(!_.isEmpty).get.get.getClass.getName

val c0 = Class.forName(v0)

val cons = c0.getConstructor(Class.forName("java.lang.String"))

val s0 = cons.newInstance("1234").asInstanceOf[Short]

def instantiate[T](s: String)(implicit m: Manifest[T]): T = {
  val nm = m.erasure.getName
  val clazz = Class.forName(nm)
  val constructor = clazz.getConstructor(Class.forName("java.lang.String"))
  constructor.newInstance(s).asInstanceOf[T]
}

List(instantiate[java.lang.Short]("1234"), instantiate[java.lang.Integer]("1234") )

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

