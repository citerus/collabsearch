package se.citerus.collabsearch.store.mongodb;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import se.citerus.collabsearch.model.CloudFoundryMongoConnectionInfo;
import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.GroupNode;
import se.citerus.collabsearch.model.Rank;
import se.citerus.collabsearch.model.SearchFinding;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchGroupNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchZoneNotFoundException;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.utils.CloudFoundrySettingsParser;

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
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

@Repository
@Primary
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
		String dbName = "";
		String dbPort = "";
		String dbUser = "";
		String dbPass = "";
		String dbAddr = "";
		
		//New envvar-based config
		try {
			String envVarJsonString = System.getenv("VCAP_SERVICES");
			if (envVarJsonString != null) {
				CloudFoundryMongoConnectionInfo connectionInfo = CloudFoundrySettingsParser.parseVcapServicesEnvVar(envVarJsonString);
				dbName = connectionInfo.getDb();
				dbPort = "" + connectionInfo.getPort();
				dbUser = connectionInfo.getUsername();
				dbPass = connectionInfo.getPassword();
				dbAddr = connectionInfo.getHost();
			} else {
				System.out.println("VCAP_SERVICES not declared, falling back to defaults");
				dbName = "lookingfor";
				dbPort = "27017";
				dbUser = "f6b2f4e2-5b8f-4efd-a98f-a2467f45deb6";
				dbPass = "0d8cfbad-12bd-4283-95c9-5e5c1ee8cd19";
				dbAddr = "localhost";
			}
		} catch (Exception e) {
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
				initCollections(db);
				System.out.println("MongoDB successfully initialized for SearchMissionDAO");
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

	private void initCollections(DB db) {
		missionColl = db.getCollection("searchmissions");
		missionStatusColl = db.getCollection("missionstatuses");
		operationsColl = db.getCollection("searchops");
		opStatusColl = db.getCollection("opstatuses");
		searcherColl = db.getCollection("searchers");
		zonesColl = db.getCollection("searchzones");
		groupsColl = db.getCollection("searchgroups");
	}
	
	public void setDebugDB(String dbName) {
		try {
			MongoOptions options = new MongoOptions();
			options.socketTimeout = 2*60*1000;
			String dbAddr = "localhost";
			String dbPort = "27017";
			ServerAddress addr = new ServerAddress(dbAddr, Integer.parseInt(dbPort));
			mongo = new Mongo(addr, options);
			
			System.out.println("Switching db to " + dbName);
			DB db = mongo.getDB(dbName);
			initCollections(db);
		} catch (Exception e) {
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
		List<FileMetadata> list = Collections.emptyList();
		if (dbList != null) {
			ListIterator<Object> it = dbList.listIterator();
			list = new ArrayList<FileMetadata>();
			while (it.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) it.next();
				list.add(new FileMetadata("", 
						dbo.getString("filename"), 
						dbo.getString("mimetype"), 
						dbo.getString("filepath")));
			}
		}
		return list;
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
		if (cursor == null) {
			return Collections.emptyList();
		}
		while (cursor.hasNext()) {
			BasicDBObject dbo = (BasicDBObject) cursor.next();
			SearchOperation op = new SearchOperation();
			op.setId(dbo.getString("_id"));
			op.setTitle(dbo.getString("title"));
			op.setDescr(dbo.getString("descr"));
			op.setDate(new Date(dbo.getLong("date")));
			op.setLocation(dbo.getString("location"));
			op.setStatus(getSearchOpStatusById(dbo.getInt("status")));
			op.setZones(getZonesByOperation(op.getId()));
			op.setGroups(getGroupsByOperation(op.getId()));
			list.add(op);
		}
		return list;
	}

	private List<SearchZone> getZonesByOperation(String opId) {
		BasicDBObject query = 
				new BasicDBObject("parentop", new ObjectId(opId));
		DBCursor cursor = zonesColl.find(query);
		if (cursor == null) {
			return Collections.emptyList();
		}
		List<SearchZone> list = new ArrayList<SearchZone>();
		while (cursor.hasNext()) {
			BasicDBObject zoneDBO = (BasicDBObject) cursor.next();
			list.add(makeZonePOJO(zoneDBO));
		}
		return list;
	}

	private SearchZone makeZonePOJO(BasicDBObject zoneDBO) {
		SearchZone zone = new SearchZone();
		zone.setId(zoneDBO.getObjectId("_id").toString());
		zone.setTitle(zoneDBO.getString("title"));
		zone.setPriority(zoneDBO.getInt("prio"));
		zone.setZoneCoords(getFormattedZoneCoords(zoneDBO));
		zone.setFindings(getFormattedZoneFindings(zoneDBO));
		zone.setZoomLevel(zoneDBO.getInt("zoomlvl"));
		BasicDBObject centerObj = (BasicDBObject) zoneDBO.get("center");
		Point2D.Double center = new Double();
		center.x = centerObj.getDouble("lat");
		center.y = centerObj.getDouble("lon");
		zone.setCenter(center);
		ObjectId groupOID = zoneDBO.getObjectId("groupid");
		zone.setGroupId(groupOID == null ? null : groupOID.toString());
		return zone;
	}

	private List<SearchGroup> getGroupsByOperation(String opId) {
		BasicDBObject query = 
				new BasicDBObject("parentop", new ObjectId(opId));
		DBCursor cursor = groupsColl.find(query);
		if (cursor == null) {
			return Collections.emptyList();
		}
		List<SearchGroup> list = new ArrayList<SearchGroup>();
		while (cursor.hasNext()) {
			BasicDBObject dbo = (BasicDBObject) cursor.next();
			String id = dbo.getObjectId("_id").toString();
			String name = dbo.getString("name");
			BasicDBObject object = (BasicDBObject) dbo.get("tree");
			GroupNode node = null;
			if (object != null) {
				node = makeGroupTreePOJO(object, null);
			}
			list.add(new SearchGroup(id, name, node));
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
	public SearchOperation findOperation(String opId) throws IOException, SearchOperationNotFoundException {
		BasicDBObject query = makeObjectIdQuery(opId);
		BasicDBObject dbo = (BasicDBObject) operationsColl.findOne(query);
		if (dbo == null) {
			throw new SearchOperationNotFoundException(opId);
		}
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
		result = zonesColl.remove(new BasicDBObject("parentop", new ObjectId(searchOpId)));
		result = groupsColl.remove(new BasicDBObject("parentop", new ObjectId(searchOpId)));
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
			String opId) throws IOException {
		BasicDBObject query = makeObjectIdQuery(opId);
		BasicDBObject innerUpdateObj = new BasicDBObject(5);
		innerUpdateObj.append("title", op.getTitle());
		innerUpdateObj.append("descr", op.getDescr());
		innerUpdateObj.append("date", op.getDate().getTime());
		innerUpdateObj.append("location", op.getLocation());
		innerUpdateObj.append("status", op.getStatus().getId());
		BasicDBObject updateObj = new BasicDBObject("$set", innerUpdateObj);
		WriteResult result = operationsColl.update(query, updateObj);
		checkResult(result, OpType.UPDATE);
	}
	
	@Override
	public SearchGroup getSearchGroup(String groupId) throws IOException, SearchGroupNotFoundException {
		BasicDBObject query = makeObjectIdQuery(groupId);
		BasicDBObject dbo = (BasicDBObject) groupsColl.findOne(query);
		if (dbo == null) {
			throw new SearchGroupNotFoundException();
		}
		SearchGroup group = new SearchGroup();
		group.setId(dbo.getObjectId("_id").toString());
		group.setName(dbo.getString("name"));
		BasicDBObject treeObj = (BasicDBObject) dbo.get("tree");
		if (treeObj != null) {
			group.setTreeRoot(makeGroupTreePOJO(treeObj, null));
		}
		return group;
	}
	
	private GroupNode makeGroupTreePOJO(BasicDBObject nodeObj, GroupNode parent) {
		ObjectId oid = nodeObj.getObjectId("searcherid");
		String rankStr = nodeObj.getString("rank");
		Rank.Title rank = Rank.getRankByName(rankStr);
		GroupNode node = new GroupNode(oid.toString(), rank, parent);
		BasicDBList children = (BasicDBList) nodeObj.get("children");
		if (children != null && children.size() > 0) {
			for (Object childObj : children) {
				GroupNode childNode = makeGroupTreePOJO((BasicDBObject)childObj, node);
				node.addChild(childNode);
			}
		}
		return node;
	}

	@Override
	public String createSearchGroup(SearchGroup group, String opId) throws IOException {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("name", group.getName());
		dbo.append("parentop", new ObjectId(opId));
		if (group.getTreeRoot() != null) {
			dbo.append("tree", makeGroupTreeObj(group.getTreeRoot()));
		}
		WriteResult result = groupsColl.insert(dbo);
		checkResult(result, OpType.INSERT);
		return dbo.getString("_id");
	}
	
	private BasicDBObject makeGroupTreeObj(GroupNode node) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("searcherid", new ObjectId(node.getSearcherId()));
		dbo.append("rank", node.getRank().name());
		if (node.getChildCount() > 0) {
			BasicDBList childList = new BasicDBList();
			for (GroupNode child : node.getChildren()) {
				childList.add(makeGroupTreeObj(child));
			}
			dbo.append("children", childList);
		}
		return dbo;
	}

	@Override
	public void editSearchGroup(SearchGroup group, String groupId) throws IOException {
		BasicDBObject query = makeObjectIdQuery(groupId);
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("name", group.getName());
		if (group.getTreeRoot() != null) {
			dbo.append("tree", makeGroupTreeObj(group.getTreeRoot()));
		}
		BasicDBObject update = new BasicDBObject("$set", dbo);
		WriteResult result = groupsColl.update(query, update);
		checkResult(result, OpType.UPDATE);
	}

	@Override
	public Map<String, String> getSearcherNamesByOp(String opId)
			throws IOException, SearchOperationNotFoundException {
		BasicDBObject query = makeObjectIdQuery(opId);
		BasicDBObject fields = new BasicDBObject("searchers", 1);
		DBObject dbo = operationsColl.findOne(query, fields);
		if (dbo == null) {
			throw new SearchOperationNotFoundException(opId);
		}
		BasicDBList list = (BasicDBList) dbo.get("searchers");
		DBCursor cursor = null;
		if (list != null) {
			BasicDBObject searcherQuery = new BasicDBObject();
			searcherQuery.append("_id", new BasicDBObject("$in", list));
			BasicDBObject fields2 = new BasicDBObject("email", 0).append("tele", 0);
			cursor = searcherColl.find(searcherQuery, fields2);
		}
		if (cursor == null) {
			return Collections.emptyMap();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		while(cursor.hasNext()) {
			BasicDBObject searcher = (BasicDBObject) cursor.next();
			map.put(searcher.getObjectId("_id").toString(), 
				searcher.getString("name"));
		}
		return map;
	}
	
	@Override
	public List<SearcherInfo> getSearchersInfoByOp(String opId) 
			throws IOException, SearchOperationNotFoundException {
		BasicDBObject query = makeObjectIdQuery(opId);
		BasicDBObject fields = new BasicDBObject("searchers", 1);
		DBObject dbo = operationsColl.findOne(query, fields);
		if (dbo == null) {
			throw new SearchOperationNotFoundException(opId);
		}
		BasicDBList list = (BasicDBList) dbo.get("searchers");
		DBCursor cursor = null;
		if (list != null) {
			BasicDBObject searcherQuery = new BasicDBObject();
			searcherQuery.append("_id", new BasicDBObject("$in", list));
			cursor = searcherColl.find(searcherQuery);
		}
		if (cursor == null) {
			return Collections.emptyList();
		}
		List<SearcherInfo> outputList = new ArrayList<SearcherInfo>();
		while(cursor.hasNext()) {
			BasicDBObject searcherDBO = (BasicDBObject) cursor.next();
			SearcherInfo searcher = new SearcherInfo(
					searcherDBO.getObjectId("_id").toString(),
					searcherDBO.getString("name"),
					searcherDBO.getString("email"),
					searcherDBO.getString("tele"));
			outputList.add(searcher);
		}
		return outputList;
	}
	
	@Override
	public SearchOperationWrapper[] getAllSearchOpsInShortForm() throws IOException {
		SearchOperationWrapper[] array = null;
		try {
			BasicDBObject missionQuery = new BasicDBObject("status", new BasicDBObject("$ne", 0));
			DBObject fields = new BasicDBObject("_id", 1);
			DBCursor cursor2 = missionColl.find(missionQuery, fields);
			ArrayList<ObjectId> idList = new ArrayList<ObjectId>();
			while (cursor2.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) cursor2.next();
				idList.add(dbo.getObjectId("_id"));
			}
			DBObject opQuery = new BasicDBObject("status", 2);
			DBObject limit = new BasicDBObject("_id", 1)
									.append("title", 1)
									.append("descr", 1)
									.append("mission", 1);
			DBCursor cursor = operationsColl.find(opQuery, limit);
			if (cursor != null) {
				ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
				while (cursor.hasNext()) {
					BasicDBObject dbo = (BasicDBObject) cursor.next();
					if (inListOfActiveMissions(idList, dbo.getObjectId("mission"))) {
						list.add(dbo);
					}
				}
				array = makeOpsIntroArrayFromCursor(list);
			}
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return array;
	}
	
	@Override
	public SearchOperationWrapper[] getSearchOpsByFilter(String title, String location, 
			String startDate, String endDate) throws IOException {
		SearchOperationWrapper[] array = null;
		try {
			BasicDBObject missionQuery = new BasicDBObject("status", new BasicDBObject("$ne", 0));
			DBObject fields = new BasicDBObject("_id", 1);
			DBCursor cursor2 = missionColl.find(missionQuery, fields);
			if (cursor2 != null) {
				ArrayList<ObjectId> idList = new ArrayList<ObjectId>();
				while (cursor2.hasNext()) {
					BasicDBObject dbo = (BasicDBObject) cursor2.next();
					idList.add(dbo.getObjectId("_id"));
				}
				
				BasicDBObject query = new BasicDBObject("status", 2);
				BasicDBObject limit = new BasicDBObject("_id", 1)
											.append("title", 1)
											.append("descr", 1)
											.append("mission", 1);
				
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
					ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
					while (cursor.hasNext()) {
						BasicDBObject dbo = (BasicDBObject) cursor.next();
						if (inListOfActiveMissions(idList, dbo.getObjectId("mission"))) {
							list.add(dbo);
						}
					}
					array = makeOpsIntroArrayFromCursor(list);
				}
			}
		} catch (Exception e) {
			throw new IOException("Kontakt med databasen kunde ej upprättas", e);
		}
		return array;
	}

	private boolean inListOfActiveMissions(ArrayList<ObjectId> list, ObjectId oid) {
		for (ObjectId otherId : list) {
			if (otherId.equals(oid)) {
				return true;
			}
		}
		return false;
	}

	private SearchOperationWrapper[] makeOpsIntroArrayFromCursor(List<BasicDBObject> dboList) {
		SearchOperationWrapper[] array;
		int count = dboList.size();
		if (count == 0) {
			array = new SearchOperationWrapper[0];
		} else {
			array = new SearchOperationWrapper[count];
			int i = 0;
			for (BasicDBObject dbo : dboList) {
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
			ObjectId searcherId = resultDbo.getObjectId("_id");

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
	public String endOperation(String opId) throws IOException {
		DBObject query = makeObjectIdQuery(opId);
		BasicDBObject statusObj = new BasicDBObject("status", 0);
		DBObject update = new BasicDBObject("$set", statusObj);
		WriteResult result = operationsColl.update(query, update);
		checkResult(result, OpType.UPDATE);
		return "Avslutad";
	}

	@Override
	public SearchZone getZoneById(String zoneId) throws SearchZoneNotFoundException {
		BasicDBObject query = makeObjectIdQuery(zoneId);
		BasicDBObject zoneDBO = (BasicDBObject) zonesColl.findOne(query);
		if (zoneDBO == null) {
			throw new SearchZoneNotFoundException(zoneId);
		}
		return makeZonePOJO(zoneDBO);
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
	public void editZone(String zoneId, SearchZone zone) throws IOException {
		DBObject query = makeObjectIdQuery(zoneId);
		BasicDBObject updateObj = makeZoneDBO(zone);
		
		BasicDBObject dbo = new BasicDBObject("$set", updateObj);
		WriteResult result = zonesColl.update(query, dbo);
		checkResult(result, OpType.UPDATE);
	}

	@Override
	public String createZone(String opId, SearchZone zone) throws IOException {
		BasicDBObject zoneDbo = makeZoneDBO(zone);
		zoneDbo.append("parentop", new ObjectId(opId));
		
		WriteResult result = zonesColl.insert(zoneDbo);
		checkResult(result, OpType.INSERT);
		return zoneDbo.getObjectId("_id").toString();
	}

	private BasicDBObject makeZoneDBO(SearchZone zone) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.append("title", zone.getTitle());
		dbo.append("prio", zone.getPriority());
		dbo.append("zoomlvl", zone.getZoomLevel());
		BasicDBObject centerDBO = new BasicDBObject();
		centerDBO.append("lat", zone.getCenter().x);
		centerDBO.append("lon", zone.getCenter().y);
		dbo.append("center", centerDBO);
		dbo.append("groupid", zone.getGroupId() == null ? null : new ObjectId(zone.getGroupId()));
		
		Double[] zoneCoords = zone.getZoneCoords();
		BasicDBList coords = new BasicDBList();
		if (zoneCoords != null) {
			for (Double coord : zoneCoords) {
				BasicDBObject coordObj = new BasicDBObject(2);
				coordObj.append("lat", coord.getX());
				coordObj.append("lon", coord.getY());
				coords.add(coordObj);
			}
		}
		dbo.append("coords", coords);
		
		SearchFinding[] zoneFindings = zone.getFindings();
		BasicDBList findings = new BasicDBList();
		if (zoneFindings != null) {
			for (SearchFinding find : zoneFindings) {
				BasicDBObject findObj = new BasicDBObject();
				findObj.append("title", find.getTitle());
				findObj.append("descr", find.getDescr());
				findObj.append("lat", find.getLat());
				findObj.append("lon", find.getLon());
				findings.add(findObj);
			}
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

	@Override
	public List<SearchOperation> getAllSearchOps() throws IOException {
		DBCursor cursor = operationsColl.find();
		if (cursor == null) {
			return Collections.emptyList();
		}
		List<SearchOperation> list = new ArrayList<SearchOperation>();
		while (cursor.hasNext()) {
			BasicDBObject dbo = (BasicDBObject) cursor.next();
			SearchOperation op = new SearchOperation();
			op.setId(dbo.getString("_id"));
			op.setTitle(dbo.getString("title"));
			op.setDescr(dbo.getString("descr"));
			op.setDate(new Date(dbo.getLong("date")));
			op.setLocation(dbo.getString("location"));
			op.setStatus(getSearchOpStatusById(dbo.getInt("status")));
			op.setZones(getZonesByOperation(op.getId()));
			op.setGroups(getGroupsByOperation(op.getId()));
			list.add(op);
		}
		return list;
	}

	/**
	 * Returns a list of SearchGroups (searcher hierarchies excluded) with parent ops matching the supplied id.
	 */
	@Override
	public List<SearchGroup> getSearchGroupsByOp(String opId) throws IOException, SearchGroupNotFoundException {
		List<SearchGroup> list = new ArrayList<SearchGroup>();
		BasicDBObject query = new BasicDBObject("parentop", 
			new ObjectId(opId));
		BasicDBObject fields = new BasicDBObject("name", 1);
		DBCursor cursor = groupsColl.find(query, fields);
		if (cursor == null) {
			throw new SearchGroupNotFoundException("No groups for op with id: " + opId);
		}
		while (cursor.hasNext()) {
			BasicDBObject dbo = (BasicDBObject) cursor.next();
			String name = dbo.getString("name");
			String id = dbo.getObjectId("_id").toString();
			list.add(new SearchGroup(id, name, null));
		}
		return list;
	}

	@Override
	public String getOpIdByZone(String zoneId) throws IOException, SearchOperationNotFoundException {
		BasicDBObject query = makeObjectIdQuery(zoneId);
		BasicDBObject fields = new BasicDBObject("parentop", 1).append("_id", 0);
		BasicDBObject dbo = (BasicDBObject) zonesColl.findOne(query, fields);
		if (dbo == null) {
			throw new SearchOperationNotFoundException(
				"The zone with the id " + zoneId + " does not have a parent op.");
		}
		String opId = dbo.getObjectId("parentop").toString();
		return opId;
	}

	@Override
	public boolean getDatabaseStatus() throws IOException {
		try {
			List<String> databaseNames = mongo.getDatabaseNames();
			if (databaseNames.isEmpty()) {
				throw new IOException("No databases found");
			}
			for (String dbName : databaseNames) {
				DB db = mongo.getDB(dbName);
				Set<String> collectionNames = db.getCollectionNames();
				for (String collectionName : collectionNames) {
					DBCollection collection = db.getCollection(collectionName);
					CommandResult stats = collection.getStats();
					if (stats == null) {
						throw new IOException();
					}
				}
			}
			return true;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
}
