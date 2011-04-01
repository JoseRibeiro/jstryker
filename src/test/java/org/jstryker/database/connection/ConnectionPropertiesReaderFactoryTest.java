package org.jstryker.database.connection;

import org.jstryker.exception.JStrykerException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
		ConnectionPropertiesReader reader = spy(new JStrykerPropertiesReader());
		readers.add(reader);
		
		assertSame(reader, factory.getConnectionPropertiesReader());
		verify(reader).read(any(Properties.class));
	}

	@Test
	public void shouldIterateReadersUntilFindUsableProperties() throws Exception {
		ConnectionPropertiesReader unusableProperties = mock(ConnectionPropertiesReader.class);
		ConnectionPropertiesReader usableProperties = new HibernatePropertiesReader();
		readers.add(unusableProperties);
		readers.add(usableProperties);

		assertSame(usableProperties, factory.getConnectionPropertiesReader());
	}

	@Test
	public void shouldThrowJStrykerExceptionWhenProperpertiesNotFoundInClasspath() throws Exception {
		String reason = "jstryker.properties or hibernate.properties not found in classpath.";
		thrown.expect(JStrykerException.class);
		thrown.expectMessage(reason);

		factory.getConnectionPropertiesReader();
	}
}