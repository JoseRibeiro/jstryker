package org.jstryker.reflection;

import static org.jstryker.reflection.AnnotationTester.isAnnotationPresent;

import javax.annotation.Resource;
import javax.annotation.Resources;

import org.jstryker.domain.Annotated;
import org.jstryker.exception.JStrykerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests to {@link AnnotationTester}.
 */
public class AnnotationTesterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldKnowThatFieldIsAnnotated() throws Exception {
        isAnnotationPresent(Annotated.class, Resource.class);
    }

    @Test
    public void shouldKnowThatFieldIsNotAnnotated() throws Exception {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Annotation javax.annotation.Resources is not present in org.jstryker.domain.Annotated class.");
        isAnnotationPresent(Annotated.class, Resources.class);
    }
   
    @Test
    public void cannotVerifyIfAnnotationIsPresentWhenClassIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Class cannot be null.");
        isAnnotationPresent(null, Resources.class);
    }
   
    @Test
    public void cannotVerifyIfAnnotaionIsPresentWhenAnnotationIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation cannot be null.");
        isAnnotationPresent(Annotated.class, null);
    }
    
    @Test
    public void shouldKnowThatMethodIsAnnotated() throws Exception {
        isAnnotationPresent(Annotated.class, "annotatedMethod", Resource.class);
    }
    
    @Test
    public void shouldKnowThatMethodIsNotAnnotated() throws Exception {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Annotation javax.annotation.Resources is not present in method notAnnotatedMethod on org.jstryker.domain.Annotated class.");
        isAnnotationPresent(Annotated.class, "notAnnotatedMethod", Resources.class);
    }
    
    @Test
    public void cannotVerifyIfAnnotationIsPresentInMethodWhenClassIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Class cannot be null.");
        isAnnotationPresent(null, "", Resources.class);
    }
   
    @Test
    public void cannotVerifyIfAnnotaionIsPresentInMethodWhenAnnotationIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation cannot be null.");
        isAnnotationPresent(Annotated.class, "", null);
    }
    
    @Test
    public void cannotVerifyIfAnnotaionIsPresentInMethodWhenMethodNameIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Method name cannot be null.");
        isAnnotationPresent(Annotated.class, null, Resources.class);
    }
    
    @Test
    public void cannotVerifyIfAnnotaionIsPresentInMethodWhenMethodDoesNotExist() throws Exception {
        thrown.expect(JStrykerException.class);
        thrown.expectMessage("Method name unknownMethod does not exist in org.jstryker.domain.Annotated class.");
        isAnnotationPresent(Annotated.class, "unknownMethod", Resources.class);
    }
}
