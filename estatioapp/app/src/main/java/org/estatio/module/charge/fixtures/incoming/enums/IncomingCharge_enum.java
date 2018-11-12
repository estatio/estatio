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
package org.estatio.module.charge.fixtures.incoming.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Counterpart of both the {@link IncomingChargesFraXlsxFixture} and {@link IncomingChargesItaXlsxFixture},
 * provides type-safe lookup of charges set up in their respective spreadsheets.
 */
@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum IncomingCharge_enum implements PersonaWithFinder<Charge> {

    France("FRANCE"),
    FrProjectManagement("PROJECT MANAGEMENT"),
    FrTax("TAX"),
    FrWorks("WORKS"),
    FrRelocation("RELOCATION / DISPOSSESSION INDEMNITY"),
    FrArchitect("ARCHITECT / GEOMETRICIAN FEES"),
    FrLegal("LEGAL / BAILIFF FEES"),
    FrMarketing("MARKETING"),
    FrTenant("TENANT INSTALLATION WORKS"),
    FrSecurityAgents("SECURITY AGENTS"),
    FrLettingFees("LETTING FEES"),
    FrInsurance("INSURANCE"),
    FrFurnitures("FURNITURES / DECORATION"),
    FrOther("OTHER"),

    ItAcquisition("ITWT001"),
    ItExternalConsultantCosts("ITWT004"),
    ItConstruction("ITWT005"),
    ItInternalConsultantCosts("ITWT008"),

    ;

    private final String reference;

    @Override
    public Charge findUsing(final ServiceRegistry2 serviceRegistry) {
        final ChargeRepository repository =
                serviceRegistry.lookupService(ChargeRepository.class);
        return repository.findByReference(reference);
    }

}
