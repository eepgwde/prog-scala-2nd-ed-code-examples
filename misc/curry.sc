// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/fp/curry/curried-func2.sc

val f1: String =>  String => String  = (s1: String) => (s2: String) => s1 + s2
val f2: String => (String => String) = (s1: String) => (s2: String) => s1 + s2

f1("hello")("world")
// helloworld

f2("hello")("world")
// helloworld

val ff1 = Function.uncurried(f1)
val ff2 = Function.uncurried(f2)

ff1("hello", "world")
// helloworld

ff2("hello", "world")
// helloworld
// ** src/main/scala/progscala2/fp/curry/lifted-func.sc

val finicky: PartialFunction[String,String] = {
  case "finicky" => "FINICKY"
}

val finickyOption = finicky.lift

finicky("finicky")
try {
  finicky("other")
} catch {
  case e: scala.MatchError => e
}

finickyOption("finicky")
finickyOption("other")

val finicky2 = Function.unlift(finickyOption)

finicky2("finicky")
try {
  finicky2("other")
} catch {
  case e: scala.MatchError => e
}

// ** src/main/scala/progscala2/fp/curry/tupled-func.sc

def mult(d1: Double, d2: Double, d3: Double) = d1 * d2 * d3

val d3 = (2.2, 3.3, 4.4)

mult(d3._1, d3._2, d3._3)

val multTupled = Function.tupled(mult _)
// multTupled: ((Double, Double, Double)) => Double = <function1>

multTupled(d3)

val multUntupled = Function.untupled(multTupled)

multUntupled(d3._1, d3._2, d3._3)

// ** src/main/scala/progscala2/fp/curry/curried-func.sc

def cat1(s1: String)(s2: String) = s1 + s2
def cat2(s1: String) = (s2: String) => s1 + s2
def cat3(s1: String, s2: String) = s1 + s2

val cat3Curried = (cat3 _).curried
// cat3Curried: String => (String => String) = <function1>

cat3Curried("hello")("world")
// helloworld

cat3("hello", "world")
// helloworld

val cat3Uncurried = Function.uncurried(cat3Curried)
// cat3Uncurried: (String, String) => String = <function2>

cat3Uncurried("hello", "world")
// helloworld
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

