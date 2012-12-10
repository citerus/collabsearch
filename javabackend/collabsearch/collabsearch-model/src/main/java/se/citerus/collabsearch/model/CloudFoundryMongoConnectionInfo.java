package se.citerus.collabsearch.model;

/**
 * A value object class containing environments settings for a CloudFoundry
 * MongoDB connection.
 * 
 * @author ola
 */
public class CloudFoundryMongoConnectionInfo {
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String db;

	public CloudFoundryMongoConnectionInfo(String host, int port,
			String username, String password, String db) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.db = db;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDb() {
		return db;
	}

	@Override
	public String toString() {
		return "host: " + host + " port: " + port + " username: " + username
				+ " password: " + password + " db: " + db;
	}
}
