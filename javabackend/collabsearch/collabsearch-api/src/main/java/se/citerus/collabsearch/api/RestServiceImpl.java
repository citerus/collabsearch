package se.citerus.collabsearch.api;

import java.util.Arrays;
import java.util.List;

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
		return "Hello world!";
	}

	@GET
	@Path("/getAllOps")
	@Produces(MediaType.TEXT_PLAIN)
	public List<String> getAllOps() {
		List<String> list = Arrays.asList("Sökop 1", "Sökop 2", "Sökop 3");
		return list;
	}

	@GET
	@Path("/echo/{input}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String echo(@PathParam("input") String input) {
		return "Hello " + input;
	}

}
