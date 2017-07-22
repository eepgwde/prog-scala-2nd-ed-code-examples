// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/typesystem/bounds/view-bounds.sc
import scala.language.implicitConversions

object Serialization {
  case class Writable(value: Any) {
    def serialized: String = s"-- $value --"                         // <1>
  }

  implicit def fromInt(i: Int) = Writable(i)                         // <2>
  implicit def fromFloat(f: Float) = Writable(f)
  implicit def fromString(s: String) = Writable(s)
}

import Serialization._

object RemoteConnection {                                            // <3>
  def write[T <% Writable](t: T): Unit =                             // <4>
    println(t.serialized)  // Use stdout as the "remote connection"
}

RemoteConnection.write(100)       // Prints -- 100 --
RemoteConnection.write(3.14f)     // Prints -- 3.14 --
RemoteConnection.write("hello!")  // Prints -- hello! --
// RemoteConnection.write((1, 2))                                       <5>

// ** src/main/scala/progscala2/typesystem/bounds/list/list-ab.sc
import progscala2.typesystem.bounds.list._

val languages = AbbrevList("Scala", "Java", "Ruby", "C#", "C++", "Python")
val list = 3.14 :: languages
println(list)

// ** src/main/scala/progscala2/typesystem/bounds/lower-bounds.sc

class Parent(val value: Int) {                   // <1>
  override def toString = s"${this.getClass.getName}($value)" 
}
class Child(value: Int) extends Parent(value)

val op1: Option[Parent] = Option(new Child(1))   // <2>     Some(Child(1))
val p1: Parent = op1.getOrElse(new Parent(10))   // Result: Child(1)

val op2: Option[Parent] = Option[Parent](null)   // <3>     None
val p2a: Parent = op2.getOrElse(new Parent(10))  // Result: Parent(10)
val p2b: Parent = op2.getOrElse(new Child(100))  // Result: Child(100)

val op3: Option[Parent] = Option[Child](null)    // <4>     None
val p3a: Parent = op3.getOrElse(new Parent(20))  // Result: Parent(20)
val p3b: Parent = op3.getOrElse(new Child(200))  // Result: Child(200)

// ** src/main/scala/progscala2/typesystem/bounds/lower-bounds2.sc

// Won't compile, because if +A is used, then "default" in +getOrElse+
// must be a of type B, where B >: A.
// case class Opt[+A](value: A = null) {
//   def getOrElse(default: A): A = if (value != null) value else default 
// }

case class Opt[A](value: A = null) {
  def getOrElse(default: A): A = if (value != null) value else default 
}

class Parent(val value: Int) {                             // <1>
  override def toString = s"${this.getClass.getName}($value)" 
}
class Child(value: Int) extends Parent(value)

val p4: Parent = Opt(new Child(1)).getOrElse(new Parent(10))
val p5: Parent = Opt[Parent](null).getOrElse(new Parent(10))
val p6: Parent = Opt[Child](null).getOrElse(new Parent(10))

// ** src/main/scala/progscala2/typesystem/bounds/view-to-context-bounds.sc
import scala.language.implicitConversions

object Serialization {
  case class Rem[A](value: A) {
    def serialized: String = s"-- $value --"
  }
  type Writable[A] = A => Rem[A]                                     // <1>
  implicit val fromInt: Writable[Int]       = (i: Int)    => Rem(i)
  implicit val fromFloat: Writable[Float]   = (f: Float)  => Rem(f)
  implicit val fromString: Writable[String] = (s: String) => Rem(s)
}

import Serialization._

object RemoteConnection {
  def write[T : Writable](t: T): Unit =                              // <2>
    println(t.serialized)  // Use stdout as the "remote connection"
}

RemoteConnection.write(100)       // Prints -- 100 --                   <3>
RemoteConnection.write(3.14f)     // Prints -- 3.14 --
RemoteConnection.write("hello!")  // Prints -- hello! --
// RemoteConnection.write((1, 2))

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

