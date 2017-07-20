// @author weaves
//
// Introductory examples.


// wildcard type.
def f0(m:Map[_ <: AnyRef,_ <: AnyRef]):Int = {
  println(m.size)
  m.size
}

1 + 2

val m = sys.env

m.foreach( x => println(s"$x._1 -> $x._2"))

for ((k,v) <- m) printf("key: %s, value: %s\n", k, v)

m foreach {case (key, value) => println (key + "-->" + value)}
