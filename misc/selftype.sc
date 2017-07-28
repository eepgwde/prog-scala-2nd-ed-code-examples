// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/typesystem/selftype/ButtonSubjectObserver.sc
import progscala2.typesystem.selftype._

val buttons = Vector(new ButtonSubjectObserver.ObservableButton("one"), 
		     new ButtonSubjectObserver.ObservableButton("two"))

val observer = new ButtonClickObserver
buttons foreach (_.addObserver(observer))
for (i <- 0 to 2) buttons(0).click()
for (i <- 0 to 4) buttons(1).click()
println(observer.clicks)

// *** Note
// returns Map("one" -> 3, "two" -> 5)

// ** src/main/scala/progscala2/typesystem/selftype/this-alias.sc

class C1 { self =>                                                   // <1>
  def talk(message: String) = println("C1.talk: " + message)
  class C2 {
    class C3 {
      def talk(message: String) = self.talk("C3.talk: " + message)   // <2>
    }
    val c3 = new C3
  }
  val c2 = new C2
}

val c1 = new C1
c1.talk("Hello")                                                     // <3>
c1.c2.c3.talk("World")                                               // <4>

// A self type annotation can provide an alias for 'this', a self type, or both.

// Just using the alias is roughly the same as:

// trait T {
//  private[this] val self = this
// }

// trait A { this: B => ... }   
// Does not do this, it assigns the type of self.

// ** src/main/scala/progscala2/typesystem/selftype/selftype-cake-pattern.sc

trait Persistence { def startPersistence(): Unit }                   // <1>
trait Midtier { def startMidtier(): Unit }
trait UI { def startUI(): Unit }

trait Database extends Persistence {                                 // <2>
  def startPersistence(): Unit = println("Starting Database")  
}
trait BizLogic extends Midtier {
  def startMidtier(): Unit = println("Starting BizLogic")  
}
trait WebUI extends UI {
  def startUI(): Unit = println("Starting WebUI")  
}

trait App0 { self: Persistence with Midtier with UI =>                // <3>
  
  def run() = {
    startPersistence()
    startMidtier()
    startUI()
  }
}

object MyApp extends App0 with Database with BizLogic with WebUI      // <4>
                                                                     
MyApp.run                                                            // <5>

// *** Note
// This is a flexible abstract to concrete pattern.
// The top-level App0 specifies the implementations it will use.
// The control logic is then implemented using the abstract interfaces using self.


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

