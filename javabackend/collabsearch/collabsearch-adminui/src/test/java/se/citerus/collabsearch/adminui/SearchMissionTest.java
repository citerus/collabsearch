package se.citerus.collabsearch.adminui;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.inmemory.SearchMissionDAOInMemory;

public class SearchMissionTest {
	
	private static SearchMissionService service;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ApplicationContext context;
//		context = new AnnotationConfigApplicationContext(
//				"se.citerus.collabsearch.adminui",
//				"se.citerus.collabsearch.store");
		context = new AnnotationConfigApplicationContext(SearchMissionService.class, SearchMissionDAOInMemory.class);
		service = context.getBean(SearchMissionService.class);
		service.setDebugMode();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Test
	public void testCreateNewSearchMission() throws Exception {
		SearchMission mission = new SearchMission("testMission1", 
				"blab", 1, new Status(0));
		String id = service.addOrModifyMission(mission, null);
		assertFalse(id.isEmpty());
	}
	
	@Test
	public void testGetSearchMission() throws Exception {
		SearchMission mission = new SearchMission("testMission1.5", 
				"blab", 1, new Status(0));
		String id = service.addOrModifyMission(mission, null);
		assertFalse(id.length() == 0);
		SearchMission foundMission = service.getSearchMissionById(id);
		assertNotNull(foundMission);
		assertFalse(foundMission.getId().length() == 0);
		assertFalse(foundMission.getName().length() == 0);
		assertFalse(foundMission.getDescription().length() == 0);
		assertTrue(foundMission.getPrio() >= 0);
		assertTrue(foundMission.getStatus().getId() >= 0);
	}
	
	@Test(expected=Exception.class)
	public void testGetNonExistentSearchMission() throws Exception {
		service.getSearchMissionById("0");
	}
	
	@Test(expected=Exception.class)
	public void testCreateNewNullSearchMission() throws Exception {
		SearchMission mission = null;
		String createdId = service.addOrModifyMission(mission, null);
	}
	
	@Test
	public void testEditSearchMission() throws Exception {
		SearchMission mission = new SearchMission(
				"testMission2", "blab", 1, new Status(0));
		final String createdId = service.addOrModifyMission(mission, null);
		assertNotNull(createdId);
		assertFalse(createdId.isEmpty());
		
		SearchMission oldMission = service.getSearchMissionById(createdId);
		assertNotNull(oldMission);
		
		SearchMission editedMission = new SearchMission(
				oldMission.getName().concat("v2"), 
				oldMission.getDescription().concat("v2"), 
				oldMission.getPrio() + 1, 
				new Status(oldMission.getStatus().getId() + 1));
		service.addOrModifyMission(editedMission, createdId);
		
		SearchMission newMission = service.getSearchMissionById(createdId);
		assertNotNull(newMission);
		assertEquals(editedMission.getName(),(newMission.getName()));
		assertEquals(editedMission.getDescription(),(newMission.getDescription()));
		assertEquals(editedMission.getPrio(),newMission.getPrio());
		assertEquals(editedMission.getStatus().getId(),newMission.getStatus().getId());
	}
	
	@Test(expected=Exception.class)
	public void testEditNullSearchMission() throws Exception {
		SearchMission mission = new SearchMission(
				"testMission3", "blab", 1, new Status(0));
		final String createdId = service.addOrModifyMission(mission, null);
		service.addOrModifyMission(null, createdId);
	}
	
	@Test(expected=Exception.class)
	public void testEditNonExistentSearchMission() throws Exception {
		SearchMission mission = new SearchMission(
				"testMission3.5", "blab", 1, new Status(0));
		String createdId = service.addOrModifyMission(mission, null);
		assertNotNull(createdId);
		service.addOrModifyMission(mission, "0");
	}

	@Test
	public void testEndSearchMission() throws Exception {
		SearchMission mission = new SearchMission("testMission4", "blab", 1, new Status(1));
		String id = service.addOrModifyMission(mission, null);
		assertFalse(id.isEmpty());
		
		int originalStatusId = mission.getStatus().getId();
		service.endMission(id);
		
		SearchMission changedMission = service.getSearchMissionById(id);
		assertTrue(changedMission.getStatus().getId() == 0);
		assertTrue(originalStatusId != changedMission.getStatus().getId());
	}
	
	@Test(expected=Exception.class)
	public void testEndNonExistentSearchMission() throws Exception {
		service.endMission("0");
	}
	
	@Test
	public void testGetListOfSearchMissions() throws Exception {
		List<SearchMission> list = service.getListOfSearchMissions();
		assertNotNull(list);
		
		if (!list.isEmpty()) {
			for (SearchMission mission : list) {
				assertFalse(mission.getId().isEmpty());
				assertFalse(mission.getName().isEmpty());
				assertFalse(mission.getDescription().isEmpty());
				assertFalse(mission.getPrio() < 0);
				assertFalse(mission.getStatus().getId() < 0);
			}
		}
	}
	
	@Test
	public void testGetListOfStatuses() throws Exception {
		List<Status> list = service.getListOfStatuses();
		assertFalse(list.isEmpty());
		
		for (Status status : list) {
			assertNotNull(status);
			assertFalse(status.getId() < 0);
			assertFalse(status.getName().isEmpty());
			assertFalse(status.getDescr().isEmpty());
		}
	}
	
	@Test
	public void testGetStatusByName() throws Exception {
		List<Status> list = service.getListOfStatuses();
		assertFalse(list.isEmpty());
		
		for (Status status : list) {
			Status status2 = service.getStatusByName(status.getName());
			assertNotNull(status2);
			assertEquals(status.getId(),status2.getId());
			assertEquals(status.getName(),status2.getName());
			assertEquals(status.getDescr(),status2.getDescr());
		}
	}
	
	@Test(expected=Exception.class)
	public void testGetStatusByNullName() throws Exception {
		service.getStatusByName(null);
	}
	
	@Test(expected=Exception.class)
	public void testGetStatusByNonExistentName() throws Exception {
		service.getStatusByName("0");
	}
	
	@Test
	public void testAddFile() throws Exception {
		SearchMission mission = new SearchMission("testMission5", "blab", 
				1, new Status(1));
		String missionId = service.addOrModifyMission(mission, null);
		FileMetadata metadata = new FileMetadata("testfil.pdf", 
				"application/pdf", "/dev/null");
		service.addFileToMission(missionId, metadata);
		
		SearchMission mission2 = service.getSearchMissionById(missionId);
		List<FileMetadata> fileList = mission2.getFileList();
		assertFalse(fileList.isEmpty());
		FileMetadata metadata2 = fileList.get(0);
		assertEquals(metadata.getFileName(), metadata2.getFileName());
		assertEquals(metadata.getMimeType(), metadata2.getMimeType());
		assertEquals(metadata.getFilePath(), metadata2.getFilePath());
	}
	
	@Test(expected=Exception.class)
	public void testAddNullFile() throws Exception {
		service.addFileToMission("123", null);
	}
	
	@Test(expected=Exception.class)
	public void testAddEmptyFile() throws Exception {
		FileMetadata metadata = new FileMetadata("", "", "", "");
		service.addFileToMission("123", metadata);
	}
	
	@Test(expected=Exception.class)
	public void testAddFileToNonExistentMission() throws Exception {
		FileMetadata metadata = new FileMetadata("testfil.pdf", "application/pdf", "/dev/null");
		service.addFileToMission("0", metadata);
	}
	
	@Test
	public void testDeleteFile() throws Exception {
		SearchMission mission = new SearchMission("testMission6", 
				"blab", 1, new Status(1));
		String missionId = service.addOrModifyMission(mission, null);
		FileMetadata metadata = new FileMetadata("testfil.pdf", 
				"application/pdf", "/dev/null");
		service.addFileToMission(missionId, metadata);
		
		service.deleteFile(metadata.getFileName(), missionId);
		
		SearchMission mission2 = service.getSearchMissionById(missionId);
		assertTrue(mission2.getFileList().size() == 0);
	}
	
	@Test(expected=Exception.class)
	public void testDeleteNullFile() throws Exception {
		service.deleteFile(null, "123");
	}
	
	@Test(expected=Exception.class)
	public void testDeleteFileFromNonExistentMission() throws Exception {
		service.deleteFile("testfil.pdf", "0");
	}
	
}
