package org.jstryker.database.connection;

import java.util.Properties;

// TODO javadoc
public interface ConnectionPropertiesReader {
	
	String getUsername();

	String getPassword();

	String getUrl();

	String getDriver();

	String getPropertiesFileName();

	void read(Properties properties);
}
