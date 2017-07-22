// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/concurrency/process/processes.sc
import scala.sys.process._
import scala.language.postfixOps
import java.net.URL
import java.io.File

"ls -l src".!
Seq("ls", "-l", "src").!!

// Build a process to open a URL, redirect the output to "grep $filter", 
// and append the output to file (not overwrite it).
def findURL(url: String, filter: String) = 
  new URL(url) #> s"grep $filter" #>> new File(s"$filter.txt")

// Run ls -l on the file. If it exists, then count the lines.
def countLines(fileName: String) = s"ls -l $fileName" #&& s"wc -l $fileName"

findURL("http://scala-lang.org", "scala") !
countLines("scala.txt") !
findURL("http://scala-lang.org", "scala") !
countLines("scala.txt") !



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

