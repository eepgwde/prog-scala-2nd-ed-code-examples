// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/typesystem/valuetypes/type-projection.sc
import progscala2.typesystem.valuetypes._

val l1: Service.Log   = new ConsoleLogger    // ERROR: No Service "value"
val l2: Service1.Log  = new ConsoleLogger    // ERROR: No Service1 "value"
val l3: Service#Log   = new ConsoleLogger    // ERROR: Type mismatch
val l4: Service1#Log  = new ConsoleLogger    // Works!


// ** src/main/scala/progscala2/typesystem/valuetypes/type-types.sc
import progscala2.typesystem.valuetypes._

val s11 = new Service1
val s12 = new Service1

val l1: Logger = s11.logger                      // Okay
val l2: Logger = s12.logger                      // Okay

val l11: s11.logger.type = s11.logger            // Okay
val l12: s11.logger.type = s12.logger            // ERROR

// ** src/main/scala/progscala2/typesystem/valuetypes/object-types.sc

case object Foo { override def toString = "Foo says Hello!" }

def printFoo(foo: Foo.type) = println(foo)

printFoo(Foo)                          // "Foo says Hello!"


case class C(s: String)
val c1 = C("c1")

val c1b: c1.type = c1
val c1b: c1.type = C("c1b")            // Error

// ** src/main/scala/progscala2/typesystem/valuetypes/curried-function.sc

val f  = (x: Double, y: Double, z: Double) => x * y / z
val fc = f.curry

val answer1 = f(2., 5., 4.)
val answer2 = fc(2.)(5.)(4.)
println( answer1 + " == " + answer2 + "? " + (answer1 == answer2))

val fc1 = fc(2.)
val fc2 = fc1(5.)
val answer3 = fc2(4.)
println( answer3 + " == " + answer2 + "? " + (answer3 == answer2))

// ** src/main/scala/progscala2/typesystem/valuetypes/infix-types.sc

val xll1:  Int Either Double  Either String  = Left(Left(1))
val xll2: (Int Either Double) Either String  = Left(Left(1))

val xlr1:  Int Either Double  Either String  = Left(Right(3.14))
val xlr2: (Int Either Double) Either String  = Left(Right(3.14))

val xr1:   Int Either Double  Either String  = Right("foo")
val xr2:  (Int Either Double) Either String  = Right("foo")

val xl:   Int Either (Double Either String)  = Left(1)
val xrl:  Int Either (Double Either String)  = Right(Left(3.14))
val xrr:  Int Either (Double Either String)  = Right(Right("bar"))

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

