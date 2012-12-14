package se.citerus.collabsearch.api;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.citerus.collabsearch.store.facades.SearchOperationDAO;


public class DbSmokeTest {

	private static final int NOT_FOUND = 404;
	private static final int INTERNAL_SERVER_ERROR = 500;

	private static SearchOperationDAO dao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AnnotationConfigApplicationContext context;
		context = new AnnotationConfigApplicationContext(
				"se.citerus.collabsearch.api",
				"se.citerus.collabsearch.store.mongodb");
		dao = (SearchOperationDAO) context.getBean("searchMissionDAOMongoDB");
		dao.setDebugDB("test");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testDBServiceStatus() throws IOException {
		boolean databaseStatus = dao.getDatabaseStatus();
		assert(databaseStatus == true);
	}

}
