package org.jstryker.database.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jstryker.exception.JStrykerException;

public class ConnectionPropertiesReaderFactory {

	public ConnectionPropertiesReader getConnectionPropertiesReader() throws JStrykerException {
		Properties properties = new Properties();
		ConnectionPropertiesReader propertiesReader;
		InputStream stream = getClass().getResourceAsStream("/jstryker.properties");
		
		try {
			if (stream != null) {
				properties.load(stream);
				propertiesReader = new JStrykerPropertiesReader(properties);
			}

			stream = getClass().getResourceAsStream("/hibernate.properties");
			
			if (stream != null) {
				properties.load(stream);
				propertiesReader = new HibernatePropertiesReader(properties);
			} else {
				throw new JStrykerException("jstryker.properties or hibernate.properties not found.");
			}
			
			stream.close();

		} catch (IOException e) {
			throw new JStrykerException(e.getMessage(), e);
		}

		return propertiesReader;
	}
}