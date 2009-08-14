package striker.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import striker.domain.OnlyGet;
import stryker.exception.StrykerException;
import stryker.helper.ReflectionHelper;

/**
 * Test to {@link ReflectionHelper}.
 */
public class ReflectionHelperTest {

	private OnlyGet onlyGet;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		onlyGet = new OnlyGet();
	}

	@Test
	public void shouldSetValue() throws Exception {
		Object value = "Bob";
		ReflectionHelper.injectValue(onlyGet, "name", value);

		assertSame("Should set value in attribute name.", value, onlyGet.getName());
	}

	@Test(expected = StrykerException.class)
	public void cannotSetValueToInexistentField() throws Exception {
		ReflectionHelper.injectValue(onlyGet, "inexistentField", "Bob");
	}

	@Test
	public void cannotSetValueInANullReference() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Object cannot be null.");

		ReflectionHelper.injectValue(null, "anyName", "anyValue");
	}

	@Test
	public void cannotSetValueInANullFieldName() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Field name cannot be null.");

		ReflectionHelper.injectValue(onlyGet, null, "anyValue");
	}

	@Test
	public void shouldSetNull() throws Exception {
		assertNotNull("Description should not be null.", onlyGet.getDescription());

		ReflectionHelper.injectValue(onlyGet, "description", null);

		assertNull("Should set null value.", onlyGet.getDescription());
	}
}