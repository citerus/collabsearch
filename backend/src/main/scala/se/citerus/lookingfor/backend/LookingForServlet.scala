package se.citerus.lookingfor.backend

import org.scalatra._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import org.scalatra.scalate.ScalateSupport

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

  post("/:object/:user/footprints") {
    println("start")
    println(params)
    val lat: String = params.getOrElse("lat", halt(400))
    println(lat)
    val lon: String = params.getOrElse("lon", halt(400))
    val accuracy : String = params.getOrElse("accuracy", halt(400))
    val hash: String = params.getOrElse("verhash", halt(400))
    val timestamp = System.currentTimeMillis();
    val objectId = params("object")
    val user = params("user")

    val footprints = MongoConnection()("lookingfor")("footprints")

    val query = MongoDBObject("object" -> objectId, "user" -> user);
    val footprint = MongoDBObject(
        "$push" -> MongoDBObject(
    	    "footprints" -> MongoDBObject(
    		    "timestamp" -> timestamp, 
    		    "accuracy" -> accuracy,
    			"loc" -> MongoDBObject(
    			    "lon" -> lon, 
    			    "lat" -> lat))));

    footprints.update(query, footprint, true, false)

    """{ "response" : "success" }"""
  }

  
    notFound {
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() 
  }

}