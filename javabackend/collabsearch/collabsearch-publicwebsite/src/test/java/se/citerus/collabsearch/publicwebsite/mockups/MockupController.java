package se.citerus.collabsearch.publicwebsite.mockups;

import java.util.HashMap;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.publicwebsite.ControllerListener;

public class MockupController implements ControllerListener {
	
	public boolean errorThrown = false;

	@Override
	public void showErrorMessage(String errorHeader, String errorMessage) {
		System.err.println(errorHeader + ": " + errorMessage);
		errorThrown = true;
	}

	@Override
	public void showTrayNotification(String caption, String message) {
		System.out.println(caption + ": " + message);
	}

	@Override
	public SearchOperation fireReadMoreEvent(String header) {
		return null;
	}

	@Override
	public void submitSearchOpApplication(String selectedOp, String name,
			String tele, String email) {
	}

	@Override
	public SearchOperationWrapper[] getAllSearchOpsIntros() {
		return null;
	}

	@Override
	public SearchOperationWrapper[] getSearchOpsByName(String searchString) {
		return null;
	}

	@Override
	public SearchOperationWrapper[] getSearchOpsByFilter(String name,
			String location, long date, long endDate) {
		return null;
	}

	@Override
	public String[] getAllTitles() {
		return null;
	}

	@Override
	public String[] getAllLocations() {
		return null;
	}
	
	private class MockupError {
		private String methodName;
		private HashMap<String, String> inputMap = new HashMap<String, String>();
		
		public MockupError(String methodName, String... inputs) {
			this.methodName = methodName;
			
			String previous = null;
			for (int i = 0; i < inputs.length; i++) {
				
			}
		}
	}

}