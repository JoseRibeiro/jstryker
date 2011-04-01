package org.jstryker.database.connection;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * Tests to {@link JStrykerPropertiesReader}.
 */
public class JStrykerPropertiesReaderTest {

	private JStrykerPropertiesReader jStrykerPropertiesReader;

	@Before
	public void setUp() throws Exception {
		jStrykerPropertiesReader = new JStrykerPropertiesReader();
	}

	@Test
	public void shouldGetCorrectPropertiesFileName() throws Exception {
		assertEquals("jstryker.properties", jStrykerPropertiesReader.getPropertiesFileName());
	}

	@Test
	public void shouldReadProperties() throws Exception {
		Properties properties = new Properties();
		InputStream stream = getClass().getResourceAsStream("/jstryker.properties");
		properties.load(stream);
		jStrykerPropertiesReader.read(properties);
		stream.close();

		assertEquals("sa", jStrykerPropertiesReader.getUsername());
		assertEquals("", jStrykerPropertiesReader.getPassword());
		assertEquals("jdbc:hsqldb:mem:jstryker", jStrykerPropertiesReader.getUrl());
		assertEquals("org.hsqldb.jdbcDriver", jStrykerPropertiesReader.getDriver());
	}
}
