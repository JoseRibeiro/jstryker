package org.jstryker;

import javax.annotation.Resource;
import javax.annotation.Resources;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.jstryker.domain.Annotated;

/**
 * Tests to {@link AnnotationTester}.
 */
public class AnnotationTesterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldKnowThatFieldIsAnnotated() throws Exception {
        AnnotationTester.isAnnotationPresent(Annotated.class, Resource.class);
    }

    @Test
    public void shouldKnowThatFieldIsNotAnnotated() throws Exception {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Annotation javax.annotation.Resources is not present in org.jstryker.domain.Annotated class.");
        AnnotationTester.isAnnotationPresent(Annotated.class, Resources.class);
    }
   
    @Test
    public void cannotVerifyIfAnnotationIsPresentWhenClassIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Class cannot be null.");
        AnnotationTester.isAnnotationPresent(null, Resources.class);
    }
   
    @Test
    public void cannotVerifyIfAnnotaionIsPresentWhenAnnotationIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Annotation cannot be null.");
        AnnotationTester.isAnnotationPresent(Annotated.class, null);
    }
}
