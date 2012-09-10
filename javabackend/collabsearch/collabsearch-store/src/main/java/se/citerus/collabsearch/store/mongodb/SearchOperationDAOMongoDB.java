package se.citerus.collabsearch.store.mongodb;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;

public class SearchOperationDAOMongoDB implements SearchOperationDAO {

	private DBCollection operationsColl;
	private Mongo mongo;

	public SearchOperationDAOMongoDB() throws Exception {
		mongo = new Mongo();
		DB db = mongo.getDB("lookingfor");
		operationsColl = db.getCollection("searchops");
	}

	@Override
	public SearchOperationIntro[] getAllSearchOps() throws IOException {
		SearchOperationIntro[] array = null;
		try {
			DBObject query = new BasicDBObject(); //query for all
			DBObject limit = new BasicDBObject("_id", 1)
									.append("title", 1)
									.append("descr", 1);
			DBCursor cursor = operationsColl.find(query, limit);
			if (cursor != null) {
				array = makeOpsIntroArrayFromCursor(cursor);
			}
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return array;
	}

	private SearchOperationIntro[] makeOpsIntroArrayFromCursor(DBCursor cursor) {
		SearchOperationIntro[] array;
		int count = cursor.count();
		if (count == 0) {
			array = new SearchOperationIntro[0];
		} else {
			array = new SearchOperationIntro[count];
			int i = 0;
			while (cursor.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) cursor.next();
				String id = dbo.getString("_id");
				String title = dbo.getString("title");
				String descr = dbo.getString("descr");
				array[i] = new SearchOperationIntro(id, title, descr);
				i++;
			}
		}
		return array;
	}
	
	@Override
	public SearchOperation getSearchOpById(String id) throws IOException {
		SearchOperation op = null;
		if (id == null) {
			throw new IOException("Ingen sökterm angiven");
		}
		try {
			DBObject query = new BasicDBObject("_id", new ObjectId(id));
			DBObject limit = new BasicDBObject("zones", 0).append("groups", 0);
			BasicDBObject result = (BasicDBObject) operationsColl.findOne(query, limit);
			if (result != null) {
				op = new SearchOperation();
				op.setId(result.getObjectId("_id").toString());
				op.setTitle(result.getString("title"));
				op.setDescr(result.getString("descr"));
				op.setDate(new Date(result.getLong("date")));
				op.setLocation(result.getString("location"));
				op.setStatus(getSearchOpStatusByName(result.getString("status")));
			}
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return op;
	}
	
	@Override
	public void assignUserToSearchOp(String opId, String name, String email,
			String tele) throws IOException {
		try {
			DBObject queryObj = new BasicDBObject("_id", new ObjectId(opId));
			BasicDBObject userData = new BasicDBObject("name", name)
											.append("tele", tele)
											.append("email", email);
			DBObject updateObj = new BasicDBObject("$addToSet", 
					new BasicDBObject("searchers", userData));
			WriteResult result = operationsColl.update(queryObj, updateObj);
			if (!result.getLastError().ok()) {
				throw new IOException("Updatering misslyckades");
			}
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
	}

	@Override
	public SearchOperationIntro[] getSearchOpsByFilter(String title, String location, 
			String date) throws IOException {
		SearchOperationIntro[] array = null;
		if (title == null && location == null && date == null) {
			throw new IOException("Inga söktermer angivna");
		}
		try {
			BasicDBObject query = new BasicDBObject();
			BasicDBObject limit = new BasicDBObject("_id", 1)
										.append("title", 1)
										.append("descr", 1);
			if (title != null) {
				query.append("title", title);
			}
			if (location != null) {
				query.append("location", location);
			}
			if (date != null) {
				query.append("date", date);
			}
			DBCursor cursor = operationsColl.find(query, limit);
			if (cursor != null) {
				array = makeOpsIntroArrayFromCursor(cursor);
			}
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return array;
	}
	
	@Override
	public List<Status> getAllSearchOpStatuses() throws IOException {
		return null;
	}

	@Override
	public Status getSearchOpStatusByName(String statusName) throws IOException {
		//TODO fetch status from separate table
		return new Status(18295719, statusName, "Beskrivning");
	}

	@Override
	public void disconnect() {
		
	}

	@Override
	public String endOperation(String opName, String missionName) {
		return null;
	}

	@Override
	public String getZoneIdByName(String zoneName, String opName) {
		return null;
	}

	@Override
	public void deleteZone(String zoneId) {
	}

	@Override
	public String getGroupIdByName(String groupName, String opName) {
		return null;
	}

	@Override
	public void deleteGroup(String groupId) {
	}

}
