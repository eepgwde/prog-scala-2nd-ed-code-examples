// src/main/scala/DSLs/payroll/money.scala

package dsls.payroll
import scala.language.implicitConversions
import java.math.{BigDecimal => JBigDecimal, 
    MathContext => JMathContext, RoundingMode => JRoundingMode}

/**
 * If you've worked in Finance, you know that doing Money arithmetic
 * is notoriously difficult, due to rounding errors, etc. This class
 * does NOT solve that problem and it shouldn't be used as is in
 * production. See the notes for {@link equals(obj: Any)}.
 */
case class Money(amount: BigDecimal) {

  def unary_- : Money        = Money(-amount)
  def +  (m: Money): Money   = Money(amount + m.amount)
  def -  (m: Money): Money   = Money(amount - m.amount)
  def *  (m: Money): Money   = Money(amount * m.amount)
  def /  (m: Money): Money   = Money(amount / m.amount)
  def <  (m: Money): Boolean = amount <  m.amount
  def <= (m: Money): Boolean = amount <= m.amount
  def >  (m: Money): Boolean = amount >  m.amount
  def >= (m: Money): Boolean = amount >= m.amount

  /**
   * Equality of Money is tricky to get right because we allow multiplication 
   * and division, even though we are using BigDecimals. It would be reasonable
   * to only allow multiplication of fractions near 1.0, e.g., for computing 
   * interest. Instead, we use a fuzzy equality hack. Caveat Emptor!
   * Try commenting out this version of equals and just use the default
   * generated by the compiler. Does the corresponding `MoneySpec` still pass?
   */
  override def equals(obj: Any): Boolean = obj match {
    case Money(amt) => (amount - amt).abs <= 0.0001
    case _ => false
  }

  override def toString = s"$$${amount}"  // => e.g., "$22.04"
}

object Money {
  def apply(amount: JBigDecimal) = new Money(scaled(BigDecimal(amount, context)))
  def apply(amount: Double)      = new Money(scaled(BigDecimal(amount, context)))
  def apply(amount: Long)        = new Money(scaled(BigDecimal(amount, context)))
  def apply(amount: Int)         = new Money(scaled(BigDecimal(amount, context)))
  
  protected def scaled(d: BigDecimal) = d.setScale(scale, roundingMode)
  
  val scale = 12
  val jroundingMode = JRoundingMode.HALF_UP
  val roundingMode  = BigDecimal.RoundingMode.HALF_UP
  val context = new JMathContext(scale, jroundingMode)
}

object Type2Money {
  implicit def bigDecimal2Money(b: BigDecimal)   = Money(b)
  implicit def jBigDecimal2Money(b: JBigDecimal) = Money(b)
  implicit def double2Money(d: Double)           = Money(d)
  implicit def long2Money(l: Long)               = Money(l)
  implicit def int2Money(i: Int)                 = Money(i)
}
