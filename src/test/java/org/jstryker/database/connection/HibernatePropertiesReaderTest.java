package org.jstryker.database.connection;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * Tests to {@link HibernatePropertiesReader}.
 */
public class HibernatePropertiesReaderTest {

	private HibernatePropertiesReader hibernatePropertiesReader;

	@Before
	public void setUp() throws Exception {
		hibernatePropertiesReader = new HibernatePropertiesReader();
	}

	@Test
	public void shouldGetCorrectPropertiesFileName() throws Exception {
		assertEquals("hibernate.properties", hibernatePropertiesReader.getPropertiesFileName());
	}

	@Test
	public void shouldReadProperties() throws Exception {
		Properties properties = new Properties();
		InputStream stream = getClass().getResourceAsStream("/hibernate.properties");
		properties.load(stream);
		hibernatePropertiesReader.read(properties);
		stream.close();

		assertEquals("sa", hibernatePropertiesReader.getUsername());
		assertEquals("", hibernatePropertiesReader.getPassword());
		assertEquals("jdbc:hsqldb:mem:hibernate", hibernatePropertiesReader.getUrl());
		assertEquals("org.hsqldb.jdbcDriver", hibernatePropertiesReader.getDriver());
	}
}
