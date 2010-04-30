package stryker.database;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
}
