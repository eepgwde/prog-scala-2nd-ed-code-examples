// src/main/scala/Traits/observer/observer.scala

package traits.observer
import scala.language.reflectiveCalls

trait Subject {
  type Observer = { def receiveUpdate(subject: Any) }

  private var observers = List[Observer]()
  def addObserver(observer:Observer) = observers ::= observer
  def notifyObservers = observers foreach (_.receiveUpdate(this))
}
