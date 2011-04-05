package org.jstryker.reflection;

public class AnnotationTester {

	@SuppressWarnings("unchecked")
	public static void isAnnotationPresent(Class clazz, Class<?> annotation) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class cannot be null.");
		}

		if (annotation == null) {
			throw new IllegalArgumentException("Annotation cannot be null.");
		}

		if (clazz.getAnnotation(annotation) == null) {
			String message = 
				String.format("Annotation %s is not present in %s class.", annotation.getName(), clazz.getName());
			throw new AssertionError(message);
		}
	}
}