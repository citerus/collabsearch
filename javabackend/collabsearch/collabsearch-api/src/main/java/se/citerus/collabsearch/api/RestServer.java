package se.citerus.collabsearch.api;

import java.io.IOException;
import java.util.Date;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.StringArrayWrapper;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.model.interfaces.RestService;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.mongodb.SearchMissionDAOMongoDB;

@Service
@Path("/ws")
public class RestServer implements RestService {
	private static final int NOT_FOUND = 404;
	private static final int INTERNAL_SERVER_ERROR = 500;
	
//	@Autowired
	private SearchOperationDAO dao;
	
	public RestServer() {
		try {
			ApplicationContext context = 
				new AnnotationConfigApplicationContext("se.citerus.collabsearch.store");
		    dao = context.getBean(SearchMissionDAOMongoDB.class);
		    assert(dao != null);
		    
//		    context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("/getAllOps")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SearchOperationWrapper[] getAllOps() {
		SearchOperationWrapper[] array = null;
		try {
			array = dao.getAllSearchOpsInShortForm();
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(INTERNAL_SERVER_ERROR);
		}
		return array;
	}

	@GET
	@Path("/getSearchOp/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.TEXT_PLAIN)
	public SearchOperation getSearchOperationById(@PathParam("id") String id) {
		if (id == null) {
			throw new WebApplicationException(NOT_FOUND);
		}
		SearchOperation searchOperation = null;
		try {
			searchOperation = dao.getSearchOpById(id);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(INTERNAL_SERVER_ERROR);
		}
		if (searchOperation == null) {
			throw new WebApplicationException(NOT_FOUND);
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
		} catch (SearchOperationNotFoundException e) {
			throw new WebApplicationException(NOT_FOUND);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@GET
	@Path("/search")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SearchOperationWrapper[] searchForOps(
			@DefaultValue("") @QueryParam("title") String title, 
			@DefaultValue("") @QueryParam("location") String location, 
			@DefaultValue("") @QueryParam("startdate") String startDate,
			@DefaultValue("") @QueryParam("enddate") String endDate) {
		SearchOperationWrapper[] array = null;
		try {
			array = dao.getSearchOpsByFilter(title, location, startDate, endDate);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(INTERNAL_SERVER_ERROR);
		}
		if (array == null || array.length == 0) {
			throw new WebApplicationException(NOT_FOUND);
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
			throw new WebApplicationException(INTERNAL_SERVER_ERROR);
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
			throw new WebApplicationException(INTERNAL_SERVER_ERROR);
		}
		return array;
	}
}
