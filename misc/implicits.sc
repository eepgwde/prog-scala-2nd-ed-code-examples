// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/implicits/toJSON-type-class.sc

case class Address(street: String, city: String)
case class Person(name: String, address: Address)

trait ToJSON {
  def toJSON(level: Int = 0): String

  val INDENTATION = "  "
  def indentation(level: Int = 0): (String,String) = 
    (INDENTATION * level, INDENTATION * (level+1))
}

implicit class AddressToJSON(address: Address) extends ToJSON {
  def toJSON(level: Int = 0): String = {
    val (outdent, indent) = indentation(level)
    s"""{
      |${indent}"street": "${address.street}", 
      |${indent}"city":   "${address.city}"
      |$outdent}""".stripMargin
  }
}

implicit class PersonToJSON(person: Person) extends ToJSON {
  def toJSON(level: Int = 0): String = {
    val (outdent, indent) = indentation(level)
    s"""{
      |${indent}"name":    "${person.name}", 
      |${indent}"address": ${person.address.toJSON(level + 1)} 
      |$outdent}""".stripMargin
  }
}

val a = Address("1 Scala Lane", "Anytown")
val p = Person("Buck Trends", a)

println(a.toJSON())
println()
println(p.toJSON())

// ** src/main/scala/progscala2/implicits/implicit-args.sc

// Never use Floats for money:
def calcTax(amount: Float)(implicit rate: Float): Float = amount * rate

object SimpleStateSalesTax {
  implicit val rate: Float = 0.05F
}

case class ComplicatedSalesTaxData(
  baseRate: Float,
  isTaxHoliday: Boolean,
  storeId: Int)

object ComplicatedSalesTax {
  private def extraTaxRateForStore(id: Int): Float = {
    // From id, determine location, then extra taxes...
    0.0F
  }

  implicit def rate(implicit cstd: ComplicatedSalesTaxData): Float = 
    if (cstd.isTaxHoliday) 0.0F
    else cstd.baseRate + extraTaxRateForStore(cstd.storeId)
}

{
  import SimpleStateSalesTax.rate

  val amount = 100F
  println(s"Tax on $amount = ${calcTax(amount)}")
}

{
  import ComplicatedSalesTax.rate
  implicit val myStore = ComplicatedSalesTaxData(0.06F, false, 1010)

  val amount = 100F
  println(s"Tax on $amount = ${calcTax(amount)}")
}


// ** src/main/scala/progscala2/implicits/implicit-conversions-resolution2.sc
import scala.language.implicitConversions

case class Foo(s: String)
object Foo {
  implicit def fromString(s: String): Foo = Foo(s)
}

implicit def overridingConversion(s: String): Foo = Foo("Boo: "+s)

class O {
  def m1(foo: Foo) = println(foo)
  def m(s: String) = m1(s)
}

// ** src/main/scala/progscala2/implicits/implicitly-args.sc
import math.Ordering

case class MyList[A](list: List[A]) {
  def sortBy1[B](f: A => B)(implicit ord: Ordering[B]): List[A] =
    list.sortBy(f)(ord)

  def sortBy2[B : Ordering](f: A => B): List[A] =
    list.sortBy(f)(implicitly[Ordering[B]])
}

val list = MyList(List(1,3,5,2,4))

list sortBy1 (i => -i)
list sortBy2 (i => -i)

// ** src/main/scala/progscala2/implicits/type-classes-subtyping2.sc
// Adapted from: src/main/scala/progscala2/implicits/toJSON-type-class.sc

case class Address(street: String, city: String, state: String, zip: String)
case class Person(name: String, age: Int, address: Address)

trait ToJSON[+T] {
  def toJSON(level: Int = 0): String

  val INDENTATION = "  "
  def indentation(level: Int = 0): (String,String) =
    (INDENTATION * level, INDENTATION * (level+1))
}

implicit class AddressToJSON(address: Address) extends ToJSON[Address] {
  def toJSON(level: Int = 0): String = {
    val (outdent, indent) = indentation(level)
    s"""{
      |${indent}"street": "${address.street}",
      |${indent}"city":   "${address.city}",
      |${indent}"state":  "${address.state}",
      |${indent}"zip":    "${address.zip}"
      |$outdent}""".stripMargin
  }
}

implicit class PersonToJSON(person: Person) extends ToJSON[Person] {
  def toJSON(level: Int = 0): String = {
    val (outdent, indent) = indentation(level)
    s"""{
      |${indent}"name":    "${person.name}",
      |${indent}"age":     "${person.age}",
      |${indent}"address": ${new AddressToJSON(person.address).toJSON(level+1)}
      |$outdent}""".stripMargin
  }
}

val a = Address("1 Scala Lane", "Anytown", "CA", "98765")

// We want to use this list:
val list1 = List(a, Person("Buck Trends", 29, a))
// But this attempt to convert to JSON, and other variations, don't work:
list1 map _.toJSON()

// This works, but it's ugly and the list has the *ToJSON objects, not
// the original Address and Person:
val list2: List[ToJSON[_]] = List(a, Person("Buck Trends", 29, a))
list2 map ((x: ToJSON[_]) => x.toJSON())

// So, does this work?
implicit class ToJSONs[+T : ToJSON](seq: Seq[T]) {
  def apply(): Seq[String] =
    seq map (t => implicitly[ToJSON[T]].toJSON())
}

// NO: It complains about that it can't find a matching typeclass instance
// with toJSON.
println(list1.toJSON())

// Does this ugly hack fix it?
implicit class GodToJSON(x: Any) extends ToJSON[Any] {
  def toJSON(level: Int = 0): String = x match {
    case person: Person   => new PersonToJSON(person).toJSON(level)
    case address: Address => new AddressToJSON(address).toJSON(level)
    case other => throw new UnsupportedOperationException(
      s"No toJSON type class instance is available for $other")
  }
}

// NO: We still get the same error.
toJSONs(list1)

// This finally works...
list1 map ((x: Any) => x.toJSON())
// But it only worked because we used this expression in the PersonToJSON,
// ${new AddressToJSON(person.address).toJSON(level+1)}, rather than
// ${person.address.toJSON(level+1)}. For the later, the GodToJSON isn't
// "found" and the "other" exception is thrown when we try to process the
// Person instance.

// ** src/main/scala/progscala2/implicits/implicit-conversions-resolution.sc
import scala.language.implicitConversions

// WARNING: You must :paste mode in the REPL for the following.
// Using :load won't compile the two definitions together!
case class Foo(s: String)
object Foo {
  implicit def fromString(s: String): Foo = Foo(s)
}

class O {
  def m1(foo: Foo) = println(foo)
  def m(s: String) = m1(s)
}

// ** src/main/scala/progscala2/implicits/type-classes-subtyping.sc

trait Stringizer[+T] {
  def stringize: String
}

implicit class AnyStringizer(a: Any) extends Stringizer[Any] {
  def stringize: String = a match {
    case s: String => s
    case i: Int => (i*10).toString
    case f: Float => (f*10.1).toString
    case other => 
      throw new UnsupportedOperationException(s"Can't stringize $other")
  }
}

val list: List[Any] = List(1, 2.2F, "three", 'symbol)

list foreach { (x:Any) => 
  try {
    println(s"$x: ${x.stringize}")
  } catch {
    case e: java.lang.UnsupportedOperationException => println(e)
  }
}

// ** src/main/scala/progscala2/implicits/implicit-erasure.sc
object M {
  implicit object IntMarker                                          // <1>
  implicit object StringMarker

  def m(seq: Seq[Int])(implicit i: IntMarker.type): Unit =           // <2>
    println(s"Seq[Int]: $seq")

  def m(seq: Seq[String])(implicit s: StringMarker.type): Unit =     // <3>
    println(s"Seq[String]: $seq")
}

import M._                                                           // <4>
m(List(1,2,3))
m(List("one", "two", "three"))


// ** src/main/scala/progscala2/implicits/implicit-conversions.sc

object Implicits {
  implicit final class DarthVadarShip[A](val self: A) {
    def <-*-> [B](y: B): Tuple2[A, B] = Tuple2(self, y)
  }
}

import Implicits._

val m = Map("one" <-*-> 1, "two" <-*-> 2)

// ** src/main/scala/progscala2/implicits/custom-string-interpolator.sc
import scala.util.parsing.json._

object Interpolators {
  implicit class jsonForStringContext(val sc: StringContext) {       // <1>
    def json(values: Any*): JSONObject = {                           // <2>
      val keyRE = """^[\s{,]*(\S+):\s*""".r                          // <3>
      val keys = sc.parts map {                                      // <4>
        case keyRE(key) => key
        case str => str
      }
      val kvs = keys zip values                                      // <5>
      JSONObject(kvs.toMap)                                          // <6>
    }
  }
}

import Interpolators._

val name = "Dean Wampler"
val book = "Programming Scala, Second Edition"

val jsonobj = json"{name: $name, book: $book}"                       // <7>
println(jsonobj)

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

