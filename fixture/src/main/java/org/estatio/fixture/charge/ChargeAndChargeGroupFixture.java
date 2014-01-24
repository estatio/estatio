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

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class ChargeAndChargeGroupFixture extends AbstractFixture {

    @Override
    public void install() {
        createCharges();
    }

    private void createCharges() {
        createChargeAndChargeGroup("RENT", "Rent", "IT-VATSTD");
        createChargeAndChargeGroup("SERVICE_CHARGE", "Service Charge", "IT-VATSTD");
        createChargeAndChargeGroup("TURNOVER_RENT", "Turnover Rent", "IT-VATSTD");
    }

    private void createChargeAndChargeGroup(String reference, String description, String taxReference) {
        ChargeGroup chargeGroup = createChargeGroup(reference, description);
        String code = reference; // TODO: for want of anything better...
        createCharge(reference, code, description, taxReference, chargeGroup);
    }

    private ChargeGroup createChargeGroup(String reference, String description) {
        return chargeGroups.createChargeGroup(reference, description);
    }

    private void createCharge(String reference, String code, String description, String taxReference, ChargeGroup chargeGroup) {
        final Tax tax = taxRepository.findTaxByReference(taxReference);
        Charge c = charges.newCharge(reference, description, code, tax, chargeGroup);
        c.setGroup(chargeGroup);
    }

    // //////////////////////////////////////

    private ChargeGroups chargeGroups;
    
    public final void injectChargeGroups(ChargeGroups chargeGroups) {
        this.chargeGroups = chargeGroups;
    }
    
    private Charges charges;

    public final void injectChargeRepository(Charges chargeRepository) {
        this.charges = chargeRepository;
    }

    private Taxes taxRepository;

    public final void injectTaxRepository(Taxes taxes) {
        this.taxRepository = taxes;
    }

}
