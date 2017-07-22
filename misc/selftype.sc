// * @author weaves

// * Introductory examples

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

trait App { self: Persistence with Midtier with UI =>                // <3>
  
  def run() = {
    startPersistence()
    startMidtier()
    startUI()
  }
}

object MyApp extends App with Database with BizLogic with WebUI      // <4>
                                                                     
MyApp.run                                                            // <5>

// ** src/main/scala/progscala2/typesystem/selftype/ButtonSubjectObserver.sc
import progscala2.typesystem.selftype._

val buttons = Vector(new ObservableButton("one"), new ObservableButton("two"))
val observer = new ButtonClickObserver
buttons foreach (_.addObserver(observer))
for (i <- 0 to 2) buttons(0).click()
for (i <- 0 to 4) buttons(1).click()
println(observer.clicks)
// Map("one" -> 3, "two" -> 5)
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

