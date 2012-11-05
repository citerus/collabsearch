package se.citerus.collabsearch.adminui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

public class SearchOperationTests {

	private static SearchOperationService opsService;
	private static Queue<String> opsToRemove;
	private static SearchMission testMission;

	@BeforeClass
	public static void setUpBeforeClass() {
		opsService = new SearchOperationService();
		opsToRemove = new PriorityQueue<String>(); 
	
		try {
			testMission = new SearchMission("testMission1", "blab", 1, new Status(0));
			new SearchMissionService().addOrModifyMission(testMission , null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		//remove test data
		while (opsToRemove.peek() != null) {
			try {
				String id = opsToRemove.remove();
				opsService.deleteSearchOperation(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testCreateOperation() throws Exception {
		SearchOperation operation = new SearchOperation(null, 
				"testOp1", "blab", new Date(), "plats X", new Status(0));
		String id = opsService.editSearchOp(operation , null, testMission.getId());
		assertNotNull(id);
		
		opsToRemove.add(id);
	}
	
	@Test
	public void testFindOperation() throws Exception {
		SearchOperation operation = new SearchOperation(null, 
				"testOp2", "blab", new Date(), "plats X", new Status(0));
		String id = opsService.editSearchOp(operation , null, testMission.getId());
		Assert.assertNotNull(id);
		
		SearchOperation op = opsService.getSearchOp(id);
		assertNotNull(op);
		assertTrue(op.getId().length() > 0);
		assertTrue(op.getTitle().length() > 0);
		assertTrue(op.getDescr().length() > 0);
		assertNotNull(op.getDate());
		assertTrue(op.getLocation().length() > 0);
		assertTrue(op.getStatus().getId() >= 0);
		assertTrue(op.getStatus().getName().length() > 0);
		assertTrue(op.getStatus().getDescr().length() > 0);
		
		opsToRemove.add(id);
	}

}
