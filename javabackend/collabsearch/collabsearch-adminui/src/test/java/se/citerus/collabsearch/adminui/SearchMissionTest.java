package se.citerus.collabsearch.adminui;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;

import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;

public class SearchMissionTest {

	private static SearchMissionService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		service = new SearchMissionService();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		service.cleanUp();
		
//		DB db = new Mongo().getDB("lookingfor");
//		DBCollection missionColl = db.getCollection("searchmissions");
//		Pattern regex = Pattern.compile("testMission", Pattern.CASE_INSENSITIVE);
//		missionColl.remove(new BasicDBObject("name", regex));
	}

	@Test
	public void testCreateNewSearchMission() throws Exception {
		//create search mission
		Status status = new Status(0);
		SearchMission mission = new SearchMission("testMission1", "blab", 1, status);
		service.addOrModifyMission(mission, null);
	}
	
	@Test(expected=Exception.class)
	public void testCreateNewNullSearchMission() throws Exception {
		SearchMission mission = null;
		String createdId = service.addOrModifyMission(mission, null);
		assertNotNull(createdId);
		assertFalse(createdId.isEmpty());
	}
	
	@Test
	public void testEditSearchMission() throws Exception {
		final String createdId = service.addOrModifyMission(new SearchMission(
				"testMission2", "blab", 1, new Status(0)), null);
		assertNotNull(createdId);
		assertFalse(createdId.isEmpty());
		
		SearchMission oldMission = service.getSearchMissionData(createdId);
		assertNotNull(oldMission);
		
		SearchMission editedMission = new SearchMission(
				oldMission.getName().concat("v2"), 
				oldMission.getDescription().concat("v2"), 
				oldMission.getPrio() + 1, 
				new Status(oldMission.getStatus().getId() + 1));
		service.addOrModifyMission(editedMission, createdId);
		
		SearchMission newMission = service.getSearchMissionData(createdId);
		assertNotNull(newMission);
		assertTrue(editedMission.getName().equals(newMission.getName()));
		assertTrue(editedMission.getDescription().equals(newMission.getDescription()));
		assertTrue(editedMission.getPrio() == newMission.getPrio());
		assertTrue(editedMission.getStatus().getId() == newMission.getStatus().getId());
	}
	
	@Test(expected=Exception.class)
	public void testEditNullSearchMission() throws Exception {
		final String createdId = service.addOrModifyMission(new SearchMission(
				"testMission3", "blab", 1, new Status(0)), null);
		service.addOrModifyMission(null, createdId);
	}

}
