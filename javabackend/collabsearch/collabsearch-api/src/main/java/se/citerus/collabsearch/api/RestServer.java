package se.citerus.collabsearch.api;

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
import se.citerus.collabsearch.model.interfaces.RestService;

@Path("/ws")
public class RestServer implements RestService {
	//TODO needs SearchOpDAOMongoDB impl
	
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "Hello world!" + "\n";
	}

	@GET
	@Path("/echo/{input}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String echo(@PathParam("input") String input) {
		return "Hello " + input + "\n";
	}

	@GET
	@Path("/getAllOps")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SearchOperationIntro[] getAllOps() {
		//TODO get all ops from the db
		SearchOperationIntro[] array = new SearchOperationIntro[3];
		array[0] = new SearchOperationIntro("Sökoperation 1", "text...");
		array[1] = new SearchOperationIntro("Sökoperation 2", "text...");
		array[2] = new SearchOperationIntro("Sökoperation 3", "text...");
		if (array == null) {
			throw new WebApplicationException(404);
		} else if (array.length == 0) {
			throw new WebApplicationException(404);
		}
		return array;
	}

	@GET
	@Path("/getSearchOp/{name}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.TEXT_PLAIN)
	public SearchOperation getSearchOperationByName(@PathParam("name") String name) {
		//TODO search db for single op with matching name/id
		if (name == null) {
			throw new WebApplicationException(404);
		}
		if (name != null && name.equals("debug_nonexistent_op")) {
			throw new WebApplicationException(404);
		}
		Random r = new Random();
		SearchOperation op = new SearchOperation("" + r.nextLong(), 
			name, "beskrivning här",
			new Date(System.currentTimeMillis()), "Plats XYZ",
			new Status(0, "status 1", "beskrivn..."));
		return op;
	}

	@POST
	@Path("/apply")
	@Consumes("application/x-www-form-urlencoded")
	public Response applyForSearchOp(
			@FormParam("opname") String opName, 
			@FormParam("name") String name, 
			@FormParam("email") String email, 
			@FormParam("tele") String tele) {
		//TODO store application in db
		if (opName == null) { //if the operation was not found
			throw new WebApplicationException(404);
		}
		System.out.println("SearchOp application received: " + 
				opName + ", " + name + ", " + email + ", " + tele);
		Response response = Response
				.ok()
				.lastModified(new Date(System.currentTimeMillis()))
				.build();
		return response;
	}

	@GET
	@Path("/search")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SearchOperationIntro[] searchForOps(
			@DefaultValue("") @QueryParam("title") String title, 
			@DefaultValue("") @QueryParam("location") String location, 
			@DefaultValue("") @QueryParam("date") String date) {
		//TODO search db for matching ops
		if (title != null && title.equals("debug_nonexistent_op")) {
			throw new WebApplicationException(404);
		}
		SearchOperationIntro[] array = new SearchOperationIntro[3];
		array[0] = new SearchOperationIntro("Sökoperation 1", "text...");
		array[1] = new SearchOperationIntro("Sökoperation 2", "text...");
		array[2] = new SearchOperationIntro("Sökoperation 3", "text...");
		if (array == null) {
			throw new WebApplicationException(404);
		} else if (array.length == 0) {
			throw new WebApplicationException(404);
		}
		return array;
	}

}
