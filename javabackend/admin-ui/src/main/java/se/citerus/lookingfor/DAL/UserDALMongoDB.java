package se.citerus.lookingfor.DAL;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.citerus.lookingfor.logic.User;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

public class UserDALMongoDB implements UserDAL { //TODO break out into interface with in-memory and MongoDB impl

	private Mongo mongo;
	private DBCollection userColl;
	private DBCollection authColl;
	private DBCollection roleColl;
	
	private static final boolean ENABLEUPSERT = true;
	private static final boolean DISABLEUPSERT = false;
	private static final boolean ENABLEMULTIUPDATE = true;
	private static final boolean DISABLEMULTIUPDATE = false;

	public UserDALMongoDB() {
		try {
			mongo = new Mongo();
			DB db = mongo.getDB("lookingfor");
			userColl = db.getCollection("users");
			authColl = db.getCollection("auth");
			roleColl = db.getCollection("roles");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	public boolean findUser(String username, char[] password) throws IOException {
		try {
			BasicDBObject query = new BasicDBObject("username", username).
					append("password", String.valueOf(password));
			DBObject result = userColl.findOne(query);
			if (result != null) {
				if (username.equals(result.get("username")) && 
						String.valueOf(password).equals(result.get("password"))) {
					return true; //auth successful
				}
			}
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
		return false; //no such user
	}
	
	public void disconnect() {
		mongo.close();
	}

	public boolean findUserWithRole(String username, String role) throws IOException {
		try {
			BasicDBObject query = new BasicDBObject("username",username).append("role", role);
			DBObject result = userColl.findOne(query);
			if (result != null) {
				return true;
			}
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
		return false;
	}

	public void makeSaltForUser(String username) throws IOException {
		try {
			long salt = new Random().nextLong();
			if (salt < 0) {
				salt = (-1) * salt;
			}
			WriteResult result = authColl.insert(new BasicDBObject("username", username).append("salt", salt));
			if (result.getLastError().ok() == false) {
				throw new IOException("Database write failure");
			}
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
	}

	public long retrieveSaltForUser(String username) throws Exception {
		try {
			BasicDBObject query = new BasicDBObject("username", username);
			BasicDBObject limit = new BasicDBObject("salt",1);
			BasicDBObject result = (BasicDBObject) authColl.findOne(query,limit);
			if (result != null) {
				return result.getLong("salt");
			} else {
				throw new Exception("User not found");
			}
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
	}

	public void addOrModifyUser(User user) throws IOException {
		try {
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
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
	}

	public List<User> getAllUsers() throws IOException {
		List<User> list = null;
		try {
			DBObject query = new BasicDBObject();
			DBObject limit = new BasicDBObject("username", 1).append("role", 1).append("_id", 0);
			DBCursor cursor = userColl.find(query, limit);
			if (cursor != null) {
				list = new ArrayList<User>();
				while (cursor.hasNext()) {
					BasicDBObject result = (BasicDBObject) cursor.next();
					User user = new User(result.getString("username"),result.getString("role"));
					list.add(user);
				}
			}
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
		return list;
	}

	public User getUserByUsername(String username) throws Exception {
		User user = null;
		try {
			BasicDBObject query = new BasicDBObject("username",username);
			BasicDBObject result = (BasicDBObject) userColl.findOne(query);
			if (result == null) {
				throw new Exception("User not found");
			}
			user = new User(
				result.getString("username"), 
				result.getString("password"), 
				result.getString("email"), 
				result.getString("tele"), 
				result.getString("role")
			);
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
		return user;
	}

	public Boolean deleteUserByUsername(String username) throws IOException {
		try {
			BasicDBObject query = new BasicDBObject("username", username);
			WriteResult result = userColl.remove(query);
			CommandResult lastError = result.getLastError();
			if (!lastError.ok()) {
				throw new IOException("Error: Database write failure on User document deletion");
			}
			result = authColl.remove(query);
			lastError = result.getLastError();
			if (!lastError.ok()) {
				throw new IOException("Error: Database write failure on Salt document deletion");
			}
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
		return true;
	}

	public List<String> getAllRoles() throws IOException {
		List<String> list = null;
		try {
			BasicDBObject query = new BasicDBObject();
			BasicDBObject filter = new BasicDBObject("rolename", 1).append("_id", 0);
			DBCursor cursor = roleColl.find(query, filter);
			if (cursor != null) {
				list = new ArrayList<String>(cursor.count());
				while (cursor.hasNext()) {
					BasicDBObject obj = (BasicDBObject) cursor.next();
					list.add(obj.getString("rolename"));
				}
			} else {
				throw new IOException("No roles found in database");
			}
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
		return list;
	}

	/**
	 * Finds users matching the included username or telephone number or email address.
	 * @return true if dups are found, else false
	 */
	public boolean checkForDuplicateUserData(String username, String tele, String email) throws IOException {
		try {
			BasicDBList basicDBList = new BasicDBList();
			basicDBList.add(new BasicDBObject("username", username));
			basicDBList.add(new BasicDBObject("tele", tele));
			basicDBList.add(new BasicDBObject("email", email));
			BasicDBObject query = new BasicDBObject("$or", basicDBList);
			BasicDBObject filter = new BasicDBObject("_id", 1);
			DBCursor cursor = userColl.find(query, filter);
			if (cursor.hasNext()) {
				return true;
			}
			return false;
		} catch (MongoException e) {
			throw new IOException("No database connectivity");
		}
	}

}
