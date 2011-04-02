package org.jstryker.database.connection;

import java.util.Properties;

// TODO javadoc
public class HibernatePropertiesReader implements ConnectionPropertiesReader {

	private String password;
	private String username;
	private String url;
	private String driver;

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

	public String getPropertiesFileName() {
		return "hibernate.properties";
	}

	public void read(Properties properties) {
		driver = properties.getProperty("hibernate.connection.driver_class");
		password = properties.getProperty("hibernate.connection.password");
		username = properties.getProperty("hibernate.connection.username");
		url = properties.getProperty("hibernate.connection.url");
	}
}