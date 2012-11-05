package se.citerus.collabsearch.store.inmemory;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.stereotype.Repository;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.GroupNode;
import se.citerus.collabsearch.model.Rank;
import se.citerus.collabsearch.model.SearchFinding;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;

@Repository
public class SearchMissionDAOInMemory implements SearchMissionDAO, SearchOperationDAO {
	private static List<SearchMission> missionsList;
	private static List<Status> statusList;
	private List<Rank> ranksList;
	private List<Status> opStatusList;

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
		
		if (opStatusList == null) {
			opStatusList = new ArrayList<Status>();
			opStatusList.add(new Status(0, "Ej påbörjad", "beskrivning här"));
			opStatusList.add(new Status(1, "Sökning inledd", "beskrivning här"));
			opStatusList.add(new Status(2, "Sökning avslutad", "beskrivning här"));
		}
	}

	public List<SearchMission> getAllSearchMissions() throws IOException {
		return missionsList;
	}

	public void disconnect() throws Exception {
		//need not be implemented for in-memory storage
	}

	public void endMission(String missionId) throws IOException, SearchMissionNotFoundException {
		SearchMission mission = findMission(missionId);
		if (mission == null) {
			throw new SearchMissionNotFoundException(missionId);
		}
		
		final String endStatusName = "Avslutat uppdrag";
		mission.setStatus(findStatusByName(endStatusName));
	}

	private Status findStatusByName(String name) {
		for (Status status : statusList) {
			if (status.getName().equals(name)) {
				return status;
			}
		}
		return null;
	}

	public SearchMission findMission(String missionId) throws SearchMissionNotFoundException {
		for (SearchMission listedMission : missionsList) {
			if (missionId.equals(listedMission.getId())) {
				return listedMission;
			}
		}
		throw new SearchMissionNotFoundException(missionId);
	}

	public List<Status> getAllSearchMissionStatuses() throws IOException {
		return statusList;
	}

	private void addMockStatuses() {
		statusList.add(new Status(0, "Avslutat uppdrag", "Sökninguppdraget avslutat"));
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
		statusList.add(new Status(16, "Okänd status", "Okänd status/processfas"));
	}

	private void addMockMissions(Random r) {
		SearchMission sm1 = new SearchMission(
				"" + r.nextLong(),
				"Sökuppdrag: Åke, 62",
				"Skallgång efter 62-årige Åke som varit försvunnen i två veckor.",
				1, statusList.get(0));
		sm1.setFileList(addMockFiles(r));
		sm1.setOpsList(addMockOps(r));
		SearchMission sm2 = new SearchMission(
				"" + r.nextLong(),
				"Sökuppdrag: Ingemar Andersson, 83",
				"Ingemar Andersson som försvann onsdagen den 19 september 2012 från Hålsbo, Långbjörken, Skinnskatteberg. Ingemars bil hittades vid Hålsbo vid Sjön Långbjörken i Skinnskatteberg. Det fanns hinkar mm i bilen vilket tyder på att han åkt dit för att plocka bär.",
				5, statusList.get(1));
		sm2.setFileList(addMockFiles(r));
		sm2.setOpsList(addMockOps(r));
		SearchMission sm3 = new SearchMission(
				"" + r.nextLong(),
				"Sökuppdrag: Ingela, 52",
				"Ingela försvann 27/9 från Landskrona. Hon skulle till ett läkarbesök den 27/9 i Ängelholm, men var aldrig där.Hon har lämnat telefon och plånbok hemma och har nog inga kontanter på sig.",
				10, statusList.get(2));
		sm3.setFileList(addMockFiles(r));
		sm3.setOpsList(addMockOps(r));
		
		missionsList.add(sm1);
		missionsList.add(sm2);
		missionsList.add(sm3);
	}
	
	private List<FileMetadata> addMockFiles(Random r) {
		List<FileMetadata> list = new ArrayList<FileMetadata>();
		list.add(new FileMetadata("" + r.nextLong(), "efterlysning.pdf", "application/pdf", "/tmp/uploads/")); 
		list.add(new FileMetadata("" + r.nextLong(), "personbild.png", "image/png", "/tmp/uploads/"));
		return list;
	}
	
	private List<SearchOperation> addMockOps(Random r) {		
		List<SearchOperation> opsList = new ArrayList<SearchOperation>();
		
		List<SearcherInfo> searchers = new ArrayList<SearcherInfo>();
		searchers.add(new SearcherInfo("" + r.nextLong(), "Jessicka Kangasniemi", "pa@mail.se", "12345"));
		searchers.add(new SearcherInfo("" + r.nextLong(), "Jerri Kangasniemi", "pb@mail.se", "23456"));
		searchers.add(new SearcherInfo("" + r.nextLong(), "Peder Schillerstedt", "pc@mail.se", "34567"));
		
		List<SearchGroup> groups = new ArrayList<SearchGroup>();
		groups.add(new SearchGroup("" + r.nextLong(), "Sökgrupp Adam", addMockGroupTree(searchers)));
		groups.add(new SearchGroup("" + r.nextLong(), "Sökgrupp Bertil", addMockGroupTree(searchers)));
		groups.add(new SearchGroup("" + r.nextLong(), "Sökgrupp Carl", addMockGroupTree(searchers)));
		
		List<SearchZone> zones = new ArrayList<SearchZone>();
		Point2D.Double[] points = new Point2D.Double[]{
			new Point2D.Double(18.533248901367188,59.477609837736246),
			new Point2D.Double(18.547410964965820,59.475212233145640),
			new Point2D.Double(18.527841567993164,59.467800379411620),
			new Point2D.Double(18.525953292846680,59.474732691807280),
			new Point2D.Double(18.533248901367188,59.477609837736246)
		};
		SearchFinding[] findings = new SearchFinding[] {
				new SearchFinding(18.530502319335938, 59.472640068126864, "Fynd", "Fotspår"),
				new SearchFinding(18.536081314086914, 59.474514716221430, "Fynd", "Upphittat klädesplagg")
		};
		SearchZone zone = new SearchZone("Norra sökområdet", 3, points, findings);
		zone.setId("" + r.nextLong());
		zones.add(zone);
		zone = new SearchZone("Södra sökområdet", 2, points, findings);
		zone.setId("" + r.nextLong());
		zones.add(zone);
		zone = new SearchZone("Storsjön (sökområde för dykare)", 1, points, findings);
		zone.setId("" + r.nextLong());
		zones.add(zone);
		
		SearchOperation searchOp = new SearchOperation(
				"" + r.nextLong() ,"Skallgång, norra skogen", "Skallgång i skogens norra delar", 
				new Date(System.currentTimeMillis()), "Ljusterö", 
				new Status(0, "Ej påbörjad", "Operationen har ej påbörjats"));
		searchOp.setSearchers(searchers);
		searchOp.setGroups(groups);
		searchOp.setZones(zones);
		opsList.add(searchOp);
		
		searchOp = new SearchOperation(
				"" + r.nextLong(), "Skallgång, södra skogen", "Skallgång i skogens södra delar", 
				new Date(System.currentTimeMillis()+86400000L), "Ljusterö", 
				new Status(1, "Sökning inledd", "Sökning har inletts i de bestämda områdena"));
		searchOp.setSearchers(searchers);
		searchOp.setGroups(groups);
		searchOp.setZones(zones);
		opsList.add(searchOp);
		
		searchOp = new SearchOperation(
				"" + r.nextLong(), "Dykoperation, storsjön", "Genomsökning av sjön mha dykteam", 
				new Date(System.currentTimeMillis()+(2*86400000L)), "Ljusterö", 
				new Status(2, "Sökning avslutad", "Sökningen har avslutats"));
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
	
	public Status findMissionStatusByName(String statusName) throws IOException {
		for (Status status : statusList) {
			if (status.getName().equals(statusName)) {
				return status;
			}
		}
		throw new IOException("Status " + statusName + " ej funnen");
	}
	
	@Override
	public String createSearchMission(SearchMission mission) throws IOException {
		if (mission.getId() == null || mission.equals("")) {
			mission.setId("" + new Random().nextLong());
		}
		missionsList.add(mission);
		return mission.getId();
	}
	
	@Override
	public void editSearchMission(SearchMission mission, String missionId)
			throws IOException, SearchMissionNotFoundException {
		for (SearchMission listedMission : missionsList) {
			if (missionId.equals(listedMission.getId())) {
				listedMission.setName(mission.getName());
				listedMission.setDescription(mission.getDescription());
				listedMission.setPrio(mission.getPrio());
				listedMission.setStatus(mission.getStatus());
				return;
			}
		}
		throw new SearchMissionNotFoundException(missionId);
	}

	public List<SearchOperation> getAllSearchOpsForMission(String missionId) throws SearchMissionNotFoundException {
		SearchMission mission = findMission(missionId);
		return mission.getOpsList();
	}

	public SearchOperation findOperation(String opId) throws SearchOperationNotFoundException {
		for (SearchMission mission : missionsList) {
			List<SearchOperation> opsList = mission.getOpsList();
			for (SearchOperation operation : opsList) {
				if (operation.getId().equals(opId)) {
					return operation;
				}
			}
		}
		throw new SearchOperationNotFoundException(opId);
	}

	public void deleteSearchOperation(String opId) throws IOException, SearchOperationNotFoundException {
		for (SearchMission mission : missionsList) {
			List<SearchOperation> opsList = mission.getOpsList();
			for (SearchOperation op : opsList) {
				if (op.getId().equals(opId)) {
					opsList.remove(op);
					return;
				}
			}
		}
		throw new SearchOperationNotFoundException(opId);
	}

	public String addFileMetadata(String missionId, FileMetadata fileMetaData) throws IOException, SearchMissionNotFoundException {
		SearchMission mission = findMission(missionId);
		if (mission == null) {
			throw new SearchMissionNotFoundException(missionId);
		}
		
		mission.getFileList().add(fileMetaData);
		return fileMetaData.getId();
	}
	
	public String deleteFileMetadata(String filename, String missionId) throws IOException, FileNotFoundException, SearchMissionNotFoundException {
		SearchMission mission = findMission(missionId);
		
		List<FileMetadata> fileList = mission.getFileList();
		for (int i = 0; i < fileList.size(); i++) {
			if (fileList.get(i).getFileName().equals(filename)) {
				fileList.remove(i);
				return filename;
			}
		}
		throw new FileNotFoundException(filename);
	}

	@Override
	public FileMetadata getFileMetadata(String filename, String missionId) throws SearchMissionNotFoundException, FileNotFoundException {
		SearchMission mission = findMission(missionId);
		if (mission != null) {
			for (FileMetadata metadata : mission.getFileList()) {
				if (metadata.getFileName().equals(filename)) {
					return metadata;
				}
			}
		}
		throw new FileNotFoundException(filename);
	}

	@Override
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
	public String createSearchOperation(SearchOperation operation, String missionId) throws IOException {
		operation.setId("" + new Random().nextLong());
		for (SearchMission mission : missionsList) {
			if (mission.getId().equals(missionId)) {
				Status status = getSearchOpStatusById(operation.getStatus().getId());
				operation.setStatus(status);
				mission.getOpsList().add(operation);
				return operation.getId();
			}
		}
		return null;
	}

	@Override
	public SearchGroup getSearchGroup(String groupId) throws IOException {
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
	public Map<String, String> getUsersForSearchOp(String opId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				if (op.getId().equals(opId)) {
					List<SearcherInfo> list = op.getSearchers();
					Map<String, String> searcherMap = new HashMap<String, String>();
					for (SearcherInfo searcher : list) {
						searcherMap.put(searcher.getId(), searcher.getName());
					}
					return searcherMap;
				}
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public void addSearchGroup(SearchGroup group, String opId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				if (op.getId().equals(opId)) {
					op.getGroups().add(group);
					return;
				}
			}
		}
		throw new IOException("Failed to add SearchGroup");
	}

	@Override
	public void editSearchGroup(SearchGroup editedGroup, String opId) {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				if (op.getId().equals(opId)) {
					List<SearchGroup> groups = op.getGroups();
					for (int i = 0; i < groups.size(); i++) {
						SearchGroup oldGroup = groups.get(i);
						if (oldGroup.getId().equals(editedGroup.getId())) {
							groups.set(i, editedGroup);
						}
					}
				}
			}
		}
	}
	
	@Override
	public List<Status> getAllSearchOpStatuses() throws IOException {
		if (opStatusList == null) {
			throw new IOException("No statuses found");
		}
		return opStatusList;
	}

	@Override
	public Status getSearchOpStatus(String statusName) throws IOException {
		for (Status status : opStatusList) {
			if (status.getName().equals(statusName)) {
				return status;
			}
		}
		throw new IOException("Status " + statusName + " not found");
	}
	
	private Status getSearchOpStatusById(int statusId) throws IOException {
		for (Status status : opStatusList) {
			if (status.getId() == statusId) {
				return status;
			}
		}
		throw new IOException("Status " + statusId + " not found");
	}

	@Override
	public String endOperation(String opId) {
		return "Sökning avslutad";
	}

	@Override
	public void deleteZone(String zoneId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				List<SearchZone> zones = op.getZones();
				for (int i = 0; i < zones.size(); i++) {
					SearchZone existingZone = zones.get(i);
					if (existingZone.getId().equals(zoneId)) {
						zones.remove(i);
						return;
					}
				}
			}
		}
		throw new IOException("Zone not found");
	}

	@Override
	public void deleteGroup(String groupId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				List<SearchGroup> groups = op.getGroups();
				for (int i = 0; i < groups.size(); i++) {
					SearchGroup group = groups.get(i);
					if (group.getId().equals(groupId)) {
						groups.remove(i);
						return;
					}
				}
			}
		}
		throw new IOException("Group not found");
	}

	@Override
	public SearchOperationWrapper[] getAllSearchOps() throws IOException {
		Random r = new Random();
		SearchOperationWrapper[] array = new SearchOperationWrapper[3];
		array[0] = new SearchOperationWrapper("" + r.nextLong(), "Sökoperation 1", "text...");
		array[1] = new SearchOperationWrapper("" + r.nextLong(), "Sökoperation 2", "text...");
		array[2] = new SearchOperationWrapper("" + r.nextLong(), "Sökoperation 3", "text...");
		return array;
	}

	@Override
	public SearchOperation getSearchOpById(String opId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				if (op.getId().equals(opId)) {
					return op;
				}
			}
		}
		return null;
	}

	@Override
	public void assignUserToSearchOp(String opId, String name, String email,
			String tele) throws IOException {
		Random r = new Random();
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				if (op.getId().equals(opId)) {
					op.getSearchers().add(new SearcherInfo("" + r.nextInt(), name, email, tele));
					return;
				}
			}
		}
		throw new IOException("SearchOperation not found");
	}

	@Override
	public String[] getAllOpLocations() {
		ArrayList<String> list = new ArrayList<String>();
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				list.add(op.getLocation());
			}
		}
		String[] arr = new String[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}

	@Override
	public String[] getAllOpTitles() {
		ArrayList<String> list = new ArrayList<String>();
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				list.add(op.getTitle());
			}
		}
		String[] arr = new String[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}

	@Override
	public SearchOperationWrapper[] getSearchOpsByFilter(String title,
			String location, String startDate, String endDate)
			throws IOException {
		return null;
	}

	@Override
	public SearchZone getZoneById(String zoneId) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				for (SearchZone zone : op.getZones()) {
					if (zone.getId().equals(zoneId)) {
						return zone;
					}
				}
			}
		}
		throw new IOException("Zone not found");
	}

	@Override
	public void editZone(String zoneId, SearchZone zone) throws IOException {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				List<SearchZone> zones = op.getZones();
				for (int i = 0; i < zones.size(); i++) {
					SearchZone existingZone = zones.get(i);
					if (existingZone.getId().equals(zoneId)) {
						zones.set(i, zone);
						return;
					}
				}
			}
		}
		throw new IOException("Zone not found");
	}

	@Override
	public String createZone(String opId, SearchZone zone) {
		for (SearchMission mission : missionsList) {
			for (SearchOperation op : mission.getOpsList()) {
				if (op.getId().equals(opId)) {
					zone.setId("" + new Random().nextInt());
					op.getZones().add(zone);
					return zone.getId();
				}
			}
		}
		return null;
	}
}
