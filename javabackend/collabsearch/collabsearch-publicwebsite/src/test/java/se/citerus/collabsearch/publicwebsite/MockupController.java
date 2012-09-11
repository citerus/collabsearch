package se.citerus.collabsearch.publicwebsite;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;

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
		public SearchOperationIntro[] getAllSearchOpsIntros() {
			return null;
		}
		
		@Override
		public SearchOperationIntro[] getSearchOpsByName(String searchString) {
			return null;
		}
		
		@Override
		public SearchOperationIntro[] getSearchOpsByFilter(String name,
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