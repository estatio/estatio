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
package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Set;

import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.reflections.Reflections;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.WithIntervalContractTester.WIInstantiator;
import org.estatio.services.clock.ClockService;


/**
 * Automatically tests all domain objects implementing {@link WithInterval}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithIntervalContractTester}.
 */
@SuppressWarnings("rawtypes")
public abstract class WithIntervalContractTestAbstract_getInterval {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ClockService mockClockService;
    
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
            final WithIntervalContractTester withIntervalContractTester = new WithIntervalContractTester(instantiator, context, mockClockService);
            withIntervalContractTester.testAll();
        }
    }

    @SuppressWarnings("unchecked")
    private WIInstantiator<?> instantiatorFor(final Class<? extends WithInterval> subtype) {
        WIInstantiator<?> instantiator = lookup(subtype);
        int i=0;
        while (instantiator == null) {
            i++;
            if(i == 20){
                // HACK: a bit of a hack, but ensures that we drop out and fail
                // if we forget to register a WIInstantiator.
                return null;
            }
            final Class superClass = subtype.getSuperclass();
            if (superClass == null) {
                return null;
            }
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
