package se.citerus.collabsearch.api;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.StringArrayWrapper;
import se.citerus.collabsearch.model.interfaces.RestService;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.mongodb.SearchOperationDAOMongoDB;

@Path("/ws")
public class RestServer implements RestService {
	private SearchOperationDAO dao;
	
	public RestServer() {
		try {
			dao = new SearchOperationDAOMongoDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("/getAllOps")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SearchOperationIntro[] getAllOps() {
		SearchOperationIntro[] array = null;
		try {
			array = dao.getAllSearchOps();
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}
		return array;
	}

	@GET
	@Path("/getSearchOp/{name}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.TEXT_PLAIN)
	public SearchOperation getSearchOperationById(@PathParam("name") String id) {
		if (id == null) {
			throw new WebApplicationException(404);
		}
		SearchOperation searchOperation = null;
		try {
			searchOperation = dao.getSearchOpById(id);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}
		if (searchOperation == null) {
			throw new WebApplicationException(404);
		}
		return searchOperation;
	}

	@POST
	@Path("/apply")
	@Consumes("application/x-www-form-urlencoded")
	public Response applyForSearchOp(
			@FormParam("opname") String opName, 
			@FormParam("name") String name, 
			@FormParam("email") String email, 
			@FormParam("tele") String tele) {
		Response response = null;
		try {
			dao.assignUserToSearchOp(opName, name, email, tele);
			response = Response.ok()
				.lastModified(new Date(System.currentTimeMillis()))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			response = Response.serverError().build();
		}
		return response;
	}

	@GET
	@Path("/search")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SearchOperationIntro[] searchForOps(
			@DefaultValue("") @QueryParam("title") String title, 
			@DefaultValue("") @QueryParam("location") String location, 
			@DefaultValue("") @QueryParam("startdate") String startDate,
			@DefaultValue("") @QueryParam("enddate") String endDate) {
		SearchOperationIntro[] array = null;
		try {
			array = dao.getSearchOpsByFilter(title, location, startDate, endDate);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}
		if (array == null || array.length == 0) {
			throw new WebApplicationException(404);
		}
 		return array;
	}

	@GET
	@Path("/getAllLocations")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public StringArrayWrapper[] getAllLocations() throws Exception {
		StringArrayWrapper[] array = null;
		try {
			String[] opLocations = dao.getAllOpLocations();
			array = new StringArrayWrapper[opLocations.length];
			int i = 0;
			for (String string : opLocations) {
				array[i++] = new StringArrayWrapper(string);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}
		return array;
	}

	@GET
	@Path("/getAllTitles")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public StringArrayWrapper[] getAllTitles() throws Exception {
		StringArrayWrapper[] array = null;
		try {
			String[] opTitles = dao.getAllOpTitles();
			array = new StringArrayWrapper[opTitles.length];
			int i = 0;
			for (String string : opTitles) {
				array[i++] = new StringArrayWrapper(string);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}
		return array;
	}
}
