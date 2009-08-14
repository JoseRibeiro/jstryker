package stryker.helper;

import java.lang.reflect.Field;

import stryker.exception.StrykerException;

/**
 * Helps set and get values in fields using reflection.
 */
public class ReflectionHelper {

	public static void injectValueStaticField(Class<?> clazz, String fieldName, Object value) 
	throws NoSuchFieldException, IllegalAccessException {

		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(clazz, value);
	}

	/**
	 * Inject a value into a field.
	 * @param object Instance that contains the field.
	 * @param fieldName Name of the field.
	 * @param value Value to be injected.
	 */
	public static void injectValue(Object object, String fieldName, Object value) {

		if (object == null) {
			throw new IllegalArgumentException("Object cannot be null.");
		}

		if (fieldName == null) {
			throw new IllegalArgumentException("Field name cannot be null.");
		}

		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (NoSuchFieldException e) {
			String message = String.format("%s does not have field %s.", object.getClass(), fieldName);
			throw new StrykerException(message, e);
		} catch (IllegalAccessException e) {
			throw new StrykerException(e.getMessage(), e);
		}
	}

	public static Object getValue(Object object, String fieldName) 
	throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);

		return field.get(object);
	}

	public static Field getField(Object object, String fieldName) 
	throws SecurityException, NoSuchFieldException {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}
}