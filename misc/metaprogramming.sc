// * @author weaves

// * Meta-programming

// ** src/main/scala/progscala2/metaprogramming/reflect.sc
import scala.language.existentials

trait T0[A] {
  val vT: A
  def mT = vT
}

class C(foo: Int) extends T0[String] {
  val vT = "T0"
  val vC = "C"
  def mC = vC

  class C2
}

val c = new C(3)

val clazz = classOf[C]              // Scala method: classOf[C] ~ C.class
val clazz2 = c.getClass             // Method from java.lang.Object

// Methods from java.lang.Class<T>:
val name  = clazz.getName
val methods = clazz.getMethods
val ctors = clazz.getConstructors
val fields = clazz.getFields
val annos = clazz.getAnnotations
val interfaces = clazz.getInterfaces
val superClass = clazz.getSuperclass
val typeParams = clazz.getTypeParameters

// *** Notes
// These methods don't limit themselves to Scala methods. 
// The contructor is correct, but the fields doesn't show anything. 
//
// The getMethods lists all the under-lying Java object methods and the accessor for
// both the trait T and C as belonging to C.
//
// The trait T appears as an interface.

// ** src/main/scala/progscala2/metaprogramming/match-types.sc
// NOTE: This example is actually nonsense. I don't know what I was thinking.
// implicitly(seq.head) just returns seq.head and that call to `getClass` does
// the real work I was intending `ClassTag` to do. So, ignore this and look at
// mkArray.sc in this package instead, which is far better.
import scala.reflect.ClassTag

def useClassTag[T : ClassTag](seq: Seq[T]): String = seq match { // <1>
  case Nil => "Nothing"
  case head +: _ => implicitly(seq.head).getClass.toString           // <2>
}

def check(seq: Seq[_]): String =                                     // <3>
  s"Seq: ${useClassTag(seq)}"

Seq(Seq(5.5,5.6,5.7), Seq("a", "b"),                                 // <4>
    Seq(1, "two", 3.14), Nil) foreach {
  case seq: Seq[_] => println("%20s:  %s".format(seq, check(seq)))
  case x           => println("%20s:  %s".format(x, "unknown!"))
}

// ** src/main/scala/progscala2/metaprogramming/mkArray.sc
import scala.reflect.ClassTag

def mkArray[T : ClassTag](elems: T*) = Array[T](elems: _*)

mkArray(1, 2, 3)
mkArray("one", "two", "three")
mkArray(1, "two", 3.14)

// ** src/main/scala/progscala2/metaprogramming/match-type-tags.sc
import scala.reflect.runtime.universe._                              // <1>

def toType2[T](t: T)(implicit tag: TypeTag[T]): Type = tag.tpe       // <2>
def toType[T : TypeTag](t: T): Type = typeOf[T]                      // <3>

/**
 * Return a tuple of the type "prefix", the type's symbol and its
 * possibly empty list of type parameters.
 */
def toTypeRefInfo[T : TypeTag](x: T): (Type, Symbol, Seq[Type]) = {  // <4>
  val TypeRef(pre, typName, parems) = toType(x)
  (pre, typName, parems)
}

toType(1)
toType(true)
toType(Seq(1, true, 3.14))
toType((i: Int) => i.toString)

toType(1) <:< typeOf[AnyVal]           // => true
toType(1) <:< toType(1)                // => true
toType(1) <:< toType(true)             // => false

toType(1) =:= typeOf[AnyVal]           // => false
toType(1) =:= toType(1)                // => true
toType(1) =:= toType(true)             // => false

toTypeRefInfo(1)                       // (scala.type, class Int, List())
toTypeRefInfo(true)                    // (scala.type, class Boolean, List())
toTypeRefInfo(Seq(1, true, 3.14))      // (scala.collection.type, trait Seq,
                                       //    List(AnyVal))
toTypeRefInfo((i: Int) => i.toString)  // (scala.type, trait Function1,
                                       //    List(Int, java.lang.String))

// *** Note
// 

val t1 = toType(1)
val ts = toType(Seq(1, true, 3.14))
val tf = toType((i: Int) => i.toString)

// Use the reflect.api.types$TypeApi:
t1.typeSymbol
ts.typeSymbol
tf.typeSymbol

t1.erasure
ts.erasure
tf.erasure

t1.typeArgs
ts.typeArgs
tf.typeArgs

t1.typeParams
ts.typeParams
tf.typeParams

t1.baseClasses
ts.baseClasses
tf.baseClasses

t1.companion
ts.companion
tf.companion

t1.decls
ts.decls
tf.decls

t1.members
ts.members
tf.members

// ** src/main/scala/progscala2/metaprogramming/quasiquotes.sc
import reflect.runtime.universe._

// Bring in the "toolbox":
import reflect.runtime.currentMirror
import tools.reflect.ToolBox
val toolbox = currentMirror.mkToolBox()

val C = q"case class C(s: String)"
showCode(C)
showRaw(C)

// q != tq
val  q =  q"List[String]"
val tq = tq"List[String]"
showRaw(q)
showRaw(tq)
q equalsStructure tq

// "Unquoting", analogous to textual substitution in source code.
Seq(tq"Int", tq"String") map { param =>
  q"case class C(s: $param)"
} foreach { q =>
  println(showCode(q))
}

// Lifting
val list = Seq(1,2,3,4)
val fmt = "%d, %d, %d, %d"
val printq = q"println($fmt, ..$list)"

// Unlifting (pattern matching, too)
val q"${i: Int} + ${d: Double}" = q"1 + 3.14"

// ** src/main/scala/progscala2/metaprogramming/func.sc

class CSuper                { def msuper() = println("CSuper") }
class C      extends CSuper { def m()      = println("C") }
class CSub   extends C      { def msub()   = println("CSub") }

typeOf[C      => C     ] =:= typeOf[C => C]      // true   <1>
typeOf[CSuper => CSub  ] =:= typeOf[C => C]      // false
typeOf[CSub   => CSuper] =:= typeOf[C => C]      // false

typeOf[C      => C     ] <:< typeOf[C => C]      // true   <2>
typeOf[CSuper => CSub  ] <:< typeOf[C => C]      // true   <3>
typeOf[CSub   => CSuper] <:< typeOf[C => C]      // false  <4>

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

