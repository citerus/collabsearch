package se.citerus.collabsearch.store;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.store.mongodb.SearchMissionDAOMongoDB;

public class SearchMissionDAOMongoDBTests {

	private static SearchMissionDAOMongoDB dao;
	private static Random rand;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dao = new SearchMissionDAOMongoDB();
		rand = new Random();
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
				assertTrue(sm.getPrio() >= 0);
			}
		}
	}

	@Test
	public void testGetAllSearchMissionStatuses() throws IOException {
		List<Status> list = dao.getAllSearchMissionStatuses();
		assertNotNull(list);
		assertTrue(!list.isEmpty());
		for (Status status : list) {
			assertNotNull(status);
			
			assertNotNull(status.getName());
			assertTrue(status.getName().length() > 0);
			
			assertNotNull(status.getDescr());
			assertTrue(status.getDescr().length() > 0);
			
			assertTrue(status.getId() >= 0);
		}
	}
	
	@Test
	public void testCreateSearchMission() throws Exception {
		String createdId = dao.createSearchMission(
			new SearchMission("test1", "blab", 1, 
				new Status(1, "blab", "blab")));
		assertNotNull(createdId);
		assertTrue(createdId.length() > 0);
	}
	
	@Test
	public void testFindMission() throws Exception {
		String createdId = dao.createSearchMission(
				new SearchMission("test2", "blab", 1, 
					new Status(1, "blab", "blab")));
		
		SearchMission sm = dao.findMission(createdId);
		assertNotNull(sm);
		
		assertNotNull(sm.getId());
		assertTrue(ObjectId.isValid(sm.getId()));
		
		assertNotNull(sm.getName());
		assertTrue(sm.getName().length() > 0);
		
		assertNotNull(sm.getDescription());
		assertTrue(sm.getDescription().length() > 0);
		
		assertNotNull(sm.getPrio());
		assertTrue(sm.getPrio() > 0);
	}
	
	@Test
	public void testEndMission() throws SearchMissionNotFoundException, Exception {
		final int ENDED_MISSION_STATUS_ID = 0;
		
		String createdId = dao.createSearchMission(
				new SearchMission("test3", "blab", 1, 
					new Status(1, "blab", "blab")));
		
		SearchMission searchMission = dao.findMission(createdId);
		
		Status statusBefore = searchMission.getStatus();
		dao.endMission(searchMission.getId());
		
		SearchMission updatedMission = dao.findMission(searchMission.getId());
		Status statusAfter = updatedMission.getStatus();
		
		assert(statusAfter.getId() == ENDED_MISSION_STATUS_ID);
		assert(statusBefore.getId() != statusAfter.getId() == false);
		Assert.assertNotSame("Status not changed", statusBefore, statusAfter);
	}
	
	@Test
	public void testEditSearchMission() throws Exception {
		String createdId = dao.createSearchMission(
				new SearchMission("test4", "blab", 1, 
					new Status(1, "blab", "blab")));
		
		SearchMission oldMission = dao.findMission(createdId);
		Status newStatus = new Status(rand.nextInt(16+1));
		while (newStatus.getId() == oldMission.getStatus().getId()) {
			newStatus.setId(rand.nextInt(16+1));
		}
		SearchMission newMissionData = new SearchMission(
				null, 
				oldMission.getName() + "(changed)", 
				oldMission.getDescription() + "(changed)", 
				oldMission.getPrio() + 1, 
				newStatus);
		dao.editSearchMission(newMissionData, createdId); 
		SearchMission editedMission = dao.findMission(createdId);
		
		assertNotNull(editedMission);
		assertEquals("", oldMission.getId(), editedMission.getId());
		assertEquals(newMissionData.getName(), editedMission.getName());
		assertEquals(newMissionData.getDescription(), editedMission.getDescription());
		assertEquals(newMissionData.getPrio(), editedMission.getPrio());
		assertEquals(newMissionData.getStatus().getId(), editedMission.getStatus().getId());
	}
}
