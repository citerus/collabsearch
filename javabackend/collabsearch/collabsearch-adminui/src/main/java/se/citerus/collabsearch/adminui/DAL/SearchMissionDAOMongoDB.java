package se.citerus.collabsearch.adminui.DAL;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

public class SearchMissionDAOMongoDB implements SearchMissionDAO {

	private Mongo mongo;
	private DBCollection missionColl;
	private DBCollection statusColl;
	private DBCollection operationsColl;

	public SearchMissionDAOMongoDB() throws Exception {
		mongo = new Mongo();
		DB db = mongo.getDB("lookingfor");
		missionColl = db.getCollection("searchmissions");
	}
	
	public List<SearchMission> getAllSearchMissions() {
		BasicDBObject query = new BasicDBObject();
		DBObject limit = new BasicDBObject("_id",0);
		DBCursor cursor = missionColl.find(query, limit);
		if (cursor != null) {
			ArrayList<SearchMission> list = new ArrayList<SearchMission>();
			while (cursor.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) cursor.next();
				list.add(new SearchMission(
						dbo.getString("name"), 
						dbo.getString("description"), 
						dbo.getInt("prio"), 
						null
				));
			}
			return list;
		}
		return new ArrayList<SearchMission>(0);
	}

	public void disconnect() {
		mongo.close();
	}

	public void endMission(String name) throws IOException {
		
	}

	public void addOrModifyMission(SearchMission mission) throws IOException {
	}

	public List<SearchOperation> getAllSearchOpsForMission(String missionName)
			throws IOException {
		return null;
	}

	public List<Status> getAllStatuses() throws IOException {
		return null;
	}

	public SearchMission findMission(String missionName) {
		return null;
	}

	public void addFileMetadata(String missionName, FileMetadata metadata)
			throws IOException {
	}

	public void deleteFileMetadata(String filename, String missionName)
			throws IOException {
	}

	public SearchOperation findOperation(String name, String missionName)
			throws IOException {
		return null;
	}

	public void deleteSearchOperation(String searchOpName, String missionName)
			throws IOException {
	}

	public void addOrModifySearchOperation(SearchOperation operation,
			String missionName) throws IOException {
	}

	public Status findStatus(String statusName) throws IOException {
		return null;
	}

}
