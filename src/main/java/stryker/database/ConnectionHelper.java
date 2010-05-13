package stryker.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import stryker.exception.StrykerException;

public final class ConnectionHelper {
	
	/**
	 * Cannot be instantiate.
	 */
	private ConnectionHelper() {
	}
	
	public static Connection getConnection() {
		Properties properties = new Properties();
		try {
			properties.load(ConnectionHelper.class.getResourceAsStream("/stryker.properties"));
			Class.forName(properties.getProperty("driver"));
			
			String password = properties.getProperty("password");
			String user = properties.getProperty("user");
			String url = properties.getProperty("jdbc.url");
			
			return DriverManager.getConnection(url, user, password); 
		} catch (IOException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new StrykerException(e.getMessage(), e);
		}
	}
}