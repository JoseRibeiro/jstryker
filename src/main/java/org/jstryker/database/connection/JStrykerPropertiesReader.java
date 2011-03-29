package org.jstryker.database.connection;

import java.util.Properties;

public class JStrykerPropertiesReader implements ConnectionPropertiesReader {

	private String password;
	private String username;
	private String url;
	private String driver;

	public JStrykerPropertiesReader(Properties properties) {
		driver = properties.getProperty("driver");
		password = properties.getProperty("password");
		username = properties.getProperty("user");
		url = properties.getProperty("jdbc.url");
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public String getUrl() {
		return url;
	}

	public String getDriver() {
		return driver;
	}
}