package se.citerus.collabsearch.store;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MongoDBSmokeTest extends TestCase {
	/**
	 * Create the test case
	 * @param testName name of the test case
	 */
	public MongoDBSmokeTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(MongoDBSmokeTest.class);
	}

	/**
	 * Test the connectivity of the MongoDB database
	 */
	public void testMongoConnectivity() {
		Mongo mongo = null;
		try {
			mongo = new Mongo();
			DB db = mongo.getDB("lookingfor");
			DBCollection userColl = db.getCollection("users");
			DBCursor cursor = userColl.find();
			assertNotNull("Empty user collection", cursor);
			assertTrue("Empty user collection", cursor.hasNext());
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		} catch (MongoException e) {
			fail("No connection to db, is the server running?");
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			if (mongo != null) {
				mongo.close();
			}
		}
	}
}
