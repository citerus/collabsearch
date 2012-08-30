package se.citerus.collabsearch.publicwebsite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.Status;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RestClientTest {

	private static Client client;
	
	/** Contains the URL: http://localhost:8080/collabsearch-api/rest/ws */
	private static WebResource basicPath;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ClientConfig config = new DefaultClientConfig();
		client = Client.create(config);
		WebResource service = client.resource(
			UriBuilder.fromUri("http://localhost:8080/collabsearch-api").build());
		basicPath = service.path("rest").path("ws");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.destroy();
	}

	@Test
	public void testEcho() {
		//send a GET to /collabsearch-api/rest/ws/echo/world with Accept:text/plain
		String echoAnswer = null;
		try {
			echoAnswer = basicPath.path("echo").path("world").accept(MediaType.TEXT_PLAIN).get(String.class);
		} catch (UniformInterfaceException e) {
			fail(e.getMessage());
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
		assertNotNull(echoAnswer);
		assertTrue(echoAnswer.equals("Hello world\n"));
	}
	
	@Test
	public void testGetAllOps() {
		SearchOperationIntro[] intros1 = null;
		try {
			intros1 = basicPath.path("getAllOps").accept(MediaType.APPLICATION_XML).get(SearchOperationIntro[].class);
			assertNotNull(intros1);
			for (SearchOperationIntro op : intros1) {
				System.out.println("(XML)  " + op.toString());
			}
		} catch (UniformInterfaceException e) {
			fail(e.getMessage());
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
		
		SearchOperationIntro[] intros2 = null;
		try {
			intros2 = basicPath.path("getAllOps").accept(MediaType.APPLICATION_JSON).get(SearchOperationIntro[].class);
			assertNotNull(intros2);
			for (SearchOperationIntro op : intros2) {
				System.out.println("(JSON) " + op.toString());
			}
		} catch (UniformInterfaceException e) {
			fail(e.getMessage());
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
		
		assertTrue(intros1.length == intros2.length);
		for (int i = 0; i < intros1.length; i++) {
			assertTrue(intros1[i].getTitle().equals(intros2[i].getTitle()));
			assertTrue(intros1[i].getDescr().equals(intros2[i].getDescr()));
		}
	}

	@Test
	public void testGetSearchOpByName() {
		//get and unmarshal search op XML representation
		SearchOperation op = null;
		try {
			op = basicPath
					.path("getSearchOp")
					.path("testOp")
					.accept(MediaType.APPLICATION_XML)
					.get(SearchOperation.class);
			assertNotNull(op);
		} catch (UniformInterfaceException e) {
			fail(e.getMessage());
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
		
		assertTrue("SearchOp title did not match input", op.getTitle().equals("testOp"));
		assertNotNull(op.getDescr());
		assertNotNull(op.getDate());
		assertNotNull(op.getLocation());
		
		Status status = op.getStatus();
		assertNotNull(status);
		assertNotNull(status.getId());
		assertNotNull(status.getName());
		assertNotNull(status.getDescr());
		
		//get and unmarshal search op JSON representation
		SearchOperation op2 = null;
		try {
			op2 = basicPath
					.path("getSearchOp")
					.path("testOp")
					.accept(MediaType.APPLICATION_JSON)
					.get(SearchOperation.class);
			assertNotNull(op2);
		} catch (UniformInterfaceException e) {
			fail(e.getMessage());
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
		
		assertTrue("SearchOp title did not match input", op2.getTitle().equals("testOp"));
		assertNotNull(op2.getDescr());
		assertNotNull(op2.getDate());
		assertNotNull(op2.getLocation());
		
		Status status2 = op2.getStatus();
		assertNotNull(status);
		assertNotNull(status.getId());
		assertNotNull(status.getName());
		assertNotNull(status.getDescr());
		
		//compare XML object with JSON object
		assertEquals(op.getTitle(), op2.getTitle());
		assertEquals(op.getDescr(), op2.getDescr());
		assertEquals(op.getDate().getTime()/1000, op2.getDate().getTime()/1000); //op2's date was a created few milliseconds after op
		assertEquals(op.getLocation(), op2.getLocation());
		
		assertEquals(status.getId(), status2.getId());
		assertEquals(status.getName(), status2.getName());
		assertEquals(status.getDescr(), status2.getDescr());
		
		//handle nonexistent search op
		try {
			op = basicPath
					.path("getSearchOp")
					.path("debug_nonexistent_op")
					.accept(MediaType.APPLICATION_JSON)
					.get(SearchOperation.class);
		} catch (UniformInterfaceException e) {
			//expected
			assertTrue("Got incorrect (Error 204) response", correctResponse(e));
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testApplyForSearchOp() {
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("opname", "testoperation");
		formData.add("name", "testuser");
		formData.add("email", "testuser@testmail.com");
		formData.add("tele", "123456789");
		ClientResponse response = null;
		try {
			response = basicPath.path("apply").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
			assertEquals("Received non-OK response", ClientResponse.Status.OK, response.getClientResponseStatus());
		} catch (UniformInterfaceException e) {
			fail(e.getMessage());
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSearchForOps() {
		SearchOperationIntro[] opIntro = null;
		
		//search for op, verify response
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("title", "Sökoperation ABC");
		queryParams.add("location", "Plats XYZ");
		queryParams.add("date", "" + System.currentTimeMillis());
		try {
			opIntro = basicPath
					.path("search")
					.queryParams(queryParams)
					.get(SearchOperationIntro[].class);
			assertNotNull(opIntro);
		} catch (UniformInterfaceException e) {
			assertTrue("Got incorrect (Error 204) response", correctResponse(e));
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
		
		//search for non-existent op, verify response
		try {
			opIntro = basicPath
					.path("search")
					.queryParam("title", "debug_nonexistent_op")
					.get(SearchOperationIntro[].class);
			assertNotNull(opIntro);
		} catch (UniformInterfaceException e) {
			//expected
			assertTrue("Got incorrect (Error 204) response", correctResponse(e));
		} catch (ClientHandlerException e) {
			fail(e.getMessage());
		}
	}

	private boolean correctResponse(UniformInterfaceException e) {
		ClientResponse response = e.getResponse();
		assertNotNull("Response was null, expected ClientResponse instance", response);
		assertEquals("Did not get a 204 response", response.getStatus(), 204);
		return true;
	}
}
