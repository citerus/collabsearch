package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;

@Service
public class SearchMissionService {

	@Autowired
	private SearchMissionDAO searchMissionDAO;

	public SearchMissionService() {
	}

	@PostConstruct
	public void init() {
	}

	public List<SearchMission> getListOfSearchMissions() throws Exception {
		return searchMissionDAO.getAllSearchMissions();
	}

	public void cleanUp() {
	}

	public void endMission(String missionId) throws Exception, SearchMissionNotFoundException {
		Validate.notEmpty(missionId);
		searchMissionDAO.endMission(missionId);
	}
	
	public List<Status> getListOfStatuses() throws Exception {
		List<Status> list = searchMissionDAO.getAllSearchMissionStatuses();
		Validate.notNull(list);
		Validate.notEmpty(list);
		return list;
	}

	public SearchMission getSearchMissionById(String missionId) throws Exception {
		Validate.notEmpty(missionId);
		SearchMission searchMission = searchMissionDAO.findMission(missionId);
		Validate.notNull(searchMission);
		return searchMission;
	}

	public String addOrModifyMission(String name, String description, int prio,
			Status status, String missionId) throws Exception {
		Validate.notEmpty(name);
		Validate.notEmpty(description);
		Validate.isTrue(prio >= 0);
		Validate.notNull(status);
		Validate.isTrue(status.getId() >= 0);
		Validate.notEmpty(status.getName());
		Validate.notEmpty(status.getDescr());
		SearchMission mission = new SearchMission(missionId, name, description, prio, status);
		return addOrModifyMission(mission, missionId);
	}
	
	public String addOrModifyMission(SearchMission mission, String missionId) throws Exception {
		Validate.notNull(mission);
		//TODO break into two methods
		if (missionId == null) {
			return searchMissionDAO.createSearchMission(mission);
		} else {
			Validate.notEmpty(missionId);
			searchMissionDAO.editSearchMission(mission, missionId);
			return null;
		}
	}
	
	public void addFileToMission(String missionId, FileMetadata metadata) throws Exception {
		Validate.notEmpty(missionId);
		Validate.notNull(metadata);
		String fileName = searchMissionDAO.addFileMetadata(missionId, metadata);
		Validate.notEmpty(fileName);
	}
	
	public void deleteFile(String filename, String missionId) throws Exception {		
		Validate.notEmpty(filename);
		Validate.notEmpty(missionId);
		FileMetadata metadata = searchMissionDAO.getFileMetadata(filename, missionId);
		Validate.notNull(metadata);
		Validate.notEmpty(metadata.getFilePath());
		try {
			File file = new File(metadata.getFilePath());
			if (file.exists()) {
				file.delete();
			} else {
				System.err.println("File not found: filename");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = searchMissionDAO.deleteFileMetadata(filename, missionId);
		Validate.notEmpty(fileName);
	}
	
	public Status getStatusByName(String statusName) throws Exception {
		Validate.notEmpty(statusName);
		Status status = searchMissionDAO.findMissionStatusByName(statusName);
		Validate.notNull(status);
		return status;
	}

	public void setDebugMode() {
		searchMissionDAO.setDebugDB("test");
	}

}
