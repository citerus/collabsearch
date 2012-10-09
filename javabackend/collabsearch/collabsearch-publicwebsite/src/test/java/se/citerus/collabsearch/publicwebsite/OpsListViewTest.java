package se.citerus.collabsearch.publicwebsite;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.publicwebsite.logic.Model;
import se.citerus.collabsearch.publicwebsite.mockups.MockupController;

public class OpsListViewTest {
	//TODO write more tests

	private static Model model;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockupController listener = new MockupController();
		model = new Model(listener);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Test
	public void testGetAllOps() {
		SearchOperationWrapper[] ops = model.getAllSearchOps();
		assertNotNull(ops);
		assertFalse(ops.length == 0);
		
		for (SearchOperationWrapper intro : ops) {
			assertNotNull(intro);
			assertNotNull(intro.getId());
			assertNotNull(intro.getTitle());
			assertNotNull(intro.getDescr());
		}
	}
}
