package se.citerus.collabsearch.store;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;

import se.citerus.collabsearch.model.CloudFoundryMongoConnectionInfo;
import se.citerus.collabsearch.store.utils.CloudFoundrySettingsParser;
import junit.framework.Assert;
import junit.framework.TestCase;

public class CloudFoundryParserTest extends TestCase {

	@Test
	public void testParseExampleJsonSettingsString() throws JsonParseException, IOException, Exception {
		String content;
		content = "{\"mongodb-2.0\":[{\"name\":\"myDB\",\"label\":\"mongodb-2.0\",\"plan\":\"free\",\"tags\":[\"nosql\",\"document\",\"mongodb-2.0\",\"mongodb\"],\"credentials\":{\"hostname\":\"172.30.48.69\",\"host\":\"172.30.48.69\",\"port\":25225,\"username\":\"4d0cca0e-af2c-418b-bfb8-0a43a635f970\",\"password\":\"e6a241e8-e880-411b-a258-91759c95f10c\",\"name\":\"8b1acedc-ed58-45e9-ac12-e8bff690b562\",\"db\":\"db\",\"url\":\"mongodb:4d0cca0e-af2c-418b-bfb8-0a43a635f970:e6a241e8-e880-411b-a258-91759c95f10c@172.30.48.69:25225/db\"}}]}";
		
		CloudFoundryMongoConnectionInfo info = CloudFoundrySettingsParser.parseVcapServicesEnvVar(content);
		
		Assert.assertEquals("172.30.48.69", info.getHost());
		Assert.assertEquals(25225, info.getPort());
		Assert.assertEquals("4d0cca0e-af2c-418b-bfb8-0a43a635f970", info.getUsername());
		Assert.assertEquals("e6a241e8-e880-411b-a258-91759c95f10c", info.getPassword());
		Assert.assertEquals("db", info.getDb());
		
//		System.out.println(info);
	}
}
