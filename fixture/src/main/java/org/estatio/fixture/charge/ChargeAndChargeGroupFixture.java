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
package org.estatio.fixture.charge;

import javax.inject.Inject;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.apache.isis.applib.fixturescripts.FixtureResultList;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class ChargeAndChargeGroupFixture extends SimpleFixtureScript {

    @Override
    protected void doRun(String parameters, FixtureResultList fixtureResults) {
        createCharges(fixtureResults);
    }

    private void createCharges(FixtureResultList fixtureResults) {
        createChargeAndChargeGroup("RENT", "Rent", "IT-VATSTD", fixtureResults);
        createChargeAndChargeGroup("SERVICE_CHARGE", "Service Charge", "IT-VATSTD", fixtureResults);
        createChargeAndChargeGroup("TURNOVER_RENT", "Turnover Rent", "IT-VATSTD", fixtureResults);
    }

    private void createChargeAndChargeGroup(String reference, String description, String taxReference, FixtureResultList fixtureResults) {
        ChargeGroup chargeGroup = createChargeGroup(reference, description, fixtureResults);
        String code = reference; // TODO: for want of anything better...
        createCharge(reference, code, description, taxReference, chargeGroup, fixtureResults);
    }

    private ChargeGroup createChargeGroup(String reference, String description, FixtureResultList fixtureResults) {
        final ChargeGroup chargeGroup = chargeGroups.createChargeGroup(reference, description);
        return fixtureResults.add(this, chargeGroup.getReference(), chargeGroup);
    }

    private Charge createCharge(String reference, String code, String description, String taxReference, ChargeGroup chargeGroup, FixtureResultList fixtureResults) {
        final Tax tax = taxRepository.findTaxByReference(taxReference);
        Charge charge = charges.newCharge(reference, description, code, tax, chargeGroup);
        charge.setGroup(chargeGroup);
        return fixtureResults.add(this, charge.getReference(), charge);
    }

    // //////////////////////////////////////

    @Inject
    private ChargeGroups chargeGroups;

    @Inject
    private Charges charges;

    @Inject
    private Taxes taxRepository;

}
