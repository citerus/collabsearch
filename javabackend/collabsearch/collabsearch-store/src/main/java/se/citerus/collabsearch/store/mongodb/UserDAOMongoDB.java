package se.citerus.collabsearch.store.mongodb;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.DuplicateUserDataException;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.facades.UserDAO;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.WriteResult;

public class UserDAOMongoDB implements UserDAO {

	private Mongo mongo;
	private DBCollection userColl;
	private DBCollection authColl;
	private DBCollection roleColl;

	private static final boolean ENABLEUPSERT = true;
	private static final boolean DISABLEUPSERT = false;
	private static final boolean ENABLEMULTIUPDATE = true;
	private static final boolean DISABLEMULTIUPDATE = false;
	
	private enum OpType {
		INSERT, UPDATE, REMOVE
	}

	public UserDAOMongoDB() {
		try {
			MongoOptions options = new MongoOptions();
			options.socketTimeout = 2*60*1000;
			mongo = new Mongo("localhost", options);
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

	public boolean findUser(String username, char[] password) throws IOException, UserNotFoundException {
		try {
			BasicDBObject query = new BasicDBObject("username", username).
					append("password", String.valueOf(password));
			DBObject result = userColl.findOne(query);
			if (result != null) {
				if (username.equals(result.get("username")) && 
						String.valueOf(password).equals(result.get("password"))) {
					return true; //auth successful
				}
			} else {
				throw new UserNotFoundException();
			}
		} catch (MongoException e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return false; //no such user
	}
	
	public void disconnect() {
		mongo.close();
	}

	public boolean findUserWithRole(String username, String role) throws IOException, UserNotFoundException {
		try {
			BasicDBObject query = new BasicDBObject("username",username).append("role", role);
			DBObject result = userColl.findOne(query);
			if (result != null) {
				return true;
			}
		} catch (MongoException e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
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
				throw new IOException("Databasfel");
			}
		} catch (MongoException e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
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
				throw new Exception("Användare ej funnen");
			}
		} catch (MongoException e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
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
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return list;
	}

	public User getUserByUsername(String username) throws IOException, UserNotFoundException {
		User user = null;
		try {
			BasicDBObject query = new BasicDBObject("username",username);
			BasicDBObject result = (BasicDBObject) userColl.findOne(query);
			if (result == null) {
				throw new UserNotFoundException(username);
			}
			user = new User(
				result.getString("username"), 
				result.getString("password"), 
				result.getString("email"), 
				result.getString("tele"), 
				result.getString("role")
			);
		} catch (MongoException e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return user;
	}

	public void deleteUserByUsername(String username) throws IOException, UserNotFoundException {
		BasicDBObject query = new BasicDBObject("username", username);
		WriteResult result = userColl.remove(query);
		if (result.getN() == 0) {
			throw new UserNotFoundException(username);
		}
//		checkResult(result, OpType.REMOVE);
		
		//auth collection not yet implemented
//		result = authColl.remove(query);
//		lastError = result.getLastError();
//		if (!lastError.ok()) {
//			throw new IOException("Error: Database write failure on Salt document deletion", e);
//		} else if (result.getN() == 0) {
//			throw new IOException("User " + username + " not found in auth collection", e);
//		}
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
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
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
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
	}

	public void editExistingUser(User user) throws IOException, UserNotFoundException {
		BasicDBObject query = new BasicDBObject("username", user.getUsername());
		BasicDBObject userObject = makeUserDBO(user);
		WriteResult result = userColl.update(query, userObject);
		if (result.getLastError().ok() == false) {
			if (result.getLastError().getException() instanceof MongoException.DuplicateKey) {
				throw new IOException("Duplicerat värde på användarnamn, epost eller telefonnummer");
			}
			throw new IOException("Databasfel");
		}
	}

	public void addNewUser(User user) throws IOException, DuplicateUserDataException {
		BasicDBObject userObject = makeUserDBO(user);
		WriteResult result = userColl.insert(userObject);
		if (result.getLastError().ok() == false) {
			if (result.getLastError().getException() instanceof MongoException.DuplicateKey) {
				throw new IOException("Duplicerat värde på användarnamn, epost eller telefonnummer");
			}
			throw new IOException("Databasfel");
		} else if (result.getField("code") != null) {
			int code = Integer.parseInt(result.getField("code").toString());
			if (code == 11000 || code == 11001) {
				throw new DuplicateUserDataException("Duplicerat värde på användarnamn, epost eller telefonnummer");
			}
		}
	}

	private BasicDBObject makeUserDBO(User user) {
		BasicDBObject userObject = new BasicDBObject();
		userObject.put("username", user.getUsername());
		userObject.put("password", user.getPassword());
		userObject.put("email", user.getEmail());
		userObject.put("tele", user.getTele());
		userObject.put("role", user.getRole()); //TODO replace with dbref to roleColl
		return userObject;
	}

	@Override
	public void activateDebugMode() {
		try {
			DB db = mongo.getDB("test");
			userColl = db.getCollection("users");
			authColl = db.getCollection("auth");
			roleColl = db.getCollection("roles");
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	private void checkResult(WriteResult result, OpType opType) throws IOException {
		CommandResult lastError = result.getLastError();
		if (!lastError.ok()) {
			throw new IOException(lastError.getErrorMessage());
		}
		if (result.getError() != null) {
			throw new IOException(result.getError());
		}
		if (opType == OpType.UPDATE || opType == OpType.REMOVE) {
			if (result.getN() == 0) {
				throw new IOException("No documents were affected by the op");
			}
		}
	}

}
