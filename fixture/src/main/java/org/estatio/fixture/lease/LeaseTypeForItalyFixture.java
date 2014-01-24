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
package org.estatio.fixture.lease;

import org.estatio.dom.lease.LeaseTypes;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class LeaseTypeForItalyFixture extends AbstractFixture {

    private enum LeaseTypeData {

        AA("Apparecchiature Automatic"),
        AD("Affitto d'Azienda"),
        CG("Comodato Gratuito"),
        CO("Comodato"),
        DH("Dehors"),
        LO("Locazione"),
        OA("Occup. Abusiva Affito"),
        OL("Occup. Abusiva Locazione"),
        PA("Progroga Affitto"),
        PL("Progroga Locazione"),
        PP("Pannelli Pubblicitari"),
        PR("Precaria"),
        SA("Scritt. Privata Affitto"),
        SL("Scritt. Privata Locazione");

        private final String title;

        private LeaseTypeData(final String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }
    }

    @Override
    public void install() {
        for (LeaseTypeData ltd : LeaseTypeData.values()) {
            leaseTypes.findOrCreate(ltd.name(), ltd.title());
        }
    }

    // //////////////////////////////////////

    private LeaseTypes leaseTypes;

    public final void injectLeaseTypes(LeaseTypes leaseTypes) {
        this.leaseTypes = leaseTypes;
    }

}
