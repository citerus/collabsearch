package se.citerus.collabsearch.store;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.mongodb.SearchMissionDAOMongoDB;

public class SearchOperationDAOMongoDBTests {

	private static SearchOperationDAO dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dao = new SearchMissionDAOMongoDB();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//cleanup
	}
	

//	@Test
//	public void testFindOperation() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteSearchOperation() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCreateSearchOperation() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEditSearchOperation() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetSearchGroup() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetUsersForSearchOp() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddSearchGroup() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEditSearchGroup() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetAllSearchOps() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetSearchOpById() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAssignUserToSearchOp() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetSearchOpsByFilter() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetAllSearchOpStatuses() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetSearchOpStatus() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteZone() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteGroup() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetAllOpLocations() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetAllOpTitles() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEndOperation() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetZoneById() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEditZone() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCreateZone() {
//		fail("Not yet implemented");
//	}

}
