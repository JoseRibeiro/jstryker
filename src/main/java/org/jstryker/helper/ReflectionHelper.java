package org.jstryker.helper;

import org.jstryker.exception.JStrykerException;

import java.lang.reflect.Field;

/**
 * Helps set and get values in fields using reflection.
 */
public final class ReflectionHelper {

	/**
	 * Cannot be instantiate. 
	 */
	private ReflectionHelper() {
	}

	/**
	 * Inject a value into a field.
	 * @param object Instance that contains the field.
	 * @param fieldName Name of the field.
	 * @param value Value to be injected.
	 * @throws IllegalArgumentException When object or fieldName is null.
	 * @throws JStrykerException When value cannot be injected.
	 */
	public static void injectValue(Object object, String fieldName, Object value) 
	throws JStrykerException, IllegalArgumentException {

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
			throw new JStrykerException(message, e);
		} catch (IllegalAccessException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}

	/**
	 * Get a value from a field.
	 * @param object Instance that contains the field.
	 * @param fieldName Name of the field.
	 * @return Value of the field.
	 * @throws IllegalArgumentException When object or fieldName is null.
	 * @throws JStrykerException When value cannot be got.
	 */
	public static Object getValue(Object object, String fieldName) throws JStrykerException, IllegalArgumentException {

		if (object == null) {
			throw new IllegalArgumentException("Object cannot be null.");
		}

		if (fieldName == null) {
			throw new IllegalArgumentException("Field name cannot be null.");
		}

		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (NoSuchFieldException e) {
			String message = String.format("%s does not have field %s.", object.getClass(), fieldName);
			throw new JStrykerException(message, e);
		} catch (IllegalAccessException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}

	/**
	 * @param clazz {@link Class} that contains the field.
	 * @param fieldName  Name of the field.
	 * @param value of the field
	 * @throws IllegalArgumentException When object or fieldName is null.
	 * @throws JStrykerException When value cannot be injected.
	 */
	public static void injectValueInStaticField(Class<?> clazz, String fieldName, Object value) 
	throws JStrykerException, IllegalArgumentException {

		if (clazz == null) {
			throw new IllegalArgumentException("Clazz cannot be null.");
		}

		if (fieldName == null) {
			throw new IllegalArgumentException("Field name cannot be null.");
		}

		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(clazz, value);
		} catch (NoSuchFieldException e) {
			String message = String.format("%s does not have field %s.", clazz, fieldName);
			throw new JStrykerException(message, e);
		} catch (IllegalAccessException e) {
			throw new JStrykerException(e.getMessage(), e);
		}
	}
}