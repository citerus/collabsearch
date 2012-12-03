package se.citerus.collabsearch.store.mongodb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import se.citerus.collabsearch.model.DbUser;
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
import com.mongodb.DBPort;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.QueryBuilder;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

@Repository
@Primary
public class UserDAOMongoDB implements UserDAO {

	private Mongo mongo;
	private DBCollection userColl;
	private DBCollection authColl;
	private DBCollection roleColl;
	
	private enum OpType {
		INSERT, UPDATE, REMOVE
	}

	public UserDAOMongoDB() {
	}
	
	@PostConstruct
	public void init() {
		String dbName = "";
		String dbPort = "";
		String dbUser = "";
		String dbPass = "";
		String dbAddr = "";
		try {
			Properties prop = new Properties();
			InputStream stream;
			stream = UserDAOMongoDB.class.getResourceAsStream(
				"/db-server-config.properties");
			if (stream != null) {
				prop.load(stream);
				dbName = prop.getProperty("DBNAME", "lookingfor");
				dbPort = prop.getProperty("DBPORT", "27017");
				dbUser = prop.getProperty("DBUSER", "");
				dbPass = prop.getProperty("DBPASS", "");
				dbAddr = prop.getProperty("DBADDR", "localhost");
			}
			System.out.println("Found MongoDB database name: " + dbName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			MongoOptions options = new MongoOptions();
			options.socketTimeout = 2*60*1000;
			ServerAddress addr = new ServerAddress(dbAddr, Integer.parseInt(dbPort));
			mongo = new Mongo(addr, options);
			DB db = mongo.getDB(dbName);
			if (addr.getHost().equals("localhost")) {
				db.addUser(dbUser, dbPass.toCharArray());
			}
			boolean authenticated = db.authenticate(dbUser, dbPass.toCharArray());
			if (authenticated) {
				userColl = db.getCollection("users");
				authColl = db.getCollection("auth");
				roleColl = db.getCollection("roles");
				System.out.println("MongoDB successfully initialized");
			} else {
				throw new IOException("Authentication failure for user " + dbUser);
			}
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void cleanUp() {
		mongo.close();
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
	
	public boolean findUserWithRole(String username, String role) throws IOException, UserNotFoundException {
		BasicDBObject query = new BasicDBObject("username",username).append("role", role);
		DBObject result = userColl.findOne(query);
		if (result != null) {
			return true;
		}
		return false;
	}

	public List<User> getAllUsers() throws IOException {
		List<User> list = Collections.emptyList();
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
		return list;
	}

	public User getUserByUsername(String username) throws IOException, UserNotFoundException {
		BasicDBObject query = new BasicDBObject("username",username);
		BasicDBObject result = (BasicDBObject) userColl.findOne(query);
		if (result == null) {
			throw new UserNotFoundException(username);
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

	public void deleteUserByUsername(String username) throws IOException, UserNotFoundException {
		BasicDBObject query = new BasicDBObject("username", username);
		WriteResult result = userColl.remove(query);
		if (result.getN() == 0) {
			throw new UserNotFoundException(username);
		}
		checkResult(result, OpType.REMOVE);
	}
	
	public List<String> getAllRoles() throws IOException {
		List<String> list = null;
		BasicDBObject query = new BasicDBObject();
		BasicDBObject filter = new BasicDBObject("rolename", 1).append("_id", 0);
		DBCursor cursor = roleColl.find(query, filter);
		if (cursor == null) {
			throw new IOException("No roles found in database");
		}
		list = new ArrayList<String>(cursor.count());
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			list.add(obj.getString("rolename"));
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
			if (cursor != null) {
				if (cursor.hasNext()) {
					return true;
				}
			}
			return false;
		} catch (MongoException e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
	}

	public void editExistingUser(User user) throws IOException, UserNotFoundException, DuplicateUserDataException {
		BasicDBObject query = new BasicDBObject("username", user.getUsername());
		BasicDBObject userObject = makeUserDBO(user);
		WriteResult result = userColl.update(query, userObject);
		if (result.getField("code") != null) {
			int code = Integer.parseInt(result.getField("code").toString());
			if (code == 11000 || code == 11001) {
				throw new DuplicateUserDataException("Duplicerat värde på användarnamn, epost eller telefonnummer");
			}
		}
	}

	public String addNewUser(User user) throws IOException, DuplicateUserDataException {
		BasicDBObject userObject = makeUserDBO(user);
//		userObject.append("authref", generateSaltForUser()); //TODO use salt for passwords?
		WriteResult result = userColl.insert(userObject);
		if (result.getField("code") != null) {
			int code = Integer.parseInt(result.getField("code").toString());
			if (code == 11000 || code == 11001) {
				throw new DuplicateUserDataException("Duplicerat värde på användarnamn, epost eller telefonnummer");
			}
		}
		checkResult(result, OpType.INSERT);
		return userObject.getString("username");
	}

	private BasicDBObject makeUserDBO(User user) {
		BasicDBObject userObject = new BasicDBObject();
		userObject.put("username", user.getUsername());
		userObject.put("password", user.getPassword());
		userObject.put("email", user.getEmail());
		userObject.put("tele", user.getTele());
		userObject.put("role", user.getRole());
		return userObject;
	}
	
	private ObjectId generateSaltForUser() throws IOException {
		UUID uuid = UUID.randomUUID(); //TODO replace with bcrypt?
		BasicDBObject dbo = new BasicDBObject("salt", uuid.toString());
		WriteResult result = authColl.insert(dbo);
		checkResult(result, OpType.INSERT);
		return dbo.getObjectId("_id");
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

	/**
	 * This method is used by Spring Security to fetch user's names and passwords for login. 
	 */
	@Override
	public DbUser findUserByName(String username) throws UserNotFoundException {
		DBObject query = new BasicDBObject("username", username);
		DBObject fields = new BasicDBObject("password", 1).append("role", 1);
		BasicDBObject dbo = (BasicDBObject) userColl.findOne(query, fields);
		if (dbo == null) {
			throw new UserNotFoundException(username);
		}
		String password = dbo.getString("password");
		String role = dbo.getString("role");
		return new DbUser(username, password, role);
	}

	@Override
	public void changePasswordForUser(String username, String hashedPassword) throws IOException {
		BasicDBObject query = new BasicDBObject("username", username);
		BasicDBObject pwDBO = new BasicDBObject("password", hashedPassword);
		BasicDBObject update = new BasicDBObject("$set", pwDBO);
		WriteResult result = userColl.update(query, update);
		checkResult(result, OpType.UPDATE);
	}

}
