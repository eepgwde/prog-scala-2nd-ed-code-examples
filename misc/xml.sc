// * @author weaves

// * Introductory examples

// ** src/main/scala/progscala2/dsls/xml/reading.sc
import scala.xml._                                                   // <1>

val xmlAsString = "<sammich>...</sammich>"                           // <2>
val xml1 = XML.loadString(xmlAsString)

val xml2 =                                                           // <3>
<sammich>
  <bread>wheat</bread>
  <meat>salami</meat>
  <condiments>
    <condiment expired="true">mayo</condiment>
    <condiment expired="false">mustard</condiment>
  </condiments>
</sammich>

for {                                                                // <4>
  condiment <- (xml2 \\ "condiment")
  if (condiment \ "@expired").text == "true"
} println(s"the ${condiment.text} has expired!")

def isExpired(condiment: Node): String =                             // <5>
  condiment.attribute("expired") match {
    case Some(Nil) | None => "unknown!"
    case Some(nodes) => nodes.head.text
  }

xml2 match {                                                         // <6>
  case <sammich>{ingredients @ _*}</sammich> => {
    for {
      condiments @ <condiments>{_*}</condiments> <- ingredients
      cond <- condiments \ "condiment"
    } println(s"  condiment: ${cond.text} is expired? ${isExpired(cond)}")
  }
}

// ** src/main/scala/progscala2/dsls/xml/writing.sc
import scala.xml._

val xml2 =
<sammich>
  <bread>wheat</bread>
  <meat>salami</meat>
  <condiments>
    <condiment expired="true">mayo</condiment>
    <condiment expired="false">mustard</condiment>
  </condiments>
</sammich>

XML.save("sammich.xml", xml2, "UTF-8")                               // <1>


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

