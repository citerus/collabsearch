package se.citerus.collabsearch.publicwebsite.logic;

import java.util.Date;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.publicwebsite.ControllerListener;

public class Model {
	private ControllerListener listener;
	private RestClient restClient;

	public Model(ControllerListener listener) {
		this.listener = listener;
		restClient = new RestClient();
	}

	public SearchOperation getSearchOpById(String searchOpId){
		SearchOperation op = null;
		try {
			op = restClient.getSearchOperationById(searchOpId);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Fel vid hämtning av sökuppdraget");
		}
		return op;
	}

	public void submitSearchOpApplication(String selectedOp, String name,
			String tele, String email){
		try {
			restClient.applyForSearchOp(selectedOp, name, email, tele);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Anmälan till sökoperationen misslyckades");
		}
	}

	public SearchOperationIntro[] getAllSearchOps(){
		try {
			return restClient.getAllOps();
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Sökoperationerna kunde inte hämtas från servern");
		}
		return null;
	}

	public SearchOperationIntro[] getSearchOpsByName(String opName){
		try {
			return restClient.searchForOps(opName, null, null);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Fel uppstod vid kontakt med servern");
		}
		return null;
	}

	public SearchOperationIntro[] getSearchOpsByFilter(String opName,
			String location, long date){
		try {
			return restClient.searchForOps(opName, location, "" + date);
		} catch (Exception e) {
			listener.showErrorMessage("Fel", "Fel uppstod vid kontakt med servern");
		}
		return null;
	}
}
