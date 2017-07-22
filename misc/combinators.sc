// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/fp/combinators/combinators.sc

object Combinators1 {
  def map[A,B](list: List[A])(f: (A) ⇒ B): List[B] = list map f
}

object Combinators {
  def map[A,B](f: (A) ⇒ B)(list: List[A]): List[B] = list map f
}

val intToString = (i:Int) => s"N=$i"
// Result: intToString: Int => String = <function1>

val flist = Combinators.map(intToString) _
// Result: flist: List[Int] => List[String] = <function1>

val list = flist(List(1, 2, 3, 4))
// Result: list: List[String] = List(N=1, N=2, N=3, N=4)

// ** src/main/scala/progscala2/fp/combinators/payroll.sc

case class Employee (
  name: String,
  title: String,
  annualSalary: Double,
  taxRate: Double,
  insurancePremiumsPerWeek: Double)

val employees = List(
  Employee("Buck Trends", "CEO", 200000, 0.25, 100.0),
  Employee("Cindy Banks", "CFO", 170000, 0.22, 120.0),
  Employee("Joe Coder", "Developer", 130000, 0.20, 120.0))

// Calculate weekly payroll:
val netPay = employees map { e => 
  val net = (1.0 - e.taxRate) * (e.annualSalary / 52.0) - 
    e.insurancePremiumsPerWeek
  (e, net)
}

// "Print" paychecks:
println("** Paychecks:")
netPay foreach { 
  case (e, net) => println(f"  ${e.name+':'}%-16s ${net}%10.2f") 
}

// Generate report:
val report = (netPay foldLeft (0.0, 0.0, 0.0)) { 
  case ((totalSalary, totalNet, totalInsurance), (e, net)) => 
    (totalSalary + e.annualSalary/52.0, 
      totalNet + net, 
      totalInsurance + e.insurancePremiumsPerWeek)
}

println("\n** Report:")
println(f"  Total Salary:    ${report._1}%10.2f")
println(f"  Total Net:       ${report._2}%10.2f")
println(f"  Total Insurance: ${report._3}%10.2f")

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

