package se.citerus.collabsearch.adminui.DAL;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

public class SearchMissionDALInMemory implements SearchMissionDAL {
	
	private static List<SearchMission> missionsList;
	private static List<Status> statusList;
	
	private static SearchMission newMission;
	
	public SearchMissionDALInMemory() {
		if (missionsList == null && statusList == null) {
			missionsList = new ArrayList<SearchMission>();
			statusList = new ArrayList<Status>();
			addMockStatuses();
			addMockMissions();
		}
	}

	public List<SearchMission> getAllSearchMissions() throws IOException {
		return missionsList;
	}

	public void disconnect() {
		//nop
	}

	public void endMission(String name) throws IOException {
		SearchMission mission = findMission(name);
		if (mission == null) {
			throw new IOException("Sökuppdraget " + name + " ej funnet");
		}
		
		mission.setStatus(findStatusByName("Avslutat"));
	}

	private Status findStatusByName(String name) {
		for (Status status : statusList) {
			if (status.getName().equals(name)) {
				return status;
			}
		}
		return null;
	}

	public SearchMission findMission(String name) {
		for (SearchMission listedMission : missionsList) {
			if (name.equals(listedMission.getName())) {
				return listedMission;
			}
		}
		return null;
	}

	public void addOrModifyMission(SearchMission mission) throws IOException {
		for (SearchMission listedMission : missionsList) {
			if (mission.getName().equals(listedMission)) {
				//edit the existing mission
				listedMission.setDescription(mission.getDescription());
				listedMission.setPrio(mission.getPrio());
				listedMission.setStatus(mission.getStatus());
				return;
			}
		}
		
		//else, add a new mission
		missionsList.add(mission);
	}

	public List<SearchOperation> getAllSearchOpsForMission(String missionName) {
		SearchMission mission = findMission(missionName);
		return mission.getOpsList();
	}

	public List<Status> getAllStatuses() throws IOException {
		return statusList;
	}

	public SearchOperation findOperation(String opName, String missionName) {
		for (SearchMission mission : missionsList) {
			if (mission.getName().equals(missionName)) {
				List<SearchOperation> opsList = mission.getOpsList();
				for (SearchOperation operation : opsList) {
					if (operation.getTitle().equals(opName)) {
						return operation;
					}
				}
			}
		}
		return null;
	}

	private void addMockStatuses() {
		statusList.add(new Status(1,"1.","Anmälan om försvinnande ankommer"));
		statusList.add(new Status(2,"2.","Anhörigkontakt tas per telefon av X för att kontrollera om uppgifterna är korrekta"));
		statusList.add(new Status(3,"3.A","OM NEJ - Anmälan avskrivs"));
		statusList.add(new Status(4,"3.B","OM JA - Kontakt tas med polis av X för att kontrollera om en polisanmälan är gjord"));
		statusList.add(new Status(5,"4.A","OM NEJ - Anmälan avskrivs"));
		statusList.add(new Status(6,"4.B","OM JA - Möte med anhöriga och rapportering till organisationen"));
		statusList.add(new Status(7,"5.","Analys av information. Finns tillräckligt med underlag för att påbörja sök?"));
		statusList.add(new Status(8,"6.A","OM NEJ - Avvakta tills mer information inkommer innan fortsättning av process sker"));
		statusList.add(new Status(9,"6.B","OM JA - Akut eftersök påbörjas"));
		statusList.add(new Status(10,"7.","Y informerar organisation genom hemsida och telefonkejda angående tid och plats. " +
				"Q informerar media. W tar fram material så som kartor, västar och patrullistor m.m"));
		statusList.add(new Status(11,"8.","Samling - Information, gruppindelning - Sök påbörjas - Gav eftersök resultat?"));
		statusList.add(new Status(12,"9.A","OM JA - Kontakta polisen. Sök avslutas"));
		statusList.add(new Status(13,"9.B","OM NEJ - Skall nytt sök göras?"));
		statusList.add(new Status(14,"10.A","OM NEJ - Anmälan avskrivs"));
		statusList.add(new Status(15,"10.B","OM JA - Gå till punkt 7"));
		statusList.add(new Status(16, "Avslutat uppdrag", "Sökninguppdraget avslutat"));
		statusList.add(new Status(17, "Okänd status", "Okänd status/processfas"));
	}

	private void addMockMissions() {
		List<FileMetadata> fileList = new ArrayList<FileMetadata>();
		fileList.add(new FileMetadata("fil1.pdf", "application/pdf", "/tmp/uploads/"));
		fileList.add(new FileMetadata("fil2.png", "image/png", "/tmp/uploads/"));
		
		List<SearchOperation> opsList = new ArrayList<SearchOperation>();
		addMockOps(opsList);
		
		SearchMission sm1 = new SearchMission("Sökuppdrag 1", "text...", 1, statusList.get(0));
		sm1.setFileList(fileList);
		sm1.setOpsList(opsList);
		SearchMission sm2 = new SearchMission("Sökuppdrag 2", "text...", 5, statusList.get(1));
		sm2.setFileList(fileList);
		sm2.setOpsList(opsList);
		SearchMission sm3 = new SearchMission("Sökuppdrag 3", "text...", 10, statusList.get(2));
		sm3.setFileList(fileList);
		sm3.setOpsList(opsList);
		
		missionsList.add(sm1);
		missionsList.add(sm2);
		missionsList.add(sm3);
	}
	
	private void addMockOps(List<SearchOperation> opsList) {
		opsList.add(new SearchOperation("Operation 1", "beskrivn...", 
				new Date(System.currentTimeMillis())));
		opsList.add(new SearchOperation("Operation 2", "beskrivn...", 
				new Date(System.currentTimeMillis()+86400000L)));
		opsList.add(new SearchOperation("Operation 3", "beskrivn...", 
				new Date(System.currentTimeMillis()+(2*86400000L))));
	}

	public void addFileMetadata(String missionName, FileMetadata fileMetaData) throws IOException {
//		SearchMission mission = findMission(missionName);
//		if (mission == null) {
//			throw new IOException("Sökuppdraget " + missionName + " ej funnet");
//		}
//		
//		mission.getFileList().add(fileMetaData);
		
		
	}

	public void deleteFileMetadata(String filename, String missionName) throws IOException {
		SearchMission mission = findMission(missionName);
		if (mission == null) {
			throw new IOException("Sökuppdraget " + missionName + " ej funnet");
		}
		
		List<FileMetadata> fileList = mission.getFileList();
		for (int i = 0; i < fileList.size(); i++) {
			if (fileList.get(i).getFilename().equals(filename)) {
				fileList.remove(i);
				break;
			}
		}
	}

	public void deleteSearchOperation(String searchOpName, String missionName) throws IOException {
		SearchMission mission = findMission(missionName);
		if (mission == null) {
			if (newMission != null && newMission.getName().equals(missionName)) {
				mission = newMission;
			} else {
				throw new IOException("Sökuppdraget " + missionName + " ej funnet");
			}
		}
		
		List<SearchOperation> opsList = mission.getOpsList();
		for (SearchOperation op : opsList) {
			if (op.getTitle().equals(searchOpName)) {
				opsList.remove(op);
				break;
			}
		}
	}

	public void clearNewMissionContainer() {
		newMission = null;
	}

	public void initNewMissionContainer() {
		newMission = new SearchMission();
	}

	public void addOrModifySearchOperation(SearchOperation operation, String missionName) throws IOException {
		SearchMission mission = findMission(missionName);
		if (mission == null) {
			if (newMission != null && newMission.getName().equals(missionName)) {
				mission = newMission;
			} else {
				throw new IOException("Sökuppdraget " + missionName + " ej funnet");
			}
		}
		
		List<SearchOperation> opsList = mission.getOpsList();
		for (int i = 0; i < opsList.size(); i++) {
			if (opsList.get(i).getTitle().equals(operation)) {
				opsList.set(i, operation);
				return;
			}
		}
		
		opsList.add(operation);
	}

	public Status findStatus(String statusName) throws IOException {
		for (Status status : statusList) {
			if (status.getName().equals(statusName)) {
				return status;
			}
		}
		throw new IOException("Status " + statusName + " ej funnen");
	}
}
