package se.citerus.collabsearch.api;

import java.util.List;

public interface RestService {
	
	public String echo(String input);
	
	public List<String> getAllOps();
}
