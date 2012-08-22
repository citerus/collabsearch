package se.citerus.collabsearch.api;

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
import javax.ws.rs.core.MediaType;

import se.citerus.collabsearch.model.SearchOperationDTO;

@Path("/ws")
public class RestServiceImpl implements RestService {

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
	public SearchOperationDTO[] getAllOps() {
		SearchOperationDTO[] array = new SearchOperationDTO[3];
		array[0] = new SearchOperationDTO("Sökoperation 1", "text...", new Date(
				System.currentTimeMillis()), "Plats X");
		array[1] = new SearchOperationDTO("Sökoperation 2", "text...", new Date(
				System.currentTimeMillis() + 86400000), "Plats Y");
		array[2] = new SearchOperationDTO("Sökoperation 3", "text...", new Date(
				System.currentTimeMillis() + 86400000 * 2), "Plats Z");
		return array;
	}

	@GET
	@Path("/getSearchOp/{name}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.TEXT_PLAIN)
	public SearchOperationDTO getSearchOperation(@PathParam("name") String name) {
		//TODO search db for single op with matching name/id
		SearchOperationDTO op = new SearchOperationDTO(name, "beskrivning här",
				new Date(System.currentTimeMillis()), "Plats XYZ");
		return op;
	}

	@POST
	@Path("/apply")
	@Consumes("application/x-www-form-urlencoded")
	public void applyForSearchOp(
			@FormParam("opname") String opName, 
			@FormParam("name") String name, 
			@FormParam("email") String email, 
			@FormParam("tele") String tele) {
		//TODO store application in db
	}

	@GET
	@Path("/search")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SearchOperationDTO[] searchForOps(
			@DefaultValue("") @QueryParam("title") String title, 
			@DefaultValue("") @QueryParam("location") String location, 
			@DefaultValue("") @QueryParam("date") String date) {
		//TODO search db for matching ops
		return null;
	}

}
