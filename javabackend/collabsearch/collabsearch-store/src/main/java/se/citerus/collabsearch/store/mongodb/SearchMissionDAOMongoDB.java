package se.citerus.collabsearch.store.mongodb;

import java.awt.geom.Point2D.Double;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchFinding;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBPort;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.WriteResult;

@Repository
public class SearchMissionDAOMongoDB implements SearchMissionDAO, SearchOperationDAO {

	private Mongo mongo;
	private DBCollection missionColl;
	private DBCollection missionStatusColl;
	private DBCollection opStatusColl;
	private DBCollection operationsColl;
	private DBCollection searcherColl;
	private DBCollection zonesColl;
	private DBCollection groupsColl;

	private enum OpType {
		INSERT, UPDATE, REMOVE
	}
	
	public SearchMissionDAOMongoDB() {
	}
	
	@PostConstruct
	public void init() {
//		Log4JLogger logger = new Log4JLogger();
//		logger.info("Init db");
		
		String dbName = "test";
		Properties prop = new Properties();
		try {
			InputStream stream;
			stream = SearchMissionDAOMongoDB.class.getResourceAsStream(
				"/db-server-config.properties");
			if (stream != null) {
				prop.load(stream);
				dbName = prop.getProperty("DBNAME");
			}
			System.out.println("Configured database name: " + dbName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MongoOptions options = new MongoOptions();
		options.socketTimeout = 2*60*1000;
		String serverAddress = "localhost:" + DBPort.PORT;
		try {
			mongo = new Mongo(serverAddress, options);
			DB db = mongo.getDB(dbName);
			missionColl = db.getCollection("searchmissions");
			missionStatusColl = db.getCollection("missionstatuses");
			operationsColl = db.getCollection("searchops");
			opStatusColl = db.getCollection("opstatuses");
			searcherColl = db.getCollection("searchers");
			zonesColl = db.getCollection("zones");
			groupsColl = db.getCollection("groups");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void cleanUp() {
		mongo.close();
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
//		mongo.close();
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
		mission.setFileList(convertMissionFileList((BasicDBList)dbo.get("files")));
		mission.setOpsList(getAllSearchOpsForMission(mission.getId()));
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
	public String addFileMetadata(String missionId, FileMetadata metadata)
			throws IOException {
		BasicDBObject query = makeObjectIdQuery(missionId);
		BasicDBObject metadataObj = new BasicDBObject();
		metadataObj.append("filename", metadata.getFileName());
		metadataObj.append("mimetype", metadata.getMimeType());
		metadataObj.append("filepath", metadata.getFilePath());
		BasicDBObject update = new BasicDBObject("$push", 
				new BasicDBObject("files", metadataObj));
		WriteResult result = missionColl.update(query, update);
		checkResult(result, OpType.UPDATE);
		return metadata.getFileName();
	}

	@Override
	public String deleteFileMetadata(String fileName, String missionId)
			throws IOException {
		DBObject query = makeObjectIdQuery(missionId);
		DBObject deleteObj = new BasicDBObject("$pull", 
			new BasicDBObject("files", new BasicDBObject("filename", fileName)));
		WriteResult result = missionColl.update(query, deleteObj);
		checkResult(result, OpType.REMOVE);
		return fileName;
	}

	@Override
	public FileMetadata getFileMetadata(String filename, String missionId) throws SearchMissionNotFoundException, FileNotFoundException {
		BasicDBObject query = makeObjectIdQuery(missionId);
		BasicDBObject keys = new BasicDBObject("files", 1).append("_id", 0);
		BasicDBObject dbo = (BasicDBObject) missionColl.findOne(query, keys);
		if (dbo == null) {
			throw new SearchMissionNotFoundException(missionId);
		}
		BasicDBList filesObj = (BasicDBList) dbo.get("files");
		FileMetadata fileMetadata = null;
		for (Object obj : filesObj) {
			BasicDBObject fileObj = (BasicDBObject) obj;
			if (fileObj.getString("filename").equals(filename)) {
				fileMetadata = new FileMetadata(null, 
						fileObj.getString("filename"), 
						fileObj.getString("mimetype"), 
						fileObj.getString("filepath"));
				break;
			}
		}
		if (fileMetadata == null) {
			throw new FileNotFoundException(filename);
		}
		return fileMetadata;
	}

	@Override
	public Status findMissionStatusByName(String statusName) throws IOException {
		DBObject query = new BasicDBObject("name", statusName);
		DBObject keys = new BasicDBObject("_id", 0);
		BasicDBObject dbo = (BasicDBObject) missionStatusColl.findOne(query, keys);
		if (dbo == null) {
			throw new IOException("Status " + statusName + " not found");
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
		checkResult(result, OpType.INSERT);
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
		checkResult(result, OpType.UPDATE);
	}

	@Override
	public SearchOperation findOperation(String opId) throws IOException {
		BasicDBObject query = makeObjectIdQuery(opId);
		BasicDBObject dbo = (BasicDBObject) operationsColl.findOne(query);
		SearchOperation op = new SearchOperation();
		op.setId(dbo.getString("_id"));
		op.setTitle(dbo.getString("title"));
		op.setDescr(dbo.getString("descr"));
		op.setDate(new Date(dbo.getLong("date")));
		op.setLocation(dbo.getString("location"));
		op.setStatus(getSearchOpStatusById(dbo.getInt("status")));
		return op;
	}

	@Override
	public void deleteSearchOperation(String searchOpId) throws IOException {
		BasicDBObject query = makeObjectIdQuery(searchOpId);
		WriteResult result = operationsColl.remove(query);
		checkResult(result, OpType.REMOVE);
	}

	@Override
	public String createSearchOperation(SearchOperation op, String missionId) throws IOException {
		BasicDBObject opDbo = new BasicDBObject(7);
		opDbo.append("title", op.getTitle());
		opDbo.append("descr", op.getDescr());
		opDbo.append("date", op.getDate().getTime());
		opDbo.append("location", op.getLocation());
		opDbo.append("status", op.getStatus().getId());
		opDbo.append("mission", new ObjectId(missionId));
		WriteResult result = operationsColl.insert(opDbo);
		checkResult(result, OpType.INSERT);
		String opId = opDbo.getObjectId("_id").toString();
		return opId;
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
		checkResult(result, OpType.UPDATE);
	}
	
	@Override
	public SearchGroup getSearchGroup(String groupId) throws IOException {
		BasicDBObject query = makeObjectIdQuery(groupId);
		BasicDBObject dbo = (BasicDBObject) operationsColl.findOne(query);
		SearchGroup group = new SearchGroup();
		group.setId(dbo.getObjectId("_id").toString());
		group.setName(dbo.getString("name"));
		//TODO traverse and convert tree
		//group.setTreeRoot(treeRoot);
		return group;
	}

	@Override
	public Map<String, String> getUsersForSearchOp(String opId) throws IOException {
		BasicDBObject query = makeObjectIdQuery(opId);
		BasicDBObject fields = new BasicDBObject("searchers", 1);
		DBObject dbo = operationsColl.findOne(query, fields);
		BasicDBList list = (BasicDBList) dbo.get("searchers");
		BasicDBObject searcherQuery = new BasicDBObject();
		searcherQuery.append("_id", new BasicDBObject("$in", list));
		BasicDBObject fields2 = new BasicDBObject("email", 0).append("tele", 0);
		DBCursor cursor = searcherColl.find(searcherQuery, fields2 );
		HashMap<String, String> map = new HashMap<String, String>();
		while(cursor.hasNext()) {
			BasicDBObject searcher = (BasicDBObject) cursor.next();
			map.put(searcher.getObjectId("_id").toString(), 
				searcher.getString("name"));
		}
		return map;
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
			String tele) throws SearchOperationNotFoundException,IOException {
		try {
			BasicDBObject query = new BasicDBObject();
			query.append("name", name);
			query.append("tele", tele);
			query.append("email", email);
			BasicDBObject update = query;
			DBObject sort = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			BasicDBObject resultDbo = (BasicDBObject) searcherColl.findAndModify(
				query, fields, sort, false, update, true, true);
			if (resultDbo == null) {
				throw new IOException("Could not add searcher to searchop with id " + opId);
			}
			String searcherId = resultDbo.getObjectId("_id").toString();

			BasicDBObject opQuery = makeObjectIdQuery(opId);
			BasicDBObject opUpdate = new BasicDBObject("$push",
				new BasicDBObject("searchers", searcherId));
			WriteResult result = operationsColl.update(opQuery, opUpdate);
			checkResult(result, OpType.UPDATE);
		} catch (IOException e) {
			throw new SearchOperationNotFoundException(opId);
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
	}

	@Override
	public SearchOperationWrapper[] getSearchOpsByFilter(String title, String location, 
			String startDate, String endDate) throws IOException {
		SearchOperationWrapper[] array = null;
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
			BasicDBObject dateQuery = new BasicDBObject();
			if (startDate != null && !startDate.equals("")) {
				dateQuery.append("$gte", Long.parseLong(startDate));
			}
			if (endDate != null && !endDate.equals("")) {
				dateQuery.append("$lte", Long.parseLong(endDate));
			}
			if (!dateQuery.isEmpty()) {
				query.append("date", dateQuery);
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
		DBCursor cursor = opStatusColl.find();
		List<Status> list = new ArrayList<Status>(3);
		while (cursor.hasNext()) {
			BasicDBObject dbo = (BasicDBObject) cursor.next();
			list.add(new Status(dbo.getInt("statusid"),
				dbo.getString("name"), dbo.getString("descr")));
		}
		return list;
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
	public void deleteZone(String zoneId) throws IOException {
		DBObject query = makeObjectIdQuery(zoneId);
		WriteResult result = zonesColl.remove(query);
		checkResult(result, OpType.REMOVE);
	}
	
	@Override
	public void deleteGroup(String groupId) throws IOException {
		DBObject query = makeObjectIdQuery(groupId);
		WriteResult result = groupsColl.remove(query);
		checkResult(result, OpType.REMOVE);
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
		return new String[0];
	}

	@Override
	public String endOperation(String opId) {
		DBObject query = makeObjectIdQuery(opId);
		BasicDBObject statusObj = new BasicDBObject();
		DBObject update = new BasicDBObject("$set", statusObj);
		operationsColl.update(query, update);
		return "Avslutad";
	}

	@Override
	public SearchZone getZoneById(String zoneId) {
		BasicDBObject query = makeObjectIdQuery(zoneId);
		BasicDBObject zoneDBO = (BasicDBObject) zonesColl.findOne(query);
		SearchZone zone = new SearchZone();
		zone.setId(zoneDBO.getString("_id"));
		zone.setTitle(zoneDBO.getString("title"));
		zone.setPriority(zoneDBO.getInt("prio"));
		zone.setZoneCoords(getFormattedZoneCoords(zoneDBO));
		zone.setFindings(getFormattedZoneFindings(zoneDBO));
		zone.setZoomLevel(zoneDBO.getInt("zoomlvl"));
		return zone;
	}
	
	private Double[] getFormattedZoneCoords(BasicDBObject dbo) {
		BasicDBList list = (BasicDBList) dbo.get("coords");
		if (list == null) {
			return new Double[0];
		}
		ListIterator<Object> it = list.listIterator();
		Double[] array = new Double[list.size()];
		int i = 0;
		while (it.hasNext()) {
			BasicDBObject coord = (BasicDBObject) it.next();
			array[i] = new Double(coord.getDouble("lat"), 
				coord.getDouble("lon"));
			i++;
		}
		return array;
	}

	private SearchFinding[] getFormattedZoneFindings(BasicDBObject dbo) {
		//TODO convert findings' coords from json to java
		BasicDBList list = (BasicDBList) dbo.get("findings");
		if (list == null) {
			return new SearchFinding[0];
		}
		SearchFinding[] array = new SearchFinding[list.size()];
		ListIterator<Object> it = list.listIterator();
		int i = 0;
		while (it.hasNext()) {
			BasicDBObject next = (BasicDBObject) it.next();
			array[i] = new SearchFinding(
				next.getDouble("lat"), 
				next.getDouble("lon"),
				next.getString("title"), 
				next.getString("descr"));
			i++;
		}
		return array;
	}

	@Override
	public void editZone(String zoneId, SearchZone zone) {
		BasicDBObject updateObj = makeZoneDBO(zone);
		
		BasicDBObject dbo = new BasicDBObject("$set", updateObj);
		DBObject query = makeObjectIdQuery(zoneId);
		zonesColl.update(query, dbo);
	}

	@Override
	public String createZone(String opId, SearchZone zone) throws IOException {
		BasicDBObject zoneDbo = makeZoneDBO(zone);
		
		WriteResult result = zonesColl.insert(zoneDbo);
		checkResult(result, OpType.INSERT);
		return zoneDbo.getObjectId("_id").toString();
	}

	private BasicDBObject makeZoneDBO(SearchZone zone) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("title", zone.getTitle());
		dbo.append("prio", zone.getPriority());
		dbo.append("zoomlvl", zone.getZoomLevel());
		
		Double[] zoneCoords = zone.getZoneCoords();
		BasicDBList coords = new BasicDBList();
		for (Double coord : zoneCoords) {
			BasicDBObject coordObj = new BasicDBObject(2);
			coordObj.append("lat", coord.getX());
			coordObj.append("lon", coord.getY());
			coords.add(coordObj);
		}
		dbo.append("coords", coords);
		
		SearchFinding[] zoneFindings = zone.getFindings();
		BasicDBList findings = new BasicDBList();
		for (SearchFinding find : zoneFindings) {
			BasicDBObject findObj = new BasicDBObject();
			findObj.append("title", find.getTitle());
			findObj.append("descr", find.getDescr());
			findObj.append("lat", find.getLat());
			findObj.append("lon", find.getLon());
			findings.add(findObj);
		}
		dbo.append("findings", findings);
		
		return dbo;
	}

	private BasicDBObject makeObjectIdQuery(String id) {
		return new BasicDBObject("_id", new ObjectId(id));
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
				throw new IOException("No documents were affected by the operation");
			}
		}
	}
}
