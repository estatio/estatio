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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;

import org.estatio.dom.WithIntervalContractTester.WIInstantiator;


/**
 * Automatically tests all domain objects implementing {@link WithInterval}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithIntervalContractTester}.
 */
@SuppressWarnings("rawtypes")
public abstract class WithIntervalContractTestAbstract_getInterval {

    private final String packagePrefix;
    private Map<Class, WIInstantiator> instantiatorByType;

    protected WithIntervalContractTestAbstract_getInterval(String packagePrefix, 
            Map<Class, WIInstantiator> map) {
        this.packagePrefix = packagePrefix;
        this.instantiatorByType = map;
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void searchAndTest() throws Exception {
        Reflections reflections = new Reflections(packagePrefix);
        
        Set<Class<? extends WithInterval>> subtypes = 
                reflections.getSubTypesOf(WithInterval.class);
        for (Class<? extends WithInterval> subtype : subtypes) {
            if(subtype.isInterface() || subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                continue;
            }
            WIInstantiator<?> instantiator = instantiatorFor(subtype);
            if(instantiator == null) {
                assertThat("No instantiator for " + subtype.getName(), instantiator, is(not(nullValue())));
            }
            final WithIntervalContractTester withIntervalContractTester = new WithIntervalContractTester(instantiator);
            withIntervalContractTester.testAll();
        }
    }

    @SuppressWarnings("unchecked")
    private WIInstantiator<?> instantiatorFor(Class<? extends WithInterval> subtype) {
        WIInstantiator<?> instantiator = lookup(subtype);
        while (instantiator == null) {
            final Class superClass = subtype.getSuperclass();
            if(WithInterval.class.isAssignableFrom(superClass)) {
                instantiator = lookup(superClass);
            } else {
                break;
            }
        }
        if(instantiator == null) {
            instantiator = new WIInstantiator(subtype); 
        }
        return instantiator;
    }

    private WIInstantiator lookup(Class<? extends WithInterval> subtype) {
        return instantiatorByType.get(subtype);
    }

}
