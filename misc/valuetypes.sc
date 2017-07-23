// * @author weaves

// * Type system

// Taking types from objects. Using Left and Right and Either.

// ** src/main/scala/progscala2/typesystem/valuetypes/object-types.sc

// *** Dynamic typing

case object Foo { override def toString = "Foo says Hello!" }

def printFoo(foo: Foo.type) = println(foo)

printFoo(Foo)                          // "Foo says Hello!"

// **** Note

// This demonstrates you can define functions using the dynamic type of an
// object. That is, you don't need to know the type in advance.

// *** Dynamic typing

case class C(s: String)
val c1 = C("c1")

val c1b: c1.type = c1

val c1b: c1.type = C("c1b")            // Error

var c1bb: c1.type = c1

var c1bb: c1.type = C("c1b")            // Error

// **** Note

// This allows one to define types that are specific to the value held by an object.
// But once assigned, it cannot be reassigned.
// It isn't a feature of the immutability of the object.

// ** src/main/scala/progscala2/typesystem/valuetypes/curried-function.sc

// *** Currying

val f  = (x: Double, y: Double, z: Double) => x * y / z
val fc = f.curried

val answer1 = f(2, 5, 4)

val answer2 = fc(2)(5)(4)			  

// Effectively, this is fc(fc(fc(2), 5),4)

println( answer1 + " == " + answer2 + "? " + (answer1 == answer2))

// **** Note
// The curried method comes from 
// http://www.scala-lang.org/api/2.11.8/index.html#scala.Function2

// *** Using currying in parts

val fc1 = fc(2)

val fc2 = fc1(5)

val answer3 = fc2(4)

println( answer3 + " == " + answer2 + "? " + (answer3 == answer2))

// **** Note
// Currying is a method on the functions class that allows a function to be divided
// up. 

// ** src/main/scala/progscala2/typesystem/valuetypes/infix-types.sc

// These provide a means of wrapping arguments.
// And can be used in a similar way to the Option/Some/None pattern

val xll1:  Int Either Double  Either String  = Left(Left(1))
val xll2: (Int Either Double) Either String  = Left(Left(1))

val xlr1:  Int Either Double  Either String  = Left(Right(3.14))
val xlr2: (Int Either Double) Either String  = Left(Right(3.14))

val xr1:   Int Either Double  Either String  = Right("foo")
val xr2:  (Int Either Double) Either String  = Right("foo")

val xl:   Int Either (Double Either String)  = Left(1)
val xrl:  Int Either (Double Either String)  = Right(Left(3.14))
val xrr:  Int Either (Double Either String)  = Right(Right("bar"))

// *** Either, Left and Right provide an method for trapping exceptions

object EitherLeftRightExample {

  /**
   * A simple method to demonstrate how to declare that a method returns an Either,
   * and code that returns a Left or Right.
   */
  def divideXByY(x: Int, y: Int): Either[String, Int] = {
    if (y == 0) Left("Dude, can't divide by 0")
    else Right(x / y)
  }
  
  // a few different ways to use Either, Left, and Right
  println(divideXByY(1, 0))
  println(divideXByY(1, 1))
  divideXByY(1, 0) match {
    case Left(s) => println("Answer: " + s)
    case Right(i) => println("Answer: " + i)
  }

}

// And call it, the code is called by the constructor.
val eg0 = EitherLeftRightExample

// ** src/main/scala/progscala2/typesystem/valuetypes/type-types.sc

// Note: be sure to be in the sbt console

import progscala2.typesystem.valuetypes._

val s11 = new Service1
val s12 = new Service1

val l1: Logger = s11.logger                      // Okay
val l2: Logger = s12.logger                      // Okay

val l11: s11.logger.type = s11.logger            // Okay
val l12: s11.logger.type = s12.logger            // ERROR

// Refer to the file
// src/main/scala/progscala2/typesystem/valuetypes/type-projection.scala
// This demonstrates that the type keyword has error-checking.

// ** src/main/scala/progscala2/typesystem/valuetypes/type-projection.sc

import progscala2.typesystem.valuetypes._

val l1: Service.Log   = new ConsoleLogger    // ERROR: No Service "value"
val l2: Service1.Log  = new ConsoleLogger    // ERROR: No Service1 "value"
val l3: Service#Log   = new ConsoleLogger    // ERROR: Type mismatch
val l4: Service1#Log  = new ConsoleLogger    // Works!

// This demonstrates that a type object defined in a class can be changed.

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

