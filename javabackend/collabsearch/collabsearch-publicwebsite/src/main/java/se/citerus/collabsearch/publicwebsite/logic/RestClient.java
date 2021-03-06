package se.citerus.collabsearch.publicwebsite.logic;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.StringArrayWrapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RestClient {
	
	private static final String REST_SERVER_URL = "http://missingpeople-api.cloudfoundry.com/";
	private static final int OK = 200;
	private static final int NOT_FOUND = 404;
	private WebResource basicPath;

	public RestClient(String url) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(
			UriBuilder.fromUri(url).build());
		basicPath = service.path("rest").path("ws");
	}

	public SearchOperationWrapper[] getAllOps() throws Exception {
		SearchOperationWrapper[] array;
		try {
			array = basicPath
					.path("getAllOps")
					.accept(APPLICATION_JSON)
					.get(SearchOperationWrapper[].class);
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		if (array == null) {
			array = new SearchOperationWrapper[0];
		}
		return array;
	}

	public SearchOperation getSearchOperationById(String id) throws Exception {
		try {
			return basicPath
					.path("getSearchOp")
					.path(id)
					.accept(APPLICATION_JSON)
					.get(SearchOperation.class);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != NOT_FOUND) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return null;
	}

	public Response applyForSearchOp(String opName, String name, String email,
			String tele) throws Exception {
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("opname", opName);
		formData.add("name", name);
		formData.add("email", email);
		formData.add("tele", tele);
		try {
			ClientResponse response = basicPath
					.path("apply")
					.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.post(ClientResponse.class, formData);
			if (response.getStatus() != OK) {
				throw new Exception("Non-OK response received");
			}
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return null;
	}

	public SearchOperationWrapper[] searchForOps(String title, String location,
			String startDate, String endDate) throws Exception {
		SearchOperationWrapper[] array = null;
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		if (title != null && !title.equals("")) {
			queryParams.add("title", title);
		}
		if (location != null && !location.equals("")) {
			queryParams.add("location", location);
		}
		if (startDate != null && !(startDate.equals("0") || startDate.equals(""))) {
			queryParams.add("startdate", startDate);
		}
		if (endDate != null && !(endDate.equals("0") || endDate.equals(""))) {
			queryParams.add("enddate", endDate);
		}
		try {
			array = basicPath
					.path("search")
					.queryParams(queryParams)
					.get(SearchOperationWrapper[].class);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != NOT_FOUND) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		if (array == null) {
			array = new SearchOperationWrapper[0];
		}
		return array;
	}

	public StringArrayWrapper[] getAllLocations() throws Exception {
		StringArrayWrapper[] array = null;
		try {
			array = basicPath
					.path("getAllLocations")
					.accept(APPLICATION_JSON)
					.get(StringArrayWrapper[].class);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != NOT_FOUND) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		if (array == null) {
			array = new StringArrayWrapper[0];
		}
		return array;
	}

	public StringArrayWrapper[] getAllTitles() throws Exception {
		StringArrayWrapper[] array = null;
		try {
			array = basicPath
					.path("getAllTitles")
					.accept(APPLICATION_JSON)
					.get(StringArrayWrapper[].class);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != NOT_FOUND) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		if (array == null) {
			array = new StringArrayWrapper[0];
		}
		return array;
	}
	
}
