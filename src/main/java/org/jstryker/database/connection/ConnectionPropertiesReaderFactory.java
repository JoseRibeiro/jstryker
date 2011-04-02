package org.jstryker.database.connection;

import org.jstryker.exception.JStrykerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

// TODO javadoc
public class ConnectionPropertiesReaderFactory {

	private final List<ConnectionPropertiesReader> propertiesReader;

	public ConnectionPropertiesReaderFactory(List<ConnectionPropertiesReader> propertiesReader) {
		this.propertiesReader = propertiesReader;
	}

	public ConnectionPropertiesReader getConnectionPropertiesReader() throws JStrykerException {
		try {
			for (ConnectionPropertiesReader reader : propertiesReader) {
				InputStream stream = getClass().getResourceAsStream("/" + reader.getPropertiesFileName());
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
		
		throw new JStrykerException("jstryker.properties or hibernate.properties not found in classpath.");
	}
}