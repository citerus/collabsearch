package se.citerus.lookingfor.backend
import cc.spray.json.DefaultJsonProtocol

sealed abstract case class Response
case class Success(msg: String, code: Int = 1001) extends Response
case class TimeError(msg: String, code: Int = 1050, diffInMs: Long) extends Response
case class TimeWarning(msg: String, code: Int = 1051, diffInMs: Long) extends Response
case class UserExists(msg: String, code: Int = 1052, name: String) extends Response
case class UserDoesNotExist(msg: String, code: Int = 1053, uid: String) extends Response
case class ObjectDoesNotExist(msg: String, code: Int = 1054, oid: String) extends Response
case class InvalidParameter(msg: String, code: Int = 1055, parameter: String) extends Response

object ResponseJsonProtocol extends DefaultJsonProtocol {
  implicit val successFormat = jsonFormat(Success, "msg", "code")
  implicit val timeErrorFormat = jsonFormat(TimeError, "msg", "code", "diffMs")
  implicit val timeWarningFormat = jsonFormat(TimeWarning, "msg", "code", "diffMs")
  implicit val userExistsFormat = jsonFormat(UserExists, "msg", "code", "name")
  implicit val userDoesNotExistFormat = jsonFormat(UserDoesNotExist, "msg", "code", "uid")
  implicit val objectDoesNotExistFormat = jsonFormat(ObjectDoesNotExist, "msg", "code", "oid")
  implicit val invalidParameterFormat = jsonFormat(InvalidParameter, "msg", "code", "parameter")
}