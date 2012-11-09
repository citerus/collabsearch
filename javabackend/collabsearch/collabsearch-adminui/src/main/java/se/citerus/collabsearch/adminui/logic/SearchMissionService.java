package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;

@Service
public class SearchMissionService {

	@Autowired
	@Qualifier("searchMissionDAOMongoDB")
	private SearchMissionDAO searchMissionDAO;

	public SearchMissionService() {
	}

	@PostConstruct
	public void init() {
		if (searchMissionDAO == null) {
			try {
				Properties prop = new Properties();
				ApplicationContext context = 
					new AnnotationConfigApplicationContext("se.citerus.collabsearch.store");
				InputStream stream = SearchMissionService.class.getResourceAsStream(
					"/server-config.properties");
				String dbImpl = "searchMissionDAOInMemory";
				if (stream != null) {
					prop.load(stream);
					dbImpl = prop.getProperty("DBIMPL");
				}
				System.out.println("Configured database implementation: " + dbImpl);
				searchMissionDAO = context.getBean(dbImpl, SearchMissionDAO.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<SearchMission> getListOfSearchMissions() throws Exception {
		return searchMissionDAO.getAllSearchMissions();
	}

	public void cleanUp() {
		try {
			searchMissionDAO.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public String addOrModifyMission(SearchMission mission, String missionId) throws Exception {
		Validate.notNull(mission);
		//TODO break into two methods
		if (missionId == null) {
			//TODO create mission obj here instead
			return searchMissionDAO.createSearchMission(mission);
		} else {
			//TODO create mission obj here instead
			Validate.notEmpty(missionId);
			searchMissionDAO.editSearchMission(mission, missionId);
			return null;
		}
	}
	
	public void addFileToMission(String missionId, FileMetadata metadata) throws Exception {
		//TODO create filemetadata here instead
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
			File file = new File(metadata.getFilePath() + File.pathSeparator + metadata.getFileName());
			if (file.exists()) {
				//TODO log file not found on deletion
				file.delete();
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

}
