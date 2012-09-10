package se.citerus.collabsearch.publicwebsite.logic;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.interfaces.RestService;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RestClient implements RestService {
	
	private WebResource basicPath;

	public RestClient() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(
			UriBuilder.fromUri("http://localhost:8080/collabsearch-api").build());
		basicPath = service.path("rest").path("ws");
	}

	public SearchOperationIntro[] getAllOps() throws Exception {
		SearchOperationIntro[] array;
		try {
			array = basicPath.path("getAllOps").accept(MediaType.APPLICATION_JSON).get(SearchOperationIntro[].class);
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		if (array == null) {
			array = new SearchOperationIntro[0];
		}
		return array;
	}

	public SearchOperation getSearchOperationById(String id) throws Exception {
		try {
			return basicPath
					.path("getSearchOp")
					.path(id)
					.accept(MediaType.APPLICATION_JSON)
					.get(SearchOperation.class);
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public Response applyForSearchOp(String opName, String name, String email,
			String tele) throws Exception {
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("opname", opName);
		formData.add("name", name);
		formData.add("email", email);
		formData.add("tele", tele);
		try {
			ClientResponse response = basicPath.path("apply").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
			if (response.getStatus() != 200) {
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

	public SearchOperationIntro[] searchForOps(String title, String location,
			String date) throws Exception {
		SearchOperationIntro[] array;
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("title", title);
		queryParams.add("location", location);
		queryParams.add("date", date);
		try {
			array = basicPath
					.path("search")
					.queryParams(queryParams)
					.get(SearchOperationIntro[].class);
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		if (array == null) {
			array = new SearchOperationIntro[0];
		}
		return array;
	}
	
}
