/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.junit.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.UdoDomainRepositoryAndFactory;


/**
 * Automatically tests domain services injected into domain objects.
 */
public class DomainServicesInjectionTestAll_inject{

    static class InjectorAndField {
        private Class<?> type;
        private Method m;
        private UdoDomainRepositoryAndFactory<?> domainService;
        private Field f;
        public InjectorAndField(Class<?> type, Method m, UdoDomainRepositoryAndFactory<?> domainService ) {
            this.type = type;
            this.m = m;
            this.domainService = domainService;
        }
        public void invokeAndAssert(Object edo) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            m.invoke(edo, new Object[]{domainService});
            f.setAccessible(true);
            final Object object = f.get(edo);
            System.out.println("invoking " + type.getName() + "#" + m.getName());
            assertThat(object, is((Object)domainService));
        }
    }
    

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);

        final Map<Class<? extends UdoDomainRepositoryAndFactory>, UdoDomainRepositoryAndFactory<?>> domainServices = Maps.newHashMap();
        Set<Class<? extends UdoDomainRepositoryAndFactory>> subtypes =
                reflections.getSubTypesOf(UdoDomainRepositoryAndFactory.class);
        for (Class<? extends UdoDomainRepositoryAndFactory> subtype : subtypes) {
            if(subtype.isInterface() || subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                continue;
            }
            if(!domainServices.containsKey(subtype)) {
                UdoDomainRepositoryAndFactory dos;
                try {
                    dos = subtype.newInstance();
                    domainServices.put(subtype, dos);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        
        Set<Class<? extends UdoDomainObject>> domainObjectClasses =
                reflections.getSubTypesOf(UdoDomainObject.class);
        for (final Class<? extends UdoDomainObject> subtype : domainObjectClasses) {
            if(subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                continue;
            }
            
            Predicate<? super Method> injectors = new Predicate<Method>() {
                public boolean apply(Method m) {
                    if (!m.getName().startsWith("inject")) {
                        return false;
                    }
                    final Class<?>[] parameterTypes = m.getParameterTypes();
                    if(parameterTypes.length != 1) {
                        return false;
                    }
                    if(!domainServices.containsKey(parameterTypes[0])) {
                        return false;
                    }
                    return true;
                }
            };
            final Set<Method> injectorMethods = ReflectionUtils.getAllMethods(subtype, injectors);
            if(injectorMethods.isEmpty()) {
                continue;
            }
            final Iterable<InjectorAndField> injectorAndFields = Iterables.transform(injectorMethods, new Function<Method, InjectorAndField>(){
                public InjectorAndField apply(Method m) {
                    final UdoDomainRepositoryAndFactory<?> ds = domainServices.get(m.getParameterTypes()[0]);
                    return new InjectorAndField(subtype, m, ds);
                }
            } );


            try {
                final UdoDomainObject edo = subtype.newInstance();
                for (final InjectorAndField injector : injectorAndFields) {
                    Predicate<? super Field> injectorFields = new Predicate<Field>() {
                        public boolean apply(Field f) {
                            return f.getType() == injector.m.getParameterTypes()[0];
                        }
                    };
                    final Set<Field> fields = ReflectionUtils.getAllFields(subtype, injectorFields);
                    if(fields.size() != 1) {
                        continue;
                    }
                    injector.f = fields.iterator().next();
                    injector.invokeAndAssert(edo);
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

}
