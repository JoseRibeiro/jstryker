package stryker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import stryker.exception.StrykerException;

public final class ConnectionHelper {
	
	/**
	 * Cannot be instantiate.
	 */
	private ConnectionHelper() {
	}
	
	public static Connection getConnection(String database) throws StrykerException {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			return DriverManager.getConnection("jdbc:hsqldb:mem:" + database); 
		} catch (ClassNotFoundException e) {
			throw new StrykerException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new StrykerException(e.getMessage(), e);
		}
	}
}
