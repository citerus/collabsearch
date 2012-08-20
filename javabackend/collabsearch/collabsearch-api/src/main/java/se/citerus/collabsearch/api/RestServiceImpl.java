package se.citerus.collabsearch.api;

import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ws")
public class RestServiceImpl implements RestService {

	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "Hello world!" + "\n";
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
	@Path("/echo/{input}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String echo(@PathParam("input") String input) {
		return "Hello " + input + "\n";
	}

	@GET
	@Path("/getSeaOp/{name}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.TEXT_PLAIN)
	public SearchOperationDTO getSearchOperation(@PathParam("name") String name) {
		SearchOperationDTO op = new SearchOperationDTO(name, "beskrivning här",
				new Date(System.currentTimeMillis()), "Plats XYZ");
		return op;
	}

}
