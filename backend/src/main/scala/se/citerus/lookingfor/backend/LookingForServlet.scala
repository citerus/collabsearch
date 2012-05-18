package se.citerus.lookingfor.backend

import org.scalatra._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import org.scalatra.scalate.ScalateSupport
import se.citerus.lookingfor.backend.Response._
import se.citerus.lookingfor.backend.ResponseJsonProtocol._
import cc.spray.json.DefaultJsonProtocol
import cc.spray.json._

object LookingForServlet {
  val core = new LookingForCore()
}

class LookingForServlet extends ScalatraServlet with ScalateSupport {

  get("/") {
    <html>
      <head>
        <title>Looking For</title>
      </head>
      <body>
        <h1>Looking For - A missing person search utility by Citerus</h1>
      </body>
    </html>
  }

  post("/objects/:object/:user/footprints") {
    val authHash: String = params.getOrElse("authhash", halt(400))
    val lat: Double = params.getOrElse("lat", halt(400)).toDouble
    val lon: Double = params.getOrElse("lon", halt(400)).toDouble
    val accuracy: Double = params.getOrElse("accuracy", halt(400)).toDouble
    val fixTime: Long = params.getOrElse("fixtime", halt(400)).toLong
    val objectId = params("object")
    val user = params("user")

    LookingForServlet.core.postFootprint(authHash, user, objectId, lat, lon, accuracy, fixTime) match {
      case s: Success => println(s.toJson); s.toJson
      case tw: TimeWarning => println(tw.toJson); tw.toJson
      case te: TimeError => println(te.toJson); te.toJson
      case ip: InvalidParameter => println(ip.toJson); halt(400, ip.toJson)
      case nu: UserDoesNotExist => println(nu.toJson); halt(404, nu.toJson)
      case no: ObjectDoesNotExist => println(no.toJson); halt(404, no.toJson)
      case _ => println("Unexpected response"); halt(500)
    }
  }
  
  post("/users/:user") {
    val authHash: String = params.getOrElse("authhash", halt(400))
    val secret: String = params.getOrElse("secret", halt(400))
    val user = params("user")
    
    LookingForServlet.core.addUser(authHash, user, secret) match {
      case s: Success => println(s.toJson); s.toJson
      case ue: UserExists => println(ue.toJson); halt(409, ue.toJson)
      case _ => halt(500)
    }
  }

  notFound {
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }

}