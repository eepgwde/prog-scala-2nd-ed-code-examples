// * @author weaves

// * Functional object programming: example with Observers and notifyObservers

// Structural types have internal structure.

// ** src/main/scala/progscala2/typesystem/structuraltypes/SubjectFunc.sc

import progscala2.typesystem.structuraltypes.SubjectFunc

val observer: Int => Unit = (state: Int) => println("got one! "+state)

val subject = new SubjectFunc {
  type State = Int
  protected var count = 0

  def increment(): Unit = {
    count += 1
    notifyObservers(count)
  }
}

// *** Note
// The import of reflectiveCalls is needed and the observer is a simple implementation.

/* 

<console>:39: warning: reflective access of structural type member method increment should be enabled
by making the implicit value scala.language.reflectiveCalls visible.
This can be achieved by adding the import clause 'import scala.language.reflectiveCalls'
or by setting the compiler option -language:reflectiveCalls.
See the Scaladoc for value scala.language.reflectiveCalls for a discussion
why the feature should be explicitly enabled.
       subject.increment()

See following.

*/

subject.increment()

subject.increment()

subject.addObserver(observer)

subject.increment()

subject.increment()

// ** src/main/scala/progscala2/typesystem/structuraltypes/Observer.sc

import scala.language.reflectiveCalls

import progscala2.typesystem.structuraltypes.Subject

object observer {                                                    // <1>
  def receiveUpdate(state: Any): Unit = println("got one! "+state)
}

val subject = new Subject {                                          // <2>
  type State = Int
  protected var count = 0

  def increment(): Unit = {
    count += 1
    notifyObservers(count)
  }
}

subject.increment()
subject.increment()

subject.addObserver(observer)

subject.increment()
subject.increment()

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

