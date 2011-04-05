package org.jstryker.reflection;

/**
 * Class to verify if a annotation has been used in a class or method.
 */
public class AnnotationTester {

	/**
	 * Tests if the class is annotated with the annotation.
	 * @param clazz Class that should have the annotation.
	 * @param annotation The annotation that should be in class.
	 */
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