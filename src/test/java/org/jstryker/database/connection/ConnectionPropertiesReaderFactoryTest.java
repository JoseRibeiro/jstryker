package org.jstryker.database.connection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jstryker.exception.JStrykerException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link ConnectionPropertiesReaderFactory}.
 */
public class ConnectionPropertiesReaderFactoryTest {
	
	private ConnectionPropertiesReaderFactory factory;
	private List<ConnectionPropertiesReader> readers;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		readers = new ArrayList<ConnectionPropertiesReader>();
		factory = new ConnectionPropertiesReaderFactory(readers);
	}
	
	@Test
	public void shouldReadConnectionProperties() throws Exception {
		ConnectionPropertiesReader reader = new HibernatePropertiesReader();
		readers.add(reader);
		
		assertSame(reader, factory.getConnectionPropertiesReader());
		assertNotNull(reader.getDriver());
	}
	
	@Test
	public void shouldThrowJStrykerExceptionWhenSQLExceptionOccurs() throws Exception {
		String reason = "jstryker.properties and hibernate.properties not found in classpath.";
		thrown.expect(JStrykerException.class);
		thrown.expectMessage(reason);
		
		factory.getConnectionPropertiesReader();
	}
}