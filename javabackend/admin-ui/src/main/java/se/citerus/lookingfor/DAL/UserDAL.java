package se.citerus.lookingfor.DAL;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.citerus.lookingfor.logic.User;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

public class UserDAL { //TODO break out into interface with in-memory and MongoDB impl

	private Mongo mongo;
	private DBCollection userColl;
	private DBCollection authColl;
	
	private static final boolean ENABLEUPSERT = true;
	private static final boolean DISABLEUPSERT = false;
	private static final boolean ENABLEMULTIUPDATE = true;
	private static final boolean DISABLEMULTIUPDATE = false;

	public UserDAL() {
		try {
			mongo = new Mongo();
			DB db = mongo.getDB("lookingfor");
			userColl = db.getCollection("users");
			authColl = db.getCollection("auth");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	public boolean findUser(String username, char[] password) {
		BasicDBObject query = new BasicDBObject("username", username).
				append("password", String.valueOf(password));
		DBObject result = userColl.findOne(query);
		if (result != null) {
			if (username.equals(result.get("username")) && 
					String.valueOf(password).equals(result.get("password"))) {
				return true; //auth successful
			}
		}
		return false; //no such user
	}
	
	public void disconnect() {
		mongo.close();
	}

	public boolean findUserWithRole(String username, String role) {
		BasicDBObject query = new BasicDBObject("username",username).append("role", role);
		DBObject result = userColl.findOne(query);
		if (result != null) {
			return true;
		}
		return false;
	}

	public void makeSaltForUser(String username) throws IOException {
		long salt = new Random().nextLong();
		if (salt < 0) {
			salt = (-1) * salt;
		}
		WriteResult result = authColl.insert(new BasicDBObject("username", username).append("salt", salt));
		if (result.getLastError().ok() == false) {
			throw new IOException("Database write failure");
		}
	}

	public long getSaltForUser(String username) throws Exception {
		BasicDBObject query = new BasicDBObject("username", username);
		BasicDBObject limit = new BasicDBObject("salt",1);
		BasicDBObject result = (BasicDBObject) authColl.findOne(query,limit);
		if (result != null) {
			return result.getLong("salt");
		} else {
			throw new Exception("User not found");
		}
	}

	public void addOrModifyUser(User user) throws IOException {
		DBObject query = new BasicDBObject("username",user.getUsername());
		DBObject updateObj = new BasicDBObject();
		updateObj.put("username", user.getUsername());
		updateObj.put("password", user.getPassword());
		updateObj.put("email", user.getEmail());
		updateObj.put("tele", user.getTele());
		updateObj.put("role", user.getRole());
		WriteResult result = userColl.update(query, updateObj, ENABLEUPSERT, DISABLEMULTIUPDATE);
		if (result.getLastError().ok() == false) {
			throw new IOException("Database write failure");
		}
	}

	public List<User> getAllUsers() {
		DBObject query = new BasicDBObject();
		DBObject limit = new BasicDBObject("username", 1).append("role", 1).append("_id", 0);
		DBCursor cursor = userColl.find(query, limit);
		List<User> list = null;
		if (cursor != null) {
			list = new ArrayList<User>();
			while (cursor.hasNext()) {
				BasicDBObject result = (BasicDBObject) cursor.next();
				User user = new User(result.getString("username"),result.getString("role"));
				list.add(user);
			}
		}
		return list;
	}

	public User getUserByUsername(String username) throws Exception {
		BasicDBObject query = new BasicDBObject("username",username);
		BasicDBObject result = (BasicDBObject) userColl.findOne(query);
		if (result == null) {
			throw new Exception("User not found");
		}
		User user = new User(
			result.getString("username"), 
			result.getString("password"), 
			result.getString("email"), 
			result.getString("tele"), 
			result.getString("role")
		);
		return user;
	}

}
