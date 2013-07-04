/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
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
