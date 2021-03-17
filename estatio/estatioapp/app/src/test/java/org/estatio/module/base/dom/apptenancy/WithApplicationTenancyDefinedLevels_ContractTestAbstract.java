/*
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
package org.estatio.module.base.dom.apptenancy;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.reflections.Reflections;

/**
 * Automatically tests all domain objects implementing {@link WithApplicationTenancy} also implement one of the marker interfaces to define the levels at which instances of that type can exist..
 *
 * <p>
 * Any that cannot be instantiated are skipped.
 */
public abstract class WithApplicationTenancyDefinedLevels_ContractTestAbstract {

    private final String packagePrefix;
    private Map<Class<?>, Class<?>> noninstantiableSubstitutes;

    protected WithApplicationTenancyDefinedLevels_ContractTestAbstract(String packagePrefix,
                                                              ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes) {
        this.packagePrefix = packagePrefix;
        this.noninstantiableSubstitutes = noninstantiableSubstitutes;
    }

    @Ignore // TODO: REVIEW: we haven't done this for newer classes, with no adverse effects.  We can probably remove this contract test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(packagePrefix);
        
        Set<Class<? extends WithApplicationTenancy>> subtypes =
                reflections.getSubTypesOf(WithApplicationTenancy.class);
        StringBuilder buf = new StringBuilder();
        for (Class<? extends WithApplicationTenancy> subtype : subtypes) {
            if(subtype.isInterface() ||
                    Modifier.isAbstract(subtype.getModifiers()) ||
                    subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass() ||
                    subtype.getName().endsWith("ForTesting")) {
                // skip
                continue;
            }
            subtype = instantiable(subtype);
            final String failureMessageIfAny = test(subtype);
            if(!Strings.isNullOrEmpty(failureMessageIfAny)) {
                if(buf.length() != 0) {
                    buf.append("\n");
                }
                buf.append(failureMessageIfAny);
            }
        }
        if(buf.length() != 0) {
            Assert.fail(buf.toString());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Class<? extends WithApplicationTenancy> instantiable(Class<? extends WithApplicationTenancy> cls) {
        final Class<?> substitute = noninstantiableSubstitutes.get(cls);
        return (Class<? extends WithApplicationTenancy>) (substitute!=null?substitute:cls);
    }

    private <T extends WithApplicationTenancy> String test(Class<T> cls) {
        return new WithApplicationTenancyDefinedLevelsContractTester<>(cls).test();
    }

}
