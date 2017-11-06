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
package org.estatio.module.party.dom;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.party.dom.Party;

@javax.jdo.annotations.Discriminator("org.estatio.dom.party.PartyForTesting")
public class PartyForTesting extends Party {

    private final ApplicationTenancy applicationTenancy;

    public PartyForTesting() {
        this(null);
    }
    public PartyForTesting(final ApplicationTenancy applicationTenancy) {
        this.applicationTenancy = applicationTenancy;
    }
    public ApplicationTenancy getApplicationTenancy() {
        return applicationTenancy;
    }

}