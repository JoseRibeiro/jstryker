package org.jstryker.database.connection;

import java.util.Properties;

public interface ConnectionPropertiesReader {
	
	String getPropertyName();
	
	String getUsername();
	
	String getPassword();
	
	String getUrl();
	
	String getDriver();
	
	void read(Properties properties);
}
