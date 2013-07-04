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
package org.estatio.dom.contracttests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.base.Predicate;

import org.junit.Test;
import org.reflections.Reflections;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Optional;

import org.estatio.dom.WithInterval;


public class WithIntervalContractTestAll_getStartDateAndEndDate_annotations {

    @Test
    @SuppressWarnings("rawtypes")
    public void searchAndTest() throws Exception {
        
        Reflections reflections = new Reflections(Constants.packagePrefix);

        Set<Class<? extends WithInterval>> domainObjectClasses = reflections.getSubTypesOf(WithInterval.class);
        for (final Class<? extends WithInterval> subtype : domainObjectClasses) {
            if(subtype.isInterface() || subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                continue;
            }
            
            System.out.println("checking " + subtype.getName());

            final Set<Method> startDateMethod = Reflections.getAllMethods(subtype, withGetterNamed("getStartDate"));
            for (Method method : startDateMethod) {
                assertMethodAnnotated(subtype, method, Disabled.class);
                assertMethodAnnotated(subtype, method, Optional.class);
            }
            
            final Set<Method> endDateMethod = Reflections.getAllMethods(subtype, withGetterNamed("getEndDate"));
            for (Method method : endDateMethod) {
                assertMethodAnnotated(subtype, method, Disabled.class);
                assertMethodAnnotated(subtype, method, Optional.class);
            }
            
        }
    }

    private static Predicate<? super Method> withGetterNamed(final String methodName) {
        return (Predicate<? super Method>) new Predicate<Method>() {
            public boolean apply(Method f) {
                return f.getName().equals(methodName) && 
                       f.getParameterTypes().length == 0;
            }
        };
    }

    private static void assertMethodAnnotated(final Class<? extends WithInterval> subtype, final Method method, final Class<? extends Annotation> annotationClass) {
        final Annotation annotation = method.getAnnotation(annotationClass);
        final String methodName = method.getName();
        assertThat(subtype.getName() + "#" +
        		methodName +
        		"() must be annotated as @" +
                annotationClass.getSimpleName(),  
                annotation, is(not(nullValue())));
    }

}
