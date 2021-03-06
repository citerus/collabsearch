package se.citerus.collabsearch.adminui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.GroupNode;
import se.citerus.collabsearch.model.Rank.Title;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchGroupNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchZoneNotFoundException;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.inmemory.SearchMissionDAOInMemory;
import se.citerus.collabsearch.store.mongodb.SearchMissionDAOMongoDB;

public class SearchOperationTest {

	private static final Status DEFAULT_STATUS_ENDED = new Status(0, "Avslutat", "beskrivn");
	private static final Status STATUS_NOT_BEGUN = new Status(2, "Ej inledd", "beskrivn");
	
	private static SearchOperationService service;
	private static SearchMissionService missionService;

	private static SearchMission testMission;
	
	final int testZoomLvl = 7;
	final String testPrio = "1";
	final Double testCenter = new Double(0, 0);

	@BeforeClass
	public static void setUpBeforeClass() {
//		context = new AnnotationConfigApplicationContext(
//				"se.citerus.collabsearch.adminui",
//				"se.citerus.collabsearch.store");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				SearchOperationService.class,
				SearchMissionDAOInMemory.class,
//				SearchMissionDAOMongoDB.class,
				SearchMissionService.class);
		service = context.getBean(SearchOperationService.class);
		service.setDebugMode();
		
		try {
			testMission = new SearchMission("testMission1", "blab", 1, DEFAULT_STATUS_ENDED);
			missionService = context.getBean(SearchMissionService.class);
			String id = missionService.addOrModifyMission(testMission, null);
			testMission.setId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
	}
	
	@Test
	public void testCreateOperation() throws Exception {
		SearchOperation operation = new SearchOperation(null, 
				"testOp1", "blab", new Date(), "plats X", DEFAULT_STATUS_ENDED);
		String id = service.editSearchOp(operation , null, testMission.getId());
		assertNotNull(id);
	}
	
	@Test
	public void testFindOperation() throws Exception {
		SearchOperation operation = new SearchOperation(null, 
				"testOp2", "blab", new Date(), "plats X", DEFAULT_STATUS_ENDED);
		String id = service.editSearchOp(operation , null, testMission.getId());
		assertNotNull(id);
		
		SearchOperation op = service.getSearchOp(id);
		assertNotNull(op);
		assertTrue(op.getId().length() > 0);
		assertTrue(op.getTitle().length() > 0);
		assertTrue(op.getDescr().length() > 0);
		assertNotNull(op.getDate());
		assertTrue(op.getLocation().length() > 0);
		assertTrue(op.getStatus().getId() >= 0);
		assertTrue(op.getStatus().getName().length() > 0);
		assertTrue(op.getStatus().getDescr().length() > 0);
	}

	@Test
	public void testEditSearchOp() throws Exception {
		SearchOperation originalOp = new SearchOperation(null, 
				"testOp3", "blab", new Date(), "plats X", DEFAULT_STATUS_ENDED);
		String id = service.editSearchOp(originalOp, null, testMission.getId());
		originalOp = new SearchOperation(null, "testOp3v2", 
			"blab2", new Date(System.currentTimeMillis()+1000), 
				"plats Y", new Status(1));
		service.editSearchOp(originalOp, id, null);
		SearchOperation foundOp = service.getSearchOp(id);
		assertNotNull(foundOp);
		assertEquals(id, foundOp.getId());
		assertEquals(originalOp.getTitle(), foundOp.getTitle());
		assertEquals(originalOp.getDescr(), foundOp.getDescr());
		assertTrue(originalOp.getDate().equals(foundOp.getDate()));
		assertEquals(originalOp.getLocation(), foundOp.getLocation());
		assertEquals(originalOp.getStatus().getId(), foundOp.getStatus().getId());
	}
	
	@Test(expected=SearchOperationNotFoundException.class)
	public void testDeleteSearchOp() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp3.5", "blab",
			new Date(), "blab", DEFAULT_STATUS_ENDED);
		String id = service.editSearchOp(op, null, testMission.getId());
		assertNotNull(id);
		service.deleteSearchOp(id);
		service.getSearchOp(id);
	}
	
	@Test
	public void testGetAllSearchOpStatuses() throws Exception {
		List<Status> list = service.getAllSearchOpStatuses();
		assertTrue(list.size() > 0);
		for (Status status : list) {
			assertNotNull(status);
			assertTrue(status.getId() >= 0);
			assertTrue(status.getName().length() > 0);
			assertTrue(status.getDescr().length() > 0);
		}
	}
	
	@Test
	public void testGetSearchOpStatusByName() throws Exception {
		List<Status> list = service.getAllSearchOpStatuses();
		assertTrue(list.size() > 0);
		for (Status status : list) {
			Status status2 = service.getSearchOpStatusByName(status.getName());
			assertNotNull(status2);
			assertTrue(status.getId() == status2.getId());
			assertTrue(status.getName().equals(status2.getName()));
			assertTrue(status.getDescr().equals(status2.getDescr()));
		}
	}
	
	@Test
	public void testEndOperation() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp4", 
			"blab", new Date(), "plats X", new Status(1));
		int origStatusId = op.getStatus().getId();
		String opId = service.editSearchOp(op, null, testMission.getId());
		service.endOperation(opId);
		SearchOperation op2 = service.getSearchOp(opId);
		assertTrue(op2.getStatus().getId() == 0);
		assertTrue(origStatusId != op2.getStatus().getId());
	}
	
	@Test
	public void testCreateZone() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp5", 
			"blab", new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op , null, testMission.getId());
		Double[] points = new Double[3];
		points[0] = new Double(1.01, 2.02);
		points[1] = new Double(3.03, 4.04);
		points[2] = new Double(5.05, 6.06);
		String zoneId = service.createZone(opId, "zone1", testPrio, points, testZoomLvl, testCenter, null);
		assertNotNull(zoneId);
	}
	
	@Test
	public void testGetZone() throws Exception {
		final String testName = "zone1";
		
		SearchOperation op = new SearchOperation(null, "testOp6", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		SearchGroup group = new SearchGroup(null, "Grupp A", null);
		String groupId = service.addOrModifySearchGroup(group , null, opId);
		Double[] points = new Double[3];
		points[0] = new Double(1, 2);
		points[1] = new Double(3, 4);
		points[2] = new Double(5, 6);
		String zoneId = service.createZone(opId, testName, testPrio, 
				points, testZoomLvl, testCenter, groupId);
		SearchZone zone = service.getZone(zoneId);
		assertNotNull(zone);
		assertEquals(zoneId, zone.getId());
		assertEquals(testName, zone.getTitle());
		assertEquals(Integer.parseInt(testPrio), zone.getPriority());
		assertEquals(testZoomLvl, zone.getZoomLevel());
		Double[] coords = zone.getZoneCoords();
		assertEquals(points.length, coords.length);
		for (int i = 0; i < points.length; i++) {
			assertEquals(points[i].x, coords[i].x, 0d);
			assertEquals(points[i].y, coords[i].y, 0d);
		}
		assertEquals(groupId, zone.getGroupId());
	}
	
	@Test
	public void testEditZone() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp7", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		Double[] points = new Double[3];
		points[0] = new Double(1, 2);
		points[1] = new Double(3, 4);
		points[2] = new Double(5, 6);
		String zoneId = service.createZone(opId, "zone1", "1", points, testZoomLvl, testCenter, null);
		assertNotNull(zoneId);
		
		Double[] points2 = new Double[3];
		points2[0] = new Double(2, 3);
		points2[1] = new Double(4, 5);
		points2[2] = new Double(6, 7);
		service.editZone(zoneId, "zone1v2", "2", points2, 8, testCenter, null);
		
		SearchZone zone = service.getZone(zoneId);
		assertEquals(zoneId, zone.getId());
		assertEquals("zone1v2", zone.getTitle());
		assertEquals(2, zone.getPriority());
		assertEquals(8, zone.getZoomLevel());
		Double[] coords = zone.getZoneCoords();
		assertEquals(points2.length, coords.length);
		for (int j = 0; j < points2.length; j++) {
			assertEquals(points2[j].x, coords[j].x, 0d);
			assertEquals(points2[j].y, coords[j].y, 0d);
		}
	}
	
	@Test(expected=SearchZoneNotFoundException.class)
	public void testDeleteZone() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp8", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		Double[] points = new Double[3];
		points[0] = new Double(1, 2);
		points[1] = new Double(3, 4);
		points[2] = new Double(5, 6);
		String zoneId = service.createZone(opId, "zone1", "1", points, testZoomLvl, testCenter, null);
		assertNotNull(zoneId);
		service.deleteZone(zoneId);
		service.getZone(zoneId);
	}
	
	@Test
	public void testCreateSearchGroup() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp9", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		
		addSampleSearchers(opId);
		ArrayList<String> list = new ArrayList<String>();
		Set<Entry<String,String>> set = service.getSearchersByOp(opId).entrySet();
		for (Entry<String, String> entry : set) {
			list.add(entry.getKey());
		}
		GroupNode rootNode = new GroupNode(list.get(0), Title.OPERATIONAL_MANAGER, null);
		GroupNode childNode1 = new GroupNode(list.get(1), Title.ADMIN_MANAGER, rootNode);
		rootNode.addChild(childNode1);
		GroupNode childNode2 = new GroupNode(list.get(2), Title.ASSISTANT_AM, childNode1);
		childNode1.addChild(childNode2);
		
		SearchGroup group = new SearchGroup(null, "sökgrupp1", rootNode);
		String groupId = service.addOrModifySearchGroup(group, null, opId);
		assertNotNull(groupId);
	}
	
	@Test
	public void testGetSearchGroup() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp9", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		
		addSampleSearchers(opId);
		ArrayList<String> list = new ArrayList<String>();
		Set<Entry<String,String>> set = service.getSearchersByOp(opId).entrySet();
		for (Entry<String, String> entry : set) {
			list.add(entry.getKey());
		}
		GroupNode rootNode = new GroupNode(list.get(0), Title.OPERATIONAL_MANAGER, null);
		GroupNode childNode1 = new GroupNode(list.get(1), Title.ADMIN_MANAGER, rootNode);
		rootNode.addChild(childNode1);
		GroupNode childNode2 = new GroupNode(list.get(2), Title.ASSISTANT_AM, childNode1);
		childNode1.addChild(childNode2);
		
		SearchGroup group = new SearchGroup(null, "sökgrupp1", rootNode);
		String groupId = service.addOrModifySearchGroup(group, null, opId);
		assertNotNull(groupId);
		
		SearchGroup searchGroup = service.getSearchGroup(groupId);
		assertNotNull(searchGroup);
		assertEquals(group.getName(), searchGroup.getName());
		GroupNode treeRoot = searchGroup.getTreeRoot();
		assertEquals(rootNode.getSearcherId(), treeRoot.getSearcherId());

		//traverseTree(treeRoot);
		Queue<GroupNode> queue = new ArrayBlockingQueue<GroupNode>(5, true);
		queue.add(treeRoot);
		while (queue.peek() != null) {
			GroupNode node = queue.remove();
			if (node.getChildren().isEmpty() == false) {
				for (GroupNode groupNode : node.getChildren()) {
					queue.add(groupNode);
				}
			}
		}
	}
	
	@Test(expected=SearchGroupNotFoundException.class)
	public void testDeleteGroup() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp10", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		SearchGroup group = new SearchGroup(null, "sökgrupp1", null);
		String groupId = service.addOrModifySearchGroup(group, null, opId);
		service.deleteGroup(groupId);
		service.getSearchGroup(groupId);
	}
	
	@Test
	public void testEditSearchGroup() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp11", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		
		addSampleSearchers(opId);
		ArrayList<String> list = new ArrayList<String>();
		Set<Entry<String,String>> set = service.getSearchersByOp(opId).entrySet();
		for (Entry<String, String> entry : set) {
			list.add(entry.getKey());
		}
		GroupNode rootNode = new GroupNode(list.get(0), Title.OPERATIONAL_MANAGER, null);
		GroupNode childNode1 = new GroupNode(list.get(1), Title.ADMIN_MANAGER, rootNode);
		rootNode.addChild(childNode1);
		GroupNode childNode2 = new GroupNode(list.get(2), Title.ASSISTANT_AM, childNode1);
		childNode1.addChild(childNode2);
		
		SearchGroup group = new SearchGroup(null, "sökgrupp1", rootNode);
		String groupId = service.addOrModifySearchGroup(group, null, opId);
		assertNotNull(groupId);
		
		GroupNode treeRoot = rootNode;
		treeRoot.getChildren().clear();
		treeRoot.setRank(Title.GROUP_LEADER);
		SearchGroup editedGroup = service.getSearchGroup(groupId);
		editedGroup.setName("sökgrupp1v2");
		editedGroup.setTreeRoot(treeRoot);
		service.addOrModifySearchGroup(editedGroup, groupId, opId);
		
		SearchGroup foundGroup = service.getSearchGroup(groupId);
		assertEquals(groupId, foundGroup.getId());
		assertEquals("sökgrupp1v2", foundGroup.getName());
		GroupNode node = editedGroup.getTreeRoot();
		assertEquals(node.getSearcherId(), list.get(0));
		assertEquals(node.getRank(), Title.GROUP_LEADER);
		assertTrue(node.getChildren().isEmpty());
	}
	
	@Test
	public void testGetSearchersByOp() throws Exception {
		SearchOperation op = new SearchOperation(null, "testOp12", "blab",
				new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String opId = service.editSearchOp(op, null, testMission.getId());
		
		addSampleSearchers(opId);
		
		Map<String, String> searchersByOp = service.getSearchersByOp(opId);
		assertTrue(searchersByOp.size() == 3);
		Set<Entry<String,String>> set = searchersByOp.entrySet();
		for (Entry<String, String> entry : set) {
			assertTrue(entry.getKey().length() > 0);
			assertTrue(entry.getValue().length() > 0);
		}
	}
	
	@Test
	public void testGetAllSearchOps() throws Exception {
		List<SearchOperation> list = service.getAllSearchOps();
		assertNotNull(list);
		assertTrue(!list.isEmpty());
	}
	
	@Test
	public void testGetAllSearchOpsInShortForm() throws Exception {
		String missionId1 = missionService.addOrModifyMission("testMission2", "testing", 1, STATUS_NOT_BEGUN, null);
		String missionId2 = missionService.addOrModifyMission("testMission3", "testing", 2, DEFAULT_STATUS_ENDED, null);
		SearchOperation op1 = new SearchOperation(null, "testop13", "blab", new Date(), "plats x", STATUS_NOT_BEGUN);
		String op1id = service.editSearchOp(op1 , null, missionId1);
		SearchOperation op2 = new SearchOperation(null, "testop14", "blab", new Date(), "plats x", DEFAULT_STATUS_ENDED);
		String op2id = service.editSearchOp(op2 , null, missionId2);
		
		SearchOperationWrapper[] array = service.getAllSearchOpsInShortForm();
		boolean foundMatchingOp = false;
		for (SearchOperationWrapper op : array) {
			if (op.getId().equals(op1id)) {
				foundMatchingOp = true;
			}
			Assert.assertFalse("Operation from ended mission found", op.getId().equals(op2id));
		}
		Assert.assertTrue(foundMatchingOp);
	}

	@Test
	public void testGetFilteredSearchOpsInShortForm() throws Exception {
		String mission2id = missionService.addOrModifyMission("testMission4", "testing", 1, STATUS_NOT_BEGUN, null);
		String mission3id = missionService.addOrModifyMission("testMission5", "testing", 2, DEFAULT_STATUS_ENDED, null);
		SearchOperation op1 = new SearchOperation(null, "testop15", "blab", new Date(), "plats x", STATUS_NOT_BEGUN);
		String op1id = service.editSearchOp(op1 , null, mission2id);
		SearchOperation op2 = new SearchOperation(null, "testop16", "blab", new Date(), "plats y", STATUS_NOT_BEGUN);
		String op2id = service.editSearchOp(op2 , null, mission2id);
		SearchOperation op3 = new SearchOperation(null, "testop17", "blab", new Date(), "plats x", STATUS_NOT_BEGUN);
		String op3id = service.editSearchOp(op3 , null, mission3id);
		
		SearchOperationWrapper[] array = service.getFilteredSearchOpsInShortForm(null, "plats x", null, null);
		boolean foundMatchingOp = false;
		for (SearchOperationWrapper op : array) {
			if (op.getId().equals(op1id)) {
				foundMatchingOp = true;
			}
			Assert.assertFalse("Operation matching filter found", op.getId().equals(op2id));
			Assert.assertFalse("Operation from ended mission found", op.getId().equals(op3id));
		}
		Assert.assertTrue(foundMatchingOp);
	}
	
	private void traverseTree(GroupNode node) throws Exception {
		assertNotNull(node);
		List<GroupNode> children = node.getChildren();
		for (GroupNode child : children) {
			traverseTree(child);
		}
	}
	
	private void addSampleSearchers(String opId)
			throws SearchOperationNotFoundException, IOException {
		service.assignSearcherToSearchOp(opId, "Person A", "pa@mail.se", "12319052871");
		service.assignSearcherToSearchOp(opId, "Person B", "pb@mail.se", "12319052872");
		service.assignSearcherToSearchOp(opId, "Person C", "pc@mail.se", "12319052873");
	}
}
