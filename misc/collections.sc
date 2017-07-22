// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/collections/parallel.sc

((1 to 10) fold "") ((s1, s2) => s"$s1 - $s2")
// Result: " - 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9 - 10"

((1 to 10).par fold "") ((s1, s2) => s"$s1 - $s2")
// Results for two runs:
// " - 1 -  - 2 -  - 3 - 4 - 5 -  - 6 -  - 7 -  - 8 -  - 9 -  - 10"
// " - 1 -  - 2 -  - 3 -  - 4 - 5 -  - 6 -  - 7 -  - 8 - 9 - 10"

((1 to 10) fold 0) ((s1, s2) => s1 + s2)
// Result: 55

((1 to 10).par fold 0) ((s1, s2) => s1 + s2)
// Result: 55

// ** src/main/scala/progscala2/collections/safeseq/safeseq.sc

val mutableSeq1: Seq[Int] = List(1,2,3,4)
val mutableSeq2: Seq[Int] = Array(1,2,3,4)

import progscala2.collections.safeseq._

val immutableSeq1: Seq[Int] = List(1,2,3,4)
val immutableSeq2: Seq[Int] = Array(1,2,3,4)


// ** src/main/scala/progscala2/collections/ListBuilder.sc
import collection.mutable.Builder

class ListBuilder[T] extends Builder[T, List[T]] {

  private var storage = Vector.empty[T]

  def +=(elem: T) = {
    storage = storage :+ elem
    this
  }

  def clear(): Unit = { storage = Vector.empty[T] }

  def result(): List[T] = storage.toList
}

val lb = new ListBuilder[Int]
(1 to 3) foreach (i => lb += i)
lb.result
// Result: List(1, 2, 3)

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

