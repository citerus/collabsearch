package se.citerus.lookingfor.DAL;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import se.citerus.lookingfor.logic.FileMetadata;
import se.citerus.lookingfor.logic.SearchMission;
import se.citerus.lookingfor.logic.SearchOperation;
import se.citerus.lookingfor.logic.Status;

public class SearchMissionDALInMemory implements SearchMissionDAL {
	
	List<SearchMission> missionsList = new ArrayList<SearchMission>();
	List<Status> statusList = new ArrayList<Status>();
	
	public SearchMissionDALInMemory() {
		addMockStatuses();
		addMockMissions();
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
			throw new IOException("Sökuppdrag ej funnet");
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
		return null;
	}

	public List<Status> getAllStatuses() throws IOException {
		return statusList;
	}

	public SearchOperation findOperation(String name) {
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
		List<FileMetadata> fileList = Arrays.asList(
				new FileMetadata("fil1.pdf", "", ""), new FileMetadata("fil2.png", "", ""));
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
}
