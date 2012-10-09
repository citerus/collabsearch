package se.citerus.collabsearch.publicwebsite.mockups;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.publicwebsite.ControllerListener;

public class MockupController implements ControllerListener {
		
		@Override
		public void showErrorMessage(String errorHeader, String errorMessage) {
			System.err.println(errorHeader + ": " + errorMessage);
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
		
	}