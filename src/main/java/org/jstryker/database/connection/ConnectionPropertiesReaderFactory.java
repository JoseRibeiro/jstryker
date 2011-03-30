package org.jstryker.database.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.jstryker.exception.JStrykerException;

public class ConnectionPropertiesReaderFactory {

	private final List<ConnectionPropertiesReader> propertiesReader;

	public ConnectionPropertiesReaderFactory(List<ConnectionPropertiesReader> propertiesReader) {
		this.propertiesReader = propertiesReader;
	}

	public ConnectionPropertiesReader getConnectionPropertiesReader() throws JStrykerException {
		try {
			for (ConnectionPropertiesReader reader : propertiesReader) {
				InputStream stream = getClass().getResourceAsStream(reader.getPropertyName());
				if (stream != null) {
					Properties properties = new Properties();
					properties.load(stream);
					reader.read(properties);
					stream.close();
					
					return reader;
				}
			}
		} catch (IOException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
		
		throw new JStrykerException("jstryker.properties and hibernate.properties not found in classpath.");
	}
}