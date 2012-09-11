package se.citerus.collabsearch.publicwebsite;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.publicwebsite.logic.Model;

import com.vaadin.ui.Window;

public class OpsListViewTest {
	//TODO write more tests! :)

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
		SearchOperationIntro[] ops = model.getAllSearchOps();
		assertNotNull(ops);
		assertFalse(ops.length == 0);
		
		for (SearchOperationIntro intro : ops) {
			assertNotNull(intro);
			assertNotNull(intro.getId());
			assertNotNull(intro.getTitle());
			assertNotNull(intro.getDescr());
		}
	}
}
