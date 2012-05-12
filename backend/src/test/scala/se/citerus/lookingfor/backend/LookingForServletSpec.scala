package se.citerus.lookingfor.backend

import org.scalatra.test.specs2._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html 
class LookingForServletSpec extends ScalatraSpec { def is =
  "GET / on LookingForServlet"                     ^
    "should return status 200"                  ! root200^
   "Post footprints with valid arguments"			^
    "should return status 200"					! postFootprints^
                                                end
    
  addServlet(classOf[LookingForServlet], "/*")

  def root200 = get("/") { 
    status must_== 200
  }

  def postFootprints = post("/abc/1234/footprints", "lat" -> "12.05", "lon" -> "18.50", "accuracy" -> "15.0", "verhash" -> "xyz") {
    status must_== 200
  }
}
