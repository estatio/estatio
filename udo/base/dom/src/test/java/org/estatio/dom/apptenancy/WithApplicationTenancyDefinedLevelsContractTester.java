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
package org.estatio.dom.apptenancy;

import java.util.List;

import com.google.common.collect.Lists;

import org.estatio.dom.apptenancy.WithApplicationTenancyAny;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobal;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.apptenancy.WithApplicationTenancyPropertyLocal;


public class WithApplicationTenancyDefinedLevelsContractTester<T extends WithApplicationTenancy> {

    private final Class<T> cls;

    public WithApplicationTenancyDefinedLevelsContractTester(Class<T> cls) {
        this.cls = cls;
    }

    private static List<Class<?>> markerInterfaces = Lists.newArrayList(
            WithApplicationTenancyAny.class,
            WithApplicationTenancyGlobal.class,
            WithApplicationTenancyGlobalAndCountry.class,
            WithApplicationTenancyCountry.class,
            WithApplicationTenancyProperty.class,
            WithApplicationTenancyPropertyLocal.class
    );

    public String test() {
        System.out.println("WithApplicationTenancyDefinedLevelsContractTester: " + cls.getName());

        for (Class<?> markerInterface : markerInterfaces) {
            if(markerInterface.isAssignableFrom(cls)) {
                return null;
            }
        }
        return "Class '" + cls.getName() + "' does not also implement any of the WithApplicationTenancyXxx marker interfaces";
    }

}
