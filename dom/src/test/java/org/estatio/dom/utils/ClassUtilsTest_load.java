package org.estatio.dom.utils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClassUtilsTest_load {
    
    public static class SomeClass { }
    public static class SomeSubclass extends SomeClass { }
    public static class SomeOtherClass  { }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void doesExist() throws Exception {
        final Class<? extends SomeClass> cls = ClassUtils.load(SomeClass.class.getName(), SomeClass.class);
        assertThat(cls, is(not(nullValue())));
    }

    @Test
    public void doesNotExist() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Class 'org.estatio.dom.utils.SomeNonExistentClass' not found");
        final Class<? extends SomeClass> cls = ClassUtils.load("org.estatio.dom.utils.SomeNonExistentClass", SomeClass.class);
        assertThat(cls, is(not(nullValue())));
    }
    
    @Test
    public void existsAndIsSubclass() throws Exception {
        final Class<? extends SomeClass> cls = ClassUtils.load(SomeSubclass.class.getName(), SomeClass.class);
        assertThat(cls, is(not(nullValue())));
    }
    
    @Test
    public void existsAndIsNotASubclass() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Class 'org.estatio.dom.utils.ClassUtilsTest_load$SomeOtherClass' not a subclass of org.estatio.dom.utils.ClassUtilsTest_load$SomeClass");
        final Class<? extends SomeClass> cls = ClassUtils.load(SomeOtherClass.class.getName(), SomeClass.class);
        assertThat(cls, is(not(nullValue())));
    }
    
}
