package org.jstryker.database.connection;

import org.junit.Test;

/**
 * Tests for {@link ConnectionPropertiesReaderFactory}.
 */
public class ConnectionPropertiesReaderFactoryTest {
	
	private ConnectionPropertiesReaderFactory factory;
	
	@Test
	public void bla() throws Exception {
		factory = new ConnectionPropertiesReaderFactory();
		ConnectionPropertiesReader r = factory.getConnectionPropertiesReader();
		r.getDriver();
	}
}
