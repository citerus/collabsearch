package se.citerus.lookingfor.DAL;

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

import se.citerus.lookingfor.logic.SearchMission;

public class SearchMissionDALMongoDB implements SearchMissionDAL {

	private Mongo mongo;
	private DBCollection smColl;

	public SearchMissionDALMongoDB() {
		try {
			mongo = new Mongo();
			DB db = mongo.getDB("lookingfor");
			smColl = db.getCollection("searchmissions");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	public List<SearchMission> getAllSearchMissions() {
		BasicDBObject query = new BasicDBObject();
		DBObject limit = new BasicDBObject("_id",0);
		DBCursor cursor = smColl.find(query, limit);
		if (cursor != null) {
			ArrayList<SearchMission> list = new ArrayList<SearchMission>();
			while (cursor.hasNext()) {
				BasicDBObject dbo = (BasicDBObject) cursor.next();
				list.add(new SearchMission(dbo.getString("name"), dbo.getString("description"), dbo.getInt("status")));
			}
			return list;
		}
		return new ArrayList<SearchMission>(0);
	}

	public void disconnect() {
		mongo.close();
	}

}
