package se.citerus.lookingfor.backend
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

object LookingForCore {
  val TIME_DIFF_IN_MS_WARNING = 200
  val TIME_DIFF_IN_MS_ERROR = 1500
}

class LookingForCore {

  def addUser(authHash: String, name: String, secret: String) = {
    val users = MongoConnection()("lookingfor")("users")

    val query = MongoDBObject("name" -> name)
    if (users.findOne(query).isDefined)
      UserExists(msg = "A user with this name already exists", name = name)
    else {
      val user = MongoDBObject(
        "name" -> name, "secret" -> secret)
      users.insert(user)
      Success("User created")
    }

  }

  def postFootprint(authHash: String, userId: String, objectId: String,
    lat: Double, lon: Double, accuracy: Double, timeFixed: Long) = {
    val timestamp = System.currentTimeMillis();

    val users = MongoConnection()("lookingfor")("users")
    val objects = MongoConnection()("lookingfor")("objects")
    
    if(!ObjectId.isValid(objectId))
      InvalidParameter(msg = "A parameter is invalid", parameter = objectId)
    else if(objects.findOne(MongoDBObject("_id" -> new ObjectId(objectId))).isEmpty)
      ObjectDoesNotExist(msg = "Search object does not exist", oid = objectId)
    else if (users.findOne(MongoDBObject("_id" -> new ObjectId(userId))).isEmpty)
      UserDoesNotExist(msg = "User does not exist", uid = userId)
    else {
      val footprints = MongoConnection()("lookingfor")("footprints")

      val query = MongoDBObject("object" -> objectId, "user" -> new ObjectId(userId));
      val footprint = MongoDBObject(
        "$push" -> MongoDBObject(
          "footprints" -> MongoDBObject(
            "received" -> timestamp,
            "accuracy" -> accuracy,
            "fixed" -> timeFixed,
            "loc" -> MongoDBObject(
              "lon" -> lon,
              "lat" -> lat))));

      footprints.update(query, footprint, true, false)
      val diff: Long = Math.abs(timestamp - timeFixed);
      if (diff > LookingForCore.TIME_DIFF_IN_MS_ERROR)
        TimeError(msg = "Time of gps fix differs significantly from server time. The footprint is still registered but may be excluded in summary.", diffInMs = diff)
      else if (diff > LookingForCore.TIME_DIFF_IN_MS_WARNING)
        TimeWarning(msg = "The time of gps fix differs from server time. It's still within acceptable range but futher drifting may cause future footprints to be excluded in summary.", diffInMs = diff)
      else
        Success("Footprint registered")
    }
  }
}