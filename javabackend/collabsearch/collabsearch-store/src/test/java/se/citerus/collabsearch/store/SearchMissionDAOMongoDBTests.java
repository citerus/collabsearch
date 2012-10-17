package se.citerus.collabsearch.store;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.Validate;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.mongodb.SearchMissionDAOMongoDB;
import se.citerus.collabsearch.store.mongodb.SearchMissionNotFoundException;

public class SearchMissionDAOMongoDBTests {

	private static SearchMissionDAOMongoDB dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dao = new SearchMissionDAOMongoDB();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dao.disconnect();
	}

	@Test
	public void testGetAllSearchMissions() throws IOException {
		List<SearchMission> list = dao.getAllSearchMissions();
		assertNotNull(list);
		
		if (!list.isEmpty()) {
			for (SearchMission sm : list) {
				assertNotNull(sm.getId());
				assertTrue(ObjectId.isValid(sm.getId()));
				
				assertNotNull(sm.getName());
				assertTrue(sm.getName().length() > 0);
				
				assertNotNull(sm.getDescription());
				assertTrue(sm.getDescription().length() > 0);
				
				assertNotNull(sm.getPrio());
				assertTrue(sm.getPrio() > 0);
			}
		}
	}
	
	@Test
	public void testEndMission() throws SearchMissionNotFoundException, Exception {
		final int ENDED_MISSION_STATUS_ID = 0;
		
		SearchMission searchMission = dao.getAllSearchMissions().get(0);
		
		Status statusBefore = searchMission.getStatus();
		dao.endMission(searchMission.getId());
		
		SearchMission updatedMission = dao.findMission(searchMission.getId());
		Status statusAfter = updatedMission.getStatus();
		
		assert(statusAfter.getId() == ENDED_MISSION_STATUS_ID);
		assert(statusBefore.getId() != statusAfter.getId() == false);
		Assert.assertNotSame("Status not changed", statusBefore, statusAfter);
	}

}
