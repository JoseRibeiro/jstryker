package org.jstryker.reflection;

import org.jstryker.exception.JStrykerException;

/**
 * Class to verify if a annotation has been used in a class or method.
 */
public class AnnotationTester {

	/**
	 * Tests if the class is annotated with the annotation.
	 * @param clazz Class that should have the annotation.
	 * @param annotation The annotation that should be in class.
	 * @throws IllegalArgumentException When clazz or annotation is null.
	 */
	@SuppressWarnings("unchecked")
	public static void isAnnotationPresent(Class clazz, Class<?> annotation) throws IllegalArgumentException {
		if (clazz == null) {
			throw new IllegalArgumentException("Class cannot be null.");
		}

		if (annotation == null) {
			throw new IllegalArgumentException("Annotation cannot be null.");
		}

		if (!clazz.isAnnotationPresent(annotation)) {
			String message = 
				String.format("Annotation %s is not present in %s class.", annotation.getName(), clazz.getName());
			throw new AssertionError(message);
		}
	}
	
	/**
	 * Tests if the method is annotated with the annotation.
	 * @param clazz Class that contain the method.
	 * @param methodName Name of the method that should be annotated.
	 * @param annotation The annotation that should be in method.
	 * @throws IllegalArgumentException When clazz, method or annotation is null.
	 * @throws JStrykerException When method name does not exist in class.
	 */
	@SuppressWarnings("unchecked")
	public static void isAnnotationPresent(Class<?> clazz, String methodName, Class annotation) 
	throws IllegalArgumentException, JStrykerException {
		if (clazz == null) {
			throw new IllegalArgumentException("Class cannot be null.");
		}

		if (annotation == null) {
			throw new IllegalArgumentException("Annotation cannot be null.");
		}
		
		if (methodName == null) {
			throw new IllegalArgumentException("Method name cannot be null.");
		}
		
		try {
			if (!clazz.getMethod(methodName).isAnnotationPresent(annotation)) {
				String message = 
					String.format("Annotation %s is not present in method %s on %s class.", 
							annotation.getName(), methodName, clazz.getName());
				throw new AssertionError(message);
			}
		} catch (SecurityException e) {
			throw new JStrykerException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			String message = String.format("Method name %s does not exist in %s class.", methodName, clazz.getName());
			throw new JStrykerException(message, e);
		}
	}
}