package se.citerus.lookingfor.backend

import org.scalatra.test.specs2._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html 
class LookingForServletSpec extends ScalatraSpec { def is =
  "GET / on LookingForServlet"                     ^
    "should return status 200"                  ! root200^
   "Post footprints with non-existing object"			^
    "should return status 404"					! postFootprintsWithWrongObject^
                                                end
    
  addServlet(classOf[LookingForServlet], "/*")

  def root200 = get("/") { 
    status must_== 200
  }

  def postFootprintsWithWrongObject = post("/objects/4fb63aec797e907a5700a224/someuser/footprints", "fixtime" -> "1239104014", "lat" -> "12.05", "lon" -> "18.50", "accuracy" -> "15.0", "authhash" -> "xyz") {
    status must_== 404
  }
}
