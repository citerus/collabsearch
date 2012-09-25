package se.citerus.collabsearch.publicwebsite.logic;

import java.util.Date;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.StringArrayWrapper;
import se.citerus.collabsearch.publicwebsite.ControllerListener;

public class Model {
	private ControllerListener listener;
	private RestClient restClient;

	public Model(ControllerListener listener) {
		this.listener = listener;
		restClient = new RestClient();
	}

	public SearchOperation getSearchOpById(String searchOpId){
		try {
			return restClient.getSearchOperationById(searchOpId);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Fel vid hämtning av sökuppdraget");
		}
		return null;
	}

	public void submitSearchOpApplication(String selectedOp, String name,
			String tele, String email){
		try {
			restClient.applyForSearchOp(selectedOp, name, email, tele);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Anmälan till sökoperationen misslyckades");
		}
	}

	public SearchOperationWrapper[] getAllSearchOps(){
		try {
			return restClient.getAllOps();
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Sökoperationerna kunde ej hämtas från servern");
		}
		return null;
	}

	public SearchOperationWrapper[] getSearchOpsByName(String opName){
		try {
			return restClient.searchForOps(opName, null, null, null);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Sökoperationsdatat kunde ej hämtas från servern");
		}
		return null;
	}

	public SearchOperationWrapper[] getSearchOpsByFilter(String opName,
			String location, long startDate, long endDate){
		try {
			return restClient.searchForOps(opName, location, "" + startDate, "" + endDate);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Sökresultaten kunde ej hämtas från servern");
		}
		return null;
	}

	public String[] getAllSeachOpsTitles() {
		try {
			StringArrayWrapper[] allTitles = restClient.getAllTitles();
			String[] array = new String[allTitles.length];
			int i = 0;
			for (StringArrayWrapper stringArrayWrapper : allTitles) {
				array[i++] = stringArrayWrapper.getString();
			}
			return array;
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Sökoperationstitlarna kunde ej hämtas från servern");
		}
		return null;
	}

	public String[] getAllSeachOpsLocations() {
		try {
			StringArrayWrapper[] allTitles = restClient.getAllLocations();
			String[] array = new String[allTitles.length];
			int i = 0;
			for (StringArrayWrapper stringArrayWrapper : allTitles) {
				array[i++] = stringArrayWrapper.getString();
			}
			return array;
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Platsnamnen kunde ej hämtas från servern");
		}
		return null;
	}
}
