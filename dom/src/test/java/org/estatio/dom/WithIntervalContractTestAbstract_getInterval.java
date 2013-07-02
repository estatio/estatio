/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithInterval}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithIntervalContractTester}.
 */
public abstract class WithIntervalContractTestAbstract_getInterval {

    private final String packagePrefix;
    private Map<Class<?>, Class<?>> noninstantiableSubstitutes;

    protected WithIntervalContractTestAbstract_getInterval(String packagePrefix, 
            ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes) {
        this.packagePrefix = packagePrefix;
        this.noninstantiableSubstitutes = noninstantiableSubstitutes;
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(packagePrefix);
        
        Set<Class<? extends WithInterval>> subtypes = 
                reflections.getSubTypesOf(WithInterval.class);
        for (Class<? extends WithInterval> subtype : subtypes) {
            if(subtype.isInterface() || subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                return;
            }
            subtype = instantiable(subtype);
            test(subtype);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Class<? extends WithInterval<?>> instantiable(Class<? extends WithInterval> subtype) {
        final Class<?> substitute = noninstantiableSubstitutes.get(subtype);
        return (Class<? extends WithInterval<?>>) (substitute!=null?substitute:subtype);
    }

    private <T extends WithInterval<?>> void test(Class<T> cls) {
        new WithIntervalContractTester<T>(cls).test();
    }

}
