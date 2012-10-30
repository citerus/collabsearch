package se.citerus.collabsearch.publicwebsite;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.publicwebsite.logic.Model;
import se.citerus.collabsearch.publicwebsite.mockups.MockupController;

public class OpsListViewTest {

	private static Model model;
	private static MockupController controller;

	@Before
	public void preTest() {
		controller.errorThrown = false;
	}
	
	@After
	public void postTest() {
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		controller = new MockupController();
		model = new Model(controller);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Test
	public void testGetAllOps() {
		SearchOperationWrapper[] ops = model.getAllSearchOps();
		assertTrue(ops.length > 0);
		
		for (SearchOperationWrapper intro : ops) {
			assertNotNull(intro);
			assertNotEmpty(intro.getId());
			assertNotEmpty(intro.getTitle());
			assertNotEmpty(intro.getDescr());
		}
	}
	
	@Test
	public void testGetSearchOpById() {
		SearchOperation op = model.getSearchOpById("504decd0833697bb0a657507");
		assertNotNull(op);
		
		assertNotEmpty(op.getId());
		assertNotEmpty(op.getTitle());
		assertNotEmpty(op.getDescr());
		assertTrue(op.getDate().getTime() > 0);
		assertNotEmpty(op.getLocation());
		assertTrue(op.getStatus().getId() >= 0);
	}
	
	@Test 
	public void testSubmitSearchOpApplication() {
		model.submitSearchOpApplication("504decd0833697bb0a657507", 
				"Tester Testerson", "123", "tester@mail.com");
	}
	
	@Test
	public void testSubmitSearchOpApplicationForNonExistentOp() {
		model.submitSearchOpApplication("504decd0833697bb0000dead", 
				"Tester Testerson", "123", "tester@mail.com");
		assertTrue(controller.errorThrown);
	}
	
	@Test
	public void testSubmitSearchOpApplicationWithNullSearcherData() {
		model.submitSearchOpApplication(null, 
				null, null, null);
		assertTrue(controller.errorThrown);
	}
	
	@Test
	public void testSubmitSearchOpApplicationWithEmptySearcherData() {
		model.submitSearchOpApplication("", 
				"", "", "");
		assertTrue(controller.errorThrown);
	}
	
	@Test 
	public void testGetSearchOpsByName() {
		model.getSearchOpsByName("Skallgång, norra skogen");
	}
	
	@Test 
	public void testGetSearchOpsByNullName() {
		model.getSearchOpsByName(null);
	}
	
	@Test 
	public void testGetSearchOpsByEmptyName() {
		model.getSearchOpsByName("");
	}
	
	@Test 
	public void testGetSearchOpsByNameWithNonExistentName() {
		SearchOperationWrapper[] list;
		list = model.getSearchOpsByName("nonexistentname");
		assertTrue(list.length == 0);
	}
	
	@Test 
	public void testGetSearchOpsByFilter() {
		SearchOperationWrapper[] list = model.getSearchOpsByFilter(
				"Skallgång, norra skogen", 
				"Linanäs", 
				1221724799999L, 
				1221724800001L); //TODO why doesnt this work?
		assertTrue(list.length >= 1);
		for (SearchOperationWrapper intro : list) {
			assertNotNull(intro);
			assertNotEmpty(intro.getId());
			assertNotEmpty(intro.getTitle());
			assertNotEmpty(intro.getDescr());
		}
	}
	
	@Test 
	public void testGetSearchOpsByFilterWithNullValues() {
		SearchOperationWrapper[] list = 
			model.getSearchOpsByFilter(null, null, 0L, 0L);
		assertTrue("WS did not return full list for empty query", 
				list.length > 0);
	}
	
	@Test 
	public void testGetAllSeachOpsTitles() {
		String[] titles = model.getAllSeachOpsTitles();
		assertNotNull(titles);
		assertFalse(titles.length == 0);
		
		for (int i = 0; i < titles.length; i++) {
			assertNotNull(titles[i]);
			assertFalse(titles[i].length() == 0);
		}
	}
	
	@Test 
	public void testGetAllSeachOpsLocations() {
		String[] locations = model.getAllSeachOpsLocations();
		assertNotNull(locations);
		assertFalse(locations.length == 0);
		
		for (int i = 0; i < locations.length; i++) {
			assertNotNull(locations[i]);
			assertFalse(locations[i].length() == 0);
		}
	}
	
	private static void assertNotEmpty(String string) {
		assertNotNull("String is null", string);
		assertTrue("String has zero or negative length", string.length() > 0);
	}
}
