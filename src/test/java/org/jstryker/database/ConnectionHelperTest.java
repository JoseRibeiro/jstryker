package org.jstryker.database;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link ConnectionHelper}.
 */
public class ConnectionHelperTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void cannotBeInstantiate() throws Exception {
		thrown.expect(IllegalAccessException.class);
		ConnectionHelper.class.newInstance();
	}
	
	@Test
	public void shouldCreateANewConnection() throws Exception {
		assertNotNull(ConnectionHelper.getConnection());
	}
}
