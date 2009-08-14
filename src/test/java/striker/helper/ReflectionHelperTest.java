package striker.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import striker.domain.OnlyGet;
import striker.domain.OnlySet;
import stryker.exception.StrykerException;
import stryker.helper.ReflectionHelper;

/**
 * Test to {@link ReflectionHelper}.
 */
public class ReflectionHelperTest {

	private OnlyGet onlyGet;
	private OnlySet onlySet;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		onlyGet = new OnlyGet();
		onlySet = new OnlySet();
	}

	@Test
	public void shouldSetValue() throws Exception {
		Object value = "Bob";
		ReflectionHelper.injectValue(onlyGet, "name", value);

		assertSame("Should set value in attribute name.", value, onlyGet.getName());
	}

	@Test
	public void cannotSetValueToInexistentField() throws Exception {
		String fieldName = "inexistentField";
		String message = String.format("%s does not have field %s.", OnlyGet.class, fieldName);
		
		thrown.expect(StrykerException.class);
		thrown.expectMessage(message);

		ReflectionHelper.injectValue(onlyGet, fieldName, "Bob");
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
	
	@Test
	public void shouldGetValue() throws Exception {
		String value = "Bob";
		onlySet.setName(value);
		assertSame("Shoud get value from name.", value, ReflectionHelper.getValue(onlySet, "name"));
	}
	
	@Test
	public void cannotGetValueToInexistentField() throws Exception {
		String fieldName = "inexistentField";
		String message = String.format("%s does not have field %s.", OnlySet.class, fieldName);
		
		thrown.expect(StrykerException.class);
		thrown.expectMessage(message);
		
		ReflectionHelper.getValue(onlySet, "inexistentField");
	}
	
	@Test
	public void cannotGetValueInANullReference() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Object cannot be null.");

		ReflectionHelper.getValue(null, "anyName");
	}
	
	@Test
	public void cannotGetValueInANullFieldName() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Field name cannot be null.");

		ReflectionHelper.getValue(onlySet, null);
	}
}