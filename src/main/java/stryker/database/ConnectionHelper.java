package stryker.database;

import java.io.IOException;
import java.io.InputStream;
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

	/**
	 * Open a database connection using <code>stryker.properties</code> or <code>hibernate.properties</code> as
	 * configuration.<br>
	 * First it tries to read <code>stryker.properties</code>, if there is no such file, it tries to read
	 * <code>hibernate.properties</code>.
	 * @return Database {@link Connection}.
	 * @throws StrykerException If cannot read any configuration file or if an error occurs during open connection.
	 */
	public static Connection getConnection() throws StrykerException {
		Properties properties = new Properties();
		try {

			InputStream stykerConnection = ConnectionHelper.class.getResourceAsStream("/stryker.properties");
			InputStream hibernateConnection = ConnectionHelper.class.getResourceAsStream("/hibernate.properties");
			
			String password;
			String user;
			String url;
			String driver;
			
			if (stykerConnection != null) {
				properties.load(stykerConnection);
				
				driver = properties.getProperty("driver");
				password = properties.getProperty("password");
				user = properties.getProperty("user");
				url = properties.getProperty("jdbc.url");
				
			} else {
				properties.load(hibernateConnection);

				driver = properties.getProperty("hibernate.connection.driver_class");
				password = properties.getProperty("hibernate.connection.password");
				user = properties.getProperty("hibernate.connection.username");
				url = properties.getProperty("hibernate.connection.url");
			}
			Class.forName(driver);
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