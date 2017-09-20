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

// Using compose and initializing foldLeft with the identity function.

def composeAll[A](ts: Seq[A => A]): A => A = ts.foldLeft(identity[A] _)(_ compose _)
 
def applyTransformations(init: String, ts: Seq[String => String]): String = 
  composeAll(ts.reverse)(init)

applyTransformations("foo", List(reverse, toUpper, appendBar))



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

