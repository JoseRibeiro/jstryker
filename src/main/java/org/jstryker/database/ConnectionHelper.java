package org.jstryker.database;

import org.jstryker.database.connection.ConnectionPropertiesReader;
import org.jstryker.database.connection.ConnectionPropertiesReaderFactory;
import org.jstryker.database.connection.HibernatePropertiesReader;
import org.jstryker.database.connection.JStrykerPropertiesReader;
import org.jstryker.exception.JStrykerException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Opens database connection.
 */
public final class ConnectionHelper {

	private static final List<ConnectionPropertiesReader> PROPERTIES_READERS = new ArrayList<ConnectionPropertiesReader>();

	static {
		PROPERTIES_READERS.add(new JStrykerPropertiesReader());
		PROPERTIES_READERS.add(new HibernatePropertiesReader());
	}

	/**
	 * Cannot be instantiate.
	 */
	private ConnectionHelper() {
	}

	/**
	 * Open a database connection using <code>jstryker.properties</code> or <code>hibernate.properties</code> as
	 * configuration.<br>
	 * First it tries to read <code>jstryker.properties</code>, if there is no such file, it tries to read
	 * <code>hibernate.properties</code>.
	 * @return Database {@link Connection}.
	 * @throws JStrykerException If cannot read any configuration file or if an error occurs during open connection.
	 */
	public static Connection getConnection() throws JStrykerException {
		try {
			ConnectionPropertiesReader propertiesReader =
					new ConnectionPropertiesReaderFactory(PROPERTIES_READERS).getConnectionPropertiesReader();
			Class.forName(propertiesReader.getDriver());
			return DriverManager.getConnection(propertiesReader.getUrl(),
											   propertiesReader.getUsername(),
											   propertiesReader.getPassword());
		} catch (ClassNotFoundException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}
}