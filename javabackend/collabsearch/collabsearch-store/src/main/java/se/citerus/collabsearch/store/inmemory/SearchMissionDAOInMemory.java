package se.citerus.collabsearch.store.inmemory;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.GroupNode;
import se.citerus.collabsearch.model.Rank;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.Zone;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;

public class SearchMissionDAOInMemory implements SearchMissionDAO {

	private static List<SearchMission> missionsList;
	private static List<Status> statusList;
	private List<Rank> ranksList;

	public SearchMissionDAOInMemory() {
		if (missionsList == null) {
			missionsList = new ArrayList<SearchMission>();
			statusList = new ArrayList<Status>();
			ranksList = new ArrayList<Rank>();
			
			Random r = new Random();
			addMockStatuses();
//			addMockRanks(r);
			addMockMissions(r);
		}
	}

	public List<SearchMission> getAllSearchMissions() throws IOException {
		return missionsList;
	}

	public void disconnect() {
		//nop
	}

	public String endMission(String missionId) throws IOException {
		SearchMission mission = findMission(missionId);
		if (mission == null) {
			throw new IOException("Sökuppdraget " + missionId + " ej funnet");
		}
		
		//to be replaced with db-stored status table/collection
		final String endStatusName = "Avslutat uppdrag";
		mission.setStatus(findStatusByName(endStatusName));
		
		return endStatusName;
	}

	private Status findStatusByName(String name) {
		for (Status status : statusList) {
			if (status.getName().equals(name)) {
				return status;
			}
		}
		return null;
	}

	public SearchMission findMission(String missionId) {
		for (SearchMission listedMission : missionsList) {
			if (missionId.equals(listedMission.getId())) {
				return listedMission;
			}
		}
		return null;
	}

	public List<Status> getAllStatuses() throws IOException {
		return statusList;
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

	private void addMockMissions(Random r) {
		SearchMission sm1 = new SearchMission("" + r.nextLong(), "Sökuppdrag 1", "text...", 1, statusList.get(0));
		sm1.setFileList(addMockFiles(r));
		sm1.setOpsList(addMockOps(r));
		SearchMission sm2 = new SearchMission("" + r.nextLong(), "Sökuppdrag 2", "text...", 5, statusList.get(1));
		sm2.setFileList(addMockFiles(r));
		sm2.setOpsList(addMockOps(r));
		SearchMission sm3 = new SearchMission("" + r.nextLong(), "Sökuppdrag 3", "text...", 10, statusList.get(2));
		sm3.setFileList(addMockFiles(r));
		sm3.setOpsList(addMockOps(r));
		
		missionsList.add(sm1);
		missionsList.add(sm2);
		missionsList.add(sm3);
	}
	
	private List<FileMetadata> addMockFiles(Random r) {
		List<FileMetadata> list = new ArrayList<FileMetadata>();
		list.add(new FileMetadata("" + r.nextLong(), "fil1.pdf", "application/pdf", "/tmp/uploads/")); 
		list.add(new FileMetadata("" + r.nextLong(), "fil2.png", "image/png", "/tmp/uploads/"));
		return list;
	}
	
	private List<SearchOperation> addMockOps(Random r) {		
		List<SearchOperation> opsList = new ArrayList<SearchOperation>();
		
		List<SearcherInfo> searchers = new ArrayList<SearcherInfo>();
		searchers.add(new SearcherInfo("" + r.nextLong(), "Person A", "pa@mail.se", "12345"));
		searchers.add(new SearcherInfo("" + r.nextLong(), "Person B", "pb@mail.se", "23456"));
		searchers.add(new SearcherInfo("" + r.nextLong(), "Person C", "pc@mail.se", "34567"));
		
		List<SearchGroup> groups = new ArrayList<SearchGroup>();
		groups.add(new SearchGroup("" + r.nextLong(), "Grupp A", addMockGroupTree(searchers)));
		groups.add(new SearchGroup("" + r.nextLong(), "Grupp B", addMockGroupTree(searchers)));
		groups.add(new SearchGroup("" + r.nextLong(), "Grupp C", addMockGroupTree(searchers)));
		
		List<Zone> zones = new ArrayList<Zone>();
		zones.add(new Zone("" + r.nextLong(), "Zon Alfa"));
		zones.add(new Zone("" + r.nextLong(), "Zon Beta"));
		zones.add(new Zone("" + r.nextLong(), "Zon Gamma"));
		
		SearchOperation searchOp = new SearchOperation(
				"" + r.nextLong() ,"Operation 1", "beskrivn...", 
				new Date(System.currentTimeMillis()), "Plats X", 
				new Status(0, "status 1", "beskrivn..."));
		searchOp.setSearchers(searchers);
		searchOp.setGroups(groups);
		searchOp.setZones(zones);
		opsList.add(searchOp);
		
		searchOp = new SearchOperation(
				"" + r.nextLong(), "Operation 2", "beskrivn...", 
				new Date(System.currentTimeMillis()+86400000L), "Plats Y", 
				new Status(1, "status 2", "beskrivn..."));
		searchOp.setSearchers(searchers);
		searchOp.setGroups(groups);
		searchOp.setZones(zones);
		opsList.add(searchOp);
		
		searchOp = new SearchOperation(
				"" + r.nextLong(), "Operation 3", "beskrivn...", 
				new Date(System.currentTimeMillis()+(2*86400000L)), "Plats Z", 
				new Status(2, "status 3", "beskrivn..."));
		searchOp.setSearchers(searchers);
		searchOp.setGroups(groups);
		searchOp.setZones(zones);
		opsList.add(searchOp);
		
		return opsList;
	}
	
	private GroupNode addMockGroupTree(List<SearcherInfo> list) {
		GroupNode root = new GroupNode(list.get(0).getId(), Rank.Title.OPERATIONAL_MANAGER, null);
		root.addChild(new GroupNode(list.get(1).getId(), Rank.Title.ADMIN_MANAGER, root));
		root.addChild(new GroupNode(list.get(2).getId(), Rank.Title.GROUP_MANAGER, root));
		
		return root;
	}
	
	private void addMockRanks(Random r) { //should be set in config file?
		ranksList.add(new Rank(0, "Operativ chef", new int[]{1,2,3}, Rank.ALLOW_CHILDREN, Rank.NO_PARENT));
		ranksList.add(new Rank(1, "Operativ chefsassistent", null, Rank.NO_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(2, "Administrativ chef", new int[]{3}, Rank.ALLOW_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(3, "Administrativ chefsassistent", null, Rank.NO_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(4, "Gruppchef", new int[]{5,6}, Rank.ALLOW_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(5, "Gruppchefsassistent", null, Rank.NO_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(6, "Gruppledare", new int[]{7}, Rank.ALLOW_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(7, "Patrulledare", new int[]{8}, Rank.ALLOW_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(8, "Patrulledarassistent", new int[]{9}, Rank.ALLOW_CHILDREN, Rank.ALLOW_PARENT));
		ranksList.add(new Rank(9, "Sökare", null, Rank.NO_CHILDREN, Rank.ALLOW_PARENT));
	}
	
	public Rank findRank(int rankValue) {
		for (Rank rank : ranksList) {
			if (rank.getRankId() == rankValue) {
				return rank;
			}
		}
		return null;
	}
	
	public Status findStatus(String statusName) throws IOException {
		for (Status status : statusList) {
			if (status.getName().equals(statusName)) {
				return status;
			}
		}
		throw new IOException("Status " + statusName + " ej funnen");
	}
	
	@Override
	public void addNewSearchMission(SearchMission mission) throws IOException {
		if (mission.getId() == null || mission.equals("")) {
			mission.setId("" + new Random().nextLong());
		}
		missionsList.add(mission);
	}
	
	@Override
	public void editExistingMission(SearchMission mission, String missionId)
			throws IOException {
		for (SearchMission listedMission : missionsList) {
			if (missionId.equals(listedMission.getId())) {
				listedMission.setName(mission.getName());
				listedMission.setDescription(mission.getDescription());
				listedMission.setPrio(mission.getPrio());
				listedMission.setStatus(mission.getStatus());
				return;
			}
		}
	}

	public List<SearchOperation> getAllSearchOpsForMission(String missionId) {
		SearchMission mission = findMission(missionId);
		return mission.getOpsList();
	}

	public SearchOperation findOperation(String opId) {
		for (SearchMission mission : missionsList) {
			List<SearchOperation> opsList = mission.getOpsList();
			for (SearchOperation operation : opsList) {
				if (operation.getId().equals(opId)) {
					return operation;
				}
			}
		}
		return null;
	}

	public void deleteSearchOperation(String opId) throws IOException {
		for (SearchMission mission : missionsList) {
			List<SearchOperation> opsList = mission.getOpsList();
			for (SearchOperation op : opsList) {
				if (op.getId().equals(opId)) {
					opsList.remove(op);
					return;
				}
			}
		}
	}

	public void addFileMetadata(String missionId, FileMetadata fileMetaData) throws IOException {
		SearchMission mission = findMission(missionId);
		if (mission == null) {
			throw new IOException("Sökuppdraget ej funnet");
		}
		
		mission.getFileList().add(fileMetaData);
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

	@Override
	public FileMetadata getFileMetadata(String filename, String missionId) {
		SearchMission mission = findMission(missionId);
		if (mission != null) {
			for (FileMetadata metadata : mission.getFileList()) {
				if (metadata.getFilename().equals(filename)) {
					return metadata;
				}
			}
		}
		return null;
	}

	public void editSearchOperation(SearchOperation operation, String opId) throws IOException {
		for (SearchMission mission : missionsList) {
			List<SearchOperation> opsList = mission.getOpsList();
			for (SearchOperation op : opsList) {
				if (op.getId().equals(opId)) {
					op.setTitle(operation.getTitle());
					op.setDescr(operation.getDescr());
					op.setDate(operation.getDate());
					op.setLocation(operation.getLocation());
					op.setStatus(operation.getStatus());
					return;
				}
			}
		}
	}
	
	@Override
	public void addSearchOperation(SearchOperation operation, String missionId) throws IOException {
		operation.setId("" + new Random().nextLong());
		for (SearchMission mission : missionsList) {
			if (mission.getId().equals(missionId)) {
				mission.getOpsList().add(operation);
			}
		}
	}

	@Override
	public SearchGroup getGroupById(String groupId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				for (SearchGroup group : op.getGroups()) {
					if (group.getId().equals(groupId)) {
						return group;
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<SearcherInfo> getUsersForSearchOp(String opId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				if (op.getId().equals(opId)) {
					return op.getSearchers();
				}
			}
		}
		return Collections.emptyList();
	}
}
