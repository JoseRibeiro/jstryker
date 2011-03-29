package org.jstryker.database.connection;

public interface ConnectionPropertiesReader {
	
	String getUsername();
	
	String getPassword();
	
	String getUrl();
	
	String getDriver();
}
