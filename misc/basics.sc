// * @author weaves

// * Functional programming basics

// ** src/main/scala/progscala2/fp/basics/hofs-example.sc

// This multiplies the even numbers by 2 and then multiplies them together.

(1 to 10) filter (_ % 2 == 0) map (_ * 2) reduce (_ * _)


// ** src/main/scala/progscala2/fp/basics/hofs-closure-example.sc

var factor = 2
val multiplier = (i: Int) => i * factor

(1 to 10) filter (_ % 2 == 0) map multiplier reduce (_ * _)

// *** Note
// This does the same as the prior example, but uses a lambda parameter by factor.

factor = 3

(1 to 10) filter (_ % 2 == 0) map multiplier reduce (_ * _)

// This demonstrates how change the factor 

// ** src/main/scala/progscala2/fp/basics/hofs-closure3-example.sc

object Multiplier {
  var factor = 2
  // Compare: val multiplier = (i: Int) => i * factor
  def multiplier(i: Int) = i * factor
}

(1 to 10) filter (_ % 2 == 0) map Multiplier.multiplier reduce (_ * _)

Multiplier.factor = 3
(1 to 10) filter (_ % 2 == 0) map Multiplier.multiplier reduce (_ * _)

// This demonstrates how to use a companion object to achieve the same.

// ** src/main/scala/progscala2/fp/basics/hofs-closure2-example.sc

def m1 (multiplier: Int => Int) = {
  (1 to 10) filter (_ % 2 == 0) map multiplier reduce (_ * _)
}

def m2: Int => Int = {
  val factor = 2
  val multiplier = (i: Int) => i * factor
  multiplier
}

m1(m2)

// This demonstrates how to pass how to pass a named function onto a wrapper.

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

