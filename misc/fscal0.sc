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



// ** src/main/scala/effective/variance

// Immutable collections should be covariant. Methods that receive the
// contained type should “downgrade” the collection appropriately:


trait Collection[+T] {
  def add[U >: T](other: U): Collection[U]
}



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

