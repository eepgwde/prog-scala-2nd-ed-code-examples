// src/main/scala/FP/recursion/factorial-recur1.sc

import scala.annotation.tailrec

// What happens if you uncomment the annotation??
// @tailrec
def factorial(i: BigInt): BigInt = i match {
  case _ if i == 1 => i
  case _ => i * factorial(i - 1)
}

for (i <- 1 to 10)
  println(s"$i: ${factorial(i)}")
