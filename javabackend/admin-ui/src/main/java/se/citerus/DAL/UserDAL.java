package se.citerus.DAL;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class UserDAL {

	private DBCollection userColl;
	private Mongo mongo;

	public UserDAL() {
		try {
			mongo = new Mongo();
			DB db = mongo.getDB("lookingfor");
			userColl = db.getCollection("users");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	public boolean findUser(String username, char[] password) {
		BasicDBObject query = new BasicDBObject("username", username).append("password", password);
		DBObject result = userColl.findOne(query);
		if (result != null) {
			return true; //auth successful
		} else {
			return false; //no such user
		}
	}
	
	public void disconnect() {
		mongo.close();
	}

}
