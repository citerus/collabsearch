package se.citerus.lookingfor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.lookingfor.logic.SearchMissionService;

public class SearchMissionTest {

	private static SearchMissionService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		service = new SearchMissionService();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		service.cleanUp();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateAndFindSearchMission() {
		//create search mission
		
		//search for created search mission
			//if not found, fail
	}

}
