// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/typesystem/lazy/lazy-ints.sc

def from(n: Int): Stream[Int] = Stream.cons(n, from(n+1))

lazy val ints = from(0)
lazy val odds = ints.filter(_ % 2 == 1)
lazy val evens = ints.filter(_ % 2 == 0)

odds.take(10).print
evens.take(10).print

// ** src/main/scala/progscala2/typesystem/lazy/lazy-fibonacci.sc

lazy val fib: Stream[Int] = 
  Stream.cons(0, Stream.cons(1, fib.zip(fib.tail).map(p => p._1 + p._2)))
  
fib.take(10).print

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

