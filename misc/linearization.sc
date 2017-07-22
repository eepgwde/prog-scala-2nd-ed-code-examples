// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/objectsystem/linearization/value-class-universal-traits.sc

trait M extends Any {
  def m = print("M ")
}

trait Digitizer extends Any with M {
  override def m = { print("Digitizer "); super.m }

  def digits(s: String): String = s.replaceAll("""\D""", "")
}

trait Formatter extends Any with M {   
  override def m = { print("Formatter "); super.m }

  def format(areaCode: String, exchange: String, subnumber: String): String =
    s"($areaCode) $exchange-$subnumber"
}

// Split "exetnds AnyVal" to tell the REPL the expression crosses 2 lines:
class USPhoneNumber(val s: String) extends 
    AnyVal with Digitizer with Formatter{
  override def m = { print("USPhoneNumber "); super.m }
  
  override def toString = {
    val digs = digits(s)
    val areaCode = digs.substring(0,3)
    val exchange = digs.substring(3,6)
    val subnumber  = digs.substring(6,10)
    format(areaCode, exchange, subnumber)
  }
}

val number = new USPhoneNumber("987-654-3210")
println("Call m:")
number.m

// ** src/main/scala/progscala2/objectsystem/linearization/linearization3.sc

class C1 {
  def m(previous: String) = print(s"C1($previous)")
}

trait T1 extends C1 {
  override def m(p: String) = { super.m(s"T1($p)") }
}

trait T2 extends C1 {
  override def m(p: String) = { super.m(s"T2($p)") }
}

trait T3 extends C1 {
  override def m(p: String) = { super.m(s"T3($p)") }
}

class C2 extends T1 with T2 with T3 {
  override def m(p: String) = { super.m(s"C2($p)") }
}

val c2 = new C2
c2.m("")

// ** src/main/scala/progscala2/objectsystem/linearization/linearization4.sc

class C1 {
  def m = print("C1 ")
}

trait T1 extends C1 {
  override def m = { print("T1 "); super.m }
}

trait T2 extends C1 {
  override def m = { print("T2 "); super.m }
}

trait T3 extends C1 {
  override def m = { print("T3 "); super.m }
}

class C2A extends T2 {
  override def m = { print("C2A " ); super.m }
}

class C2 extends C2A with T1 with T2 with T3 {
  override def m = { print("C2 "); super.m }
}

def calcLinearization(obj: C1, name: String) = {
  print(s"$name: ")
  obj.m
  print("AnyRef ")
  println("Any")
}
    
calcLinearization(new C2, "C2 ")
println("")
calcLinearization(new T3 {}, "T3 ")
calcLinearization(new T2 {}, "T2 ")
calcLinearization(new T1 {}, "T1 ")
calcLinearization(new C2A, "C2A")
calcLinearization(new C1, "C1 ")

// ** src/main/scala/progscala2/objectsystem/linearization/linearization1.sc

class C1 {
  def m = print("C1 ")
}

trait T1 extends C1 {
  override def m = { print("T1 "); super.m }
}

trait T2 extends C1 {
  override def m = { print("T2 "); super.m }
}

trait T3 extends C1 {
  override def m = { print("T3 "); super.m }
}

class C2 extends T1 with T2 with T3 {
  override def m = { print("C2 "); super.m }
}

val c2 = new C2
c2.m

// ** src/main/scala/progscala2/objectsystem/linearization/linearization2.sc

class C1 {
  print("C1 ")
}

trait T1 extends C1 {
  print("T1 ")
}

trait T2 extends C1 {
  print("T2 ")
}

trait T3 extends C1 {
  print("T3 ")
}

class C2 extends T1 with T2 with T3 {
  println("C2 ")
}

val c2 = new C2

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

