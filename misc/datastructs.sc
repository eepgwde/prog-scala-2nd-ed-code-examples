// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/fp/datastructs/seq.sc

val seq1 = Seq("Programming", "Scala")                               // <1>
val seq2 = "People" +: "should" +: "read" +: seq1                    // <2>

val seq3 = "Programming" +: "Scala" +: Seq.empty                     // <3>
val seq4 = "People" +: "should" +: "read" +: Seq.empty
val seq5 = seq4 ++ seq3                                              // <4>

// ** src/main/scala/progscala2/fp/datastructs/fold-map.sc

(List(1, 2, 3, 4, 5, 6) foldRight List.empty[String]) {
  (x, list) => ("[" + x + "]") :: list
}

// ** src/main/scala/progscala2/fp/datastructs/set.sc

val states = Set("Alabama", "Alaska", "Wyoming")

val lengths = states map (st => st.length)
println(lengths)

val states2 = states + "Virginia"
println(states2)

val states3 = states2 + ("New York", "Illinois")
println(states3)

// ** src/main/scala/progscala2/fp/datastructs/filter.sc

val stateCapitals = Map(
  "Alabama" -> "Montgomery",
  "Alaska"  -> "Juneau",
  "Wyoming" -> "Cheyenne")
  
val map2 = stateCapitals filter { kv => kv._1 startsWith "A" }

println( map2 )

// ** src/main/scala/progscala2/fp/datastructs/fibonacci.sc
import scala.math.BigInt

val fibs: Stream[BigInt] =
  BigInt(0) #:: BigInt(1) #:: fibs.zip(fibs.tail).map (n => n._1 + n._2)

fibs take 10 foreach (i => print(s"$i "))

// ** src/main/scala/progscala2/fp/datastructs/map.sc

val stateCapitals = Map(
  "Alabama" -> "Montgomery",
  "Alaska"  -> "Juneau",
  "Wyoming" -> "Cheyenne")

val lengths = stateCapitals map { 
  kv => (kv._1, kv._2.length) 
}
println(lengths)

val caps = stateCapitals map { 
  case (k, v) => (k, v.toUpperCase)
}
println(caps)

val stateCapitals2 = stateCapitals + ("Virginia" -> "Richmond")
println(stateCapitals2)

val stateCapitals3 = stateCapitals2 + (
  "New York" -> "Albany", "Illinois" -> "Springfield")
println(stateCapitals3)

// ** src/main/scala/progscala2/fp/datastructs/fold-impl.sc

// Simplified implementation. Does not output the actual collection type
// that was input as Seq[A].
def reduceLeft[A,B](s: Seq[A])(f: A => B): Seq[B] = {
  @annotation.tailrec
  def rl(accum: Seq[B], s2: Seq[A]): Seq[B] = s2 match {
    case head +: tail => rl(f(head) +: accum, tail)
    case _ => accum
  }
  rl(Seq.empty[B], s)
}

def reduceRight[A,B](s: Seq[A])(f: A => B): Seq[B] = s match {
  case head +: tail => f(head) +: reduceRight(tail)(f)
  case _ => Seq.empty[B]
}

val list = List(1,2,3,4,5,6)

reduceLeft(list)(i => 2*i)
// => List(12, 10, 8, 6, 4, 2)

reduceRight(list)(i => 2*i)
// => List(2, 4, 6, 8, 10, 12)
// ** src/main/scala/progscala2/fp/datastructs/foreach.sc

List(1, 2, 3, 4, 5) foreach { i => println("Int: " + i) }

val stateCapitals = Map(
  "Alabama" -> "Montgomery",
  "Alaska"  -> "Juneau",
  "Wyoming" -> "Cheyenne")

//stateCapitals foreach { kv => println(kv._1 + ": " + kv._2) }
stateCapitals foreach { case (k, v) => println(k + ": " + v) }

// ** src/main/scala/progscala2/fp/datastructs/list.sc

val list1 = List("Programming", "Scala")                             // <1>
val list2 = "People" :: "should" :: "read" :: list1                  // <2>

val list3 = "Programming" :: "Scala" :: Nil                          // <3>
val list4 = "People" :: "should" :: "read" :: Nil
val list5 = list4 ++ list3                                           // <4>

// ** src/main/scala/progscala2/fp/datastructs/foldreduce.sc

List(1,2,3,4,5,6) reduce (_ + _)

List(1,2,3,4,5,6).fold (10) (_ * _)
(List(1,2,3,4,5,6) fold 10) (_ * _)

val fold1 = (List(1,2,3,4,5,6) fold 10) _
fold1(_ * _)

(List.empty[Int] fold 10) (_ + _)
try {
  List.empty[Int] reduce (_ + _)
} catch {
  case e: java.lang.UnsupportedOperationException => e
}

List.empty[Int] optionReduce (_ + _)
List(1,2,3,4,5,6) optionReduce (_ * _)

// ** src/main/scala/progscala2/fp/datastructs/vector.sc

val vect1 = Vector("Programming", "Scala")
val vect2 = "People" +: "should" +: "read" +: vect1
println(vect2)

val vect3 = "Programming" +: "Scala" +: Vector.empty[String]
val vect4 = "People" +: "should" +: "read" +: Vector.empty[String]
val vect5 = vect4 ++ vect3
println(vect5)

// ** src/main/scala/progscala2/fp/datastructs/flatmap.sc

val list = List("now", "is", "", "the", "time")

list flatMap (s => s.toList)

// Like flatMap, but flatMap eliminates the intermediate step!
val list2 = List("now", "is", "", "the", "time") map (s => s.toList)
list2.flatten
// ** src/main/scala/progscala2/fp/datastructs/fold-vector-impl.sc

// Specific implementation for Vectors where we append new elements,
// not prepend them like we did in fold-impl.sc for Seqs.
def reduceLeftV[A,B](s: Vector[A])(f: A => B): Vector[B] = {
  @annotation.tailrec
  def rl(accum: Vector[B], s2: Vector[A]): Vector[B] = s2 match {
    case head +: tail => rl(accum :+ f(head), tail)
    case _ => accum
  }
  rl(Vector.empty[B], s)
}

def reduceRightV[A,B](s: Vector[A])(f: A => B): Vector[B] = s match {
  case head +: tail => reduceRightV(tail)(f) :+ f(head)
  case _ => Vector.empty[B]
}

val vect = Vector(1,2,3,4,5,6)

reduceLeftV(vect)(i => 2*i)
// => Vector(2, 4, 6, 8, 10, 12)

reduceRightV(vect)(i => 2*i)
// => Vector(12, 10, 8, 6, 4, 2)
// ** src/main/scala/progscala2/fp/datastructs/fold-assoc-funcs.sc

// fac: a func. that is associative and commutative.
// Define left and right versions, because reduceLeft takes the accumulator
// as the first argument, while reduceRight takes it as the second arg.
val facLeft  = (accum: Int, x: Int) => accum + x
val facRight = (x: Int, accum: Int) => accum + x

val list1 = List(1,2,3,4,5)

list1 reduceLeft  facLeft   // = 15
list1 reduceRight facRight  // = 15

// fnc: a func. that is associative but not commutative:
val fncLeft  = (accum: Int, x: Int) => accum - x
val fncRight = (x: Int, accum: Int) => accum - x

list1 reduceLeft  fncLeft   // = -13
list1 reduceRight fncRight  // = -5

// reduceLeft was this:
((((1 - 2) - 3) - 4) - 5)      // = -13
// reduceRight was this:
((((5 - 4) - 3) - 2) - 1)      // = -5
// or put another way, with the numbers in their original order:
(-1 + (-2 + (-3 + (-4 + 5))))  // = -5

// But "x - y" is associative if we note that "x - y" == "x + -y":
((((1 - 2) - 3) - 4) - 5)      // = -13
((((1 + -2) + -3) + -4) + -5)  // = -13
(1 + (-2 + (-3 + (-4 + -5))))  // = -13

// fnac: a func. that is neither associative nor commutative.
val fnacLeft  = (x: String, y: String) => s"($x)-($y)"
val fnacRight = (x: String, y: String) => s"($y)-($x)"

val list2 = list1 map (_.toString)

list2 reduceLeft  fnacLeft   // = ((((1)-(2))-(3))-(4))-(5)
list2 reduceRight fnacRight  // = ((((5)-(4))-(3))-(2))-(1)
list2 reduceRight fnacLeft   // = (1)-((2)-((3)-((4)-(5))))


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

