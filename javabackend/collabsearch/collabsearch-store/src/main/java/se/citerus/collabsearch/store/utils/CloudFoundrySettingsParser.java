package se.citerus.collabsearch.store.utils;

import java.io.IOException;

import org.apache.commons.lang.Validate;

import se.citerus.collabsearch.model.CloudFoundryMongoConnectionInfo;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class CloudFoundrySettingsParser {

	public static CloudFoundryMongoConnectionInfo parseVcapServicesEnvVar(String content) throws JsonParseException, IOException, Exception {
		Validate.notNull(content);
		
		String host = null;
		int port = 0;
		String username = null;
		String password = null;
		String db = null;
		
		JsonParser parser = new JsonFactory().createJsonParser(content);
		JsonToken token = parser.nextToken();
		while (token != null) {
			if (token == JsonToken.FIELD_NAME) {
				String currentName = parser.getCurrentName();
				if ("host".equals(currentName)) {
					JsonToken nextToken = parser.nextToken();
					if (nextToken == JsonToken.VALUE_STRING) {
						host = parser.getText();
					}
				} else if ("port".equals(currentName)) {
					JsonToken nextToken = parser.nextToken();
					if (nextToken == JsonToken.VALUE_NUMBER_INT) {
						port = parser.getIntValue();
					}
				} else if ("username".equals(currentName)) {
					JsonToken nextToken = parser.nextToken();
					if (nextToken == JsonToken.VALUE_STRING) {
						username = parser.getText();
					}
				} else if ("password".equals(currentName)) {
					JsonToken nextToken = parser.nextToken();
					if (nextToken == JsonToken.VALUE_STRING) {
						password = parser.getText();
					}
				} else if ("db".equals(currentName)) {
					JsonToken nextToken = parser.nextToken();
					if (nextToken == JsonToken.VALUE_STRING) {
						db = parser.getText();
					}
				}
			}
			token = parser.nextToken();
		}
		
		Validate.notNull(host);
		Validate.isTrue(port > 0);
		Validate.notNull(username);
		Validate.notNull(password);
		Validate.notNull(db);
		
		return new CloudFoundryMongoConnectionInfo(host, port, username, password, db);
	}
}
