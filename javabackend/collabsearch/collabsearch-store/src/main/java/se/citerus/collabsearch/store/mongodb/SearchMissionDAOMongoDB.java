package se.citerus.collabsearch.store.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bson.types.ObjectId;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.WriteResult;

public class SearchMissionDAOMongoDB implements SearchMissionDAO, SearchOperationDAO {

	private Mongo mongo;
	private DBCollection missionColl;
	private DBCollection missionStatusColl;
	private DBCollection opStatusColl;
	private DBCollection operationsColl;

	private enum OPTYPE {
		INSERT, UPDATE, REMOVE
	}
	
	public SearchMissionDAOMongoDB() throws Exception {
		MongoOptions options = new MongoOptions();
		options.socketTimeout = 2*60*1000;
		mongo = new Mongo("localhost", options);
		DB db = mongo.getDB("lookingfor");
		missionColl = db.getCollection("searchmissions");
		missionStatusColl = db.getCollection("missionstatuses");
		operationsColl = db.getCollection("searchops");
		opStatusColl = db.getCollection("opstatuses");
	}
	
	@Override
	public List<SearchMission> getAllSearchMissions() throws IOException {
		BasicDBObject query = new BasicDBObject();
		DBCursor cursor = missionColl.find(query);
		if (cursor != null) {
			ArrayList<SearchMission> list = new ArrayList<SearchMission>();
			while (cursor.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) cursor.next();
				SearchMission searchMission = new SearchMission(
						dbo.get("_id").toString(), 
						dbo.getString("name"), 
						dbo.getString("descr"), 
						dbo.getInt("prio"), 
						getSearchMissionStatusById(dbo.getInt("status"))
				);
				searchMission.setFileList(convertMissionFileList((BasicDBList) dbo.get("files")));
				searchMission.setOpsList(getAllSearchOpsForMission(searchMission.getId()));
				list.add(searchMission);
			}
			return list;
		}
		return Collections.emptyList();
	}

	private List<FileMetadata> convertMissionFileList(BasicDBList dbList) {
//		BasicDBList dbList = (BasicDBList) missionDBO.get("files");
		if (dbList != null) {
			ListIterator<Object> it = dbList.listIterator();
			List<FileMetadata> list = new ArrayList<FileMetadata>();
			while (it.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) it.next();
				list.add(new FileMetadata("", 
						dbo.getString("filename"), 
						dbo.getString("mimetype"), 
						dbo.getString("filepath")));
			}
			return list;
		}
		return Collections.emptyList();
	}

	@Override
	public void disconnect() {
		mongo.close();
	}

	@Override
	public void endMission(String missionId) throws IOException {
		BasicDBObject query = makeObjectIdQuery(missionId);
		//status : 0 = id of mission status "ended", to be move to separate collection
		BasicDBObject updateObj = new BasicDBObject("status", 0);
		BasicDBObject setObj = new BasicDBObject("$set", updateObj);
		WriteResult result = missionColl.update(query, setObj);
		CommandResult lastError = result.getLastError();
		if (!lastError.ok()) {
			throw new IOException(lastError.getErrorMessage());
		}
	}

	private List<SearchOperation> getAllSearchOpsForMission(String missionId)
			throws IOException {
		DBObject query = new BasicDBObject("mission", new ObjectId(missionId));
		DBCursor cursor = operationsColl.find(query);
		List<SearchOperation> list = new ArrayList<SearchOperation>(3);
		if (cursor != null) {
			while (cursor.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) cursor.next();
				SearchOperation op = new SearchOperation();
				op.setId(dbo.getString("_id"));
				op.setTitle(dbo.getString("title"));
				op.setDescr(dbo.getString("descr"));
				op.setDate(new Date(dbo.getLong("date")));
				op.setLocation(dbo.getString("location"));
				op.setStatus(getSearchOpStatusById(dbo.getInt("status")));
				list.add(op);
			}
		}
		if (list.isEmpty()) {
			return Collections.emptyList();
		}
		return list;
	}

	@Override
	public List<Status> getAllSearchMissionStatuses() throws IOException {
		DBObject query = new BasicDBObject(0);
		DBObject keys = new BasicDBObject("_id",0);
		DBCursor cursor = missionStatusColl.find(query, keys);
		if (cursor == null) {
			return Collections.emptyList();
		}
		List<Status> list = new ArrayList<Status>();
		while (cursor.hasNext()) {
			BasicDBObject dbo = (BasicDBObject) cursor.next();
			list.add(new Status(dbo.getInt("statusid"), 
								dbo.getString("name"), 
								dbo.getString("descr")));
		}
		return list;
	}

	@Override
	public SearchMission findMission(String missionId) 
			throws SearchMissionNotFoundException, Exception {
		DBObject query = new BasicDBObject("_id", new ObjectId(missionId));
		BasicDBObject dbo = (BasicDBObject) missionColl.findOne(query);
		if (dbo == null) {
			throw new SearchMissionNotFoundException();
		}
		SearchMission mission = new SearchMission();
		mission.setId(dbo.getObjectId("_id").toString());
		mission.setName(dbo.getString("name"));
		mission.setDescription(dbo.getString("descr"));
		mission.setPrio(dbo.getInt("prio"));
		mission.setStatus(getSearchMissionStatusById(dbo.getInt("status")));
		return mission;
	}

	private Status getSearchMissionStatusById(int statusId) {
		DBObject query = new BasicDBObject("statusid", statusId);
		BasicDBObject dbo = (BasicDBObject) missionStatusColl.findOne(query);
		Status status = new Status(
				dbo.getInt("statusid"), 
				dbo.getString("name"), 
				dbo.getString("descr"));
		return status;
	}

	@Override
	public void addFileMetadata(String missionId, FileMetadata metadata)
			throws IOException {
		
	}

	@Override
	public void deleteFileMetadata(String fileName, String missionId)
			throws IOException {
		BasicDBObject query = new BasicDBObject("_id", missionId);
		BasicDBObject keys = new BasicDBObject("_id", 0).append("files", 1);
		BasicDBObject dbo = (BasicDBObject) missionColl.findOne(query, keys);
		if (dbo == null) {
			throw new IOException("File metadata not found");
		}
		BasicDBList list = (BasicDBList) dbo.get("files");
		Iterator<Object> it = list.iterator();
		while (it.hasNext()) {
			BasicDBObject listObj = (BasicDBObject) it.next();
			String existingFileName = listObj.getString("filename");
			if (existingFileName.equals(fileName)) {
				it.remove();
			}
		}
		BasicDBObject updateObj = new BasicDBObject("$set", new BasicDBObject("files", list));
		WriteResult result = missionColl.update(query, updateObj);
		checkResult(result, OPTYPE.REMOVE);
	}

	@Override
	public FileMetadata getFileMetadata(String filename, String missionId) throws IOException {
		BasicDBObject query = new BasicDBObject("_id", missionId);
		BasicDBObject keys = new BasicDBObject("files", 1).append("_id", 0);
		BasicDBObject dbo = (BasicDBObject) missionColl.findOne(query, keys);
		if (dbo == null) {
			throw new IOException();
		}
		return new FileMetadata(null, 
				dbo.getString("filename"), 
				dbo.getString("mimetype"), 
				dbo.getString("filepath"));
	}

	@Override
	public Status findMissionStatusByName(String statusName) throws IOException {
		DBObject query = new BasicDBObject("name", statusName);
		DBObject keys = new BasicDBObject("_id", 0);
		BasicDBObject dbo = (BasicDBObject) missionStatusColl.findOne(query, keys);
		if (dbo == null) {
			throw new IOException("");
		}
		Status status = new Status(
			dbo.getInt("statusid"),
			dbo.getString("name"),
			dbo.getString("descr"));
		return status;
	}

	@Override
	public String createSearchMission(SearchMission mission) throws IOException {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("name", mission.getName());
		dbo.append("descr", mission.getDescription());
		dbo.append("prio", mission.getPrio());
		dbo.append("status", mission.getStatus().getId());
		dbo.append("files", new BasicDBList());
		WriteResult result = missionColl.insert(dbo);
		checkResult(result, OPTYPE.INSERT);
		return dbo.getObjectId("_id").toString();
	}

	@Override
	public void editSearchMission(SearchMission mission, String missionId)
			throws IOException {
		BasicDBObject query = makeObjectIdQuery(missionId);
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("name", mission.getName());
		dbo.append("descr", mission.getDescription());
		dbo.append("prio", mission.getPrio());
		dbo.append("status", mission.getStatus().getId());
		BasicDBObject setObj = new BasicDBObject("$set", dbo);
		WriteResult result = missionColl.update(query, setObj);
		checkResult(result, OPTYPE.UPDATE);
	}

	@Override
	public SearchOperation findOperation(String opId) throws IOException {
		return null;
	}

	@Override
	public void deleteSearchOperation(String searchOpId) throws IOException {
	}

	@Override
	public void createSearchOperation(SearchOperation operation, String missionId) throws IOException {
	}

	@Override
	public void editSearchOperation(SearchOperation op,
			String missionId) throws IOException {
		BasicDBObject query = new BasicDBObject("_id", op.getId());
		BasicDBObject updateObj = new BasicDBObject(5);
		updateObj.append("title", op.getTitle());
		updateObj.append("descr", op.getDescr());
		updateObj.append("date", op.getDate().getTime());
		updateObj.append("location", op.getLocation());
		updateObj.append("status", op.getStatus().getId());
		WriteResult result = operationsColl.update(query, updateObj);
		checkResult(result, OPTYPE.UPDATE);
	}
	
	@Override
	public SearchGroup getSearchGroup(String groupId) throws IOException {
		return null;
	}

	@Override
	public Map<String, String> getUsersForSearchOp(String opId) throws IOException {
		return null;
	}

	@Override
	public void addSearchGroup(SearchGroup group, String opId) {
	}

	@Override
	public void editSearchGroup(SearchGroup group, String opId) {
	}
	
	@Override
	public SearchOperationWrapper[] getAllSearchOps() throws IOException {
		SearchOperationWrapper[] array = null;
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

	private SearchOperationWrapper[] makeOpsIntroArrayFromCursor(DBCursor cursor) {
		SearchOperationWrapper[] array;
		int count = cursor.count();
		if (count == 0) {
			array = new SearchOperationWrapper[0];
		} else {
			array = new SearchOperationWrapper[count];
			int i = 0;
			while (cursor.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) cursor.next();
				String id = dbo.getString("_id");
				String title = dbo.getString("title");
				String descr = dbo.getString("descr");
				array[i] = new SearchOperationWrapper(id, title, descr);
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
			DBObject query = makeObjectIdQuery(id);
			DBObject limit = new BasicDBObject("zones", 0).append("groups", 0);
			BasicDBObject result = (BasicDBObject) operationsColl.findOne(query, limit);
			if (result != null) {
				op = new SearchOperation();
				op.setId(result.getObjectId("_id").toString());
				op.setTitle(result.getString("title"));
				op.setDescr(result.getString("descr"));
				op.setDate(new Date(result.getLong("date")));
				op.setLocation(result.getString("location"));
				op.setStatus(getSearchOpStatusById(result.getInt("status")));
			}
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return op;
	}
	
	private Status getSearchOpStatusById(int statusId) throws IOException {
		DBObject query = new BasicDBObject("statusid", statusId);
		BasicDBObject dbo = (BasicDBObject) opStatusColl.findOne(query);
		if (dbo == null) {
			throw new IOException();
		}
		Status status = new Status(
				dbo.getInt("statusid"), 
				dbo.getString("name"), 
				dbo.getString("descr"));
		return status;
	}

	@Override
	public void assignUserToSearchOp(String opId, String name, String email,
			String tele) throws IOException {
		try {
			DBObject queryObj = makeObjectIdQuery(opId);
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
	public SearchOperationWrapper[] getSearchOpsByFilter(String title, String location, 
			String startDate, String endDate) throws IOException {
		SearchOperationWrapper[] array = null;
		if (title == null && location == null && startDate == null && endDate == null) {
			throw new IOException("Inga söktermer angivna");
		}
		try {
			BasicDBObject query = new BasicDBObject();
			BasicDBObject limit = new BasicDBObject("_id", 1)
										.append("title", 1)
										.append("descr", 1);
			
			if (title != null && !title.equals("")) {
				query.append("title", title);
			}
			if (location != null && !location.equals("")) {
				query.append("location", location);
			}
			if (startDate != null && !startDate.equals("") 
					&& endDate != null && !endDate.equals("")) {
				query.append("date", new BasicDBObject("$gte", startDate));
				query.append("date", new BasicDBObject("$lte", endDate));
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
	public Status getSearchOpStatus(String statusName) throws IOException {
		DBObject query = new BasicDBObject("name", statusName);
		BasicDBObject dbo = (BasicDBObject) opStatusColl.findOne(query);
		if (dbo == null) {
			throw new IOException();
		}
		Status status = new Status(
				dbo.getInt("statusid"), 
				dbo.getString("name"), 
				dbo.getString("descr"));
		return status;
	}

	@Override
	public void deleteZone(String zoneId) {
	}
	
	@Override
	public void deleteGroup(String groupId) {
	}

	@Override
	public String[] getAllOpLocations() {
		DBObject query = new BasicDBObject();
		DBObject limit = new BasicDBObject("location", 1).append("_id", 0);
		DBCursor cursor = operationsColl.find(query, limit);
		if (cursor != null) {
			String[] array = new String[cursor.count()];
			int i = 0;
			while (cursor.hasNext()) {
				BasicDBObject result = (BasicDBObject) cursor.next();
				array[i] = result.getString("location");
				i++;
			}
			return array;
		}
		return null;
	}

	@Override
	public String[] getAllOpTitles() {
		DBObject query = new BasicDBObject();
		DBObject limit = new BasicDBObject("title", 1).append("_id", 0);
		DBCursor cursor = operationsColl.find(query, limit);
		if (cursor != null) {
			String[] array = new String[cursor.count()];
			int i = 0;
			while (cursor.hasNext()) {
				BasicDBObject result = (BasicDBObject) cursor.next();
				array[i] = result.getString("title");
				i++;
			}
			return array;
		}
		return null;
	}

	@Override
	public String endOperation(String opId) {
		return "Avslutad";
	}

	@Override
	public SearchZone getZoneById(String zoneId) {
		return null;
	}

	@Override
	public void editZone(String zoneId, SearchZone zone) {
	}

	@Override
	public String createZone(String opId, SearchZone zone) {
		return null;
	}

	private BasicDBObject makeObjectIdQuery(String missionId) {
		BasicDBObject basicDBObject = new BasicDBObject(1);
		basicDBObject.append("_id", new ObjectId(missionId));
		return basicDBObject;
	}
	
	private void checkResult(WriteResult result, OPTYPE opType) throws IOException {
		CommandResult lastError = result.getLastError();
		if (!lastError.ok()) {
			throw new IOException(lastError.getErrorMessage());
		}
		if (result.getError() != null) {
			throw new IOException(result.getError());
		}
		if (opType == OPTYPE.UPDATE || opType == OPTYPE.REMOVE) {
			if (result.getN() == 0) {
				throw new IOException("No documents were affected by the operation");
			}
		}
	}
}
