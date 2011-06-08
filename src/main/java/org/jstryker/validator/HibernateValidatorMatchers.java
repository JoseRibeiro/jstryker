package org.jstryker.validator;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.jstryker.exception.JStrykerException;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

public class HibernateValidatorMatchers {
    
    private String field;
	private Class<?> clazz;
	private Field declaredField;

	public static HibernateValidatorMatchers validate() {
		return new HibernateValidatorMatchers();
	}

	public HibernateValidatorMatchers field(String field) {
		if (field == null) {
			throw new IllegalArgumentException("Field cannot be null.");
		}

		this.field = field;
		return this;
	}

	public HibernateValidatorMatchers inClass(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class cannot be null.");
		}

		this.clazz = clazz;
		return this;
	}

	private Field getDeclaredField() {
		if (declaredField == null) {
			try {
				declaredField = clazz.getDeclaredField(field);
			} catch (NoSuchFieldException e) {
				throw new JStrykerException(e.getMessage(), e);
			}
		}
		return declaredField;
	}

	public HibernateValidatorMatchers cannotBeEmpty() {
		if (!getDeclaredField().isAnnotationPresent(NotEmpty.class)) {
			String message = String.format("Field %s allow empty values.", field);
			throw new AssertionError(message);
		}

		return this;
	}

	public HibernateValidatorMatchers shoudlBeUrl() {
		if (!getDeclaredField().isAnnotationPresent(URL.class)) {
			String message = String.format("Field %s allow invalid url values.", field);
			throw new AssertionError(message);
		}

		return this;
	}

	public HibernateValidatorMatchers cannotBeNull() {
		if (!getDeclaredField().isAnnotationPresent(NotNull.class)) {
			String message = String.format("Field %s allow null values.", field);
			throw new AssertionError(message);
		}

		return this;
	}
}
