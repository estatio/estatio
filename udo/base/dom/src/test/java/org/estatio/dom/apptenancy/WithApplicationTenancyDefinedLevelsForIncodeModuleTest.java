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
package org.estatio.dom.apptenancy;

import com.google.common.collect.ImmutableMap;

/**
 * Automatically tests all domain objects implementing {@link org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy} also implement one of the marker interfaces to define the levels at which instances of that type can exist..
 *
 * <p>
 * Any that cannot be instantiated are skipped.
 */
public class WithApplicationTenancyDefinedLevelsForIncodeModuleTest extends WithApplicationTenancyDefinedLevelsContractTestAbstract {

    public WithApplicationTenancyDefinedLevelsForIncodeModuleTest() {
        super("org.incode.module", ImmutableMap.<Class<?>, Class<?>>of());
    }


}
