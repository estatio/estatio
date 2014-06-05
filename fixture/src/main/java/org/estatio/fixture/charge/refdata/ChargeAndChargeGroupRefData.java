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
package org.estatio.fixture.charge.refdata;

import javax.inject.Inject;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.tax.refdata.TaxesAndTaxRatesRefData;

public class ChargeAndChargeGroupRefData extends EstatioFixtureScript {

    public static final String CHARGE_GROUP_REFERENCE_RENT = "RENT";
    public static final String CHARGE_GROUP_REFERENCE_SERVICE_CHARGE = "SERVICE_CHARGE";
    public static final String CHARGE_GROUP_REFERENCE_TURNOVER_RENT = "TURNOVER_RENT";
    public static final String CHARGE_GROUP_REFERENCE_DISCOUNT = "DISCOUNT";

    public static final String CHARGE_REFERENCE_RENT = "RENT";
    public static final String CHARGE_REFERENCE_SERVICE_CHARGE = "SERVICE_CHARGE";
    public static final String CHARGE_REFERENCE_TURNOVER_RENT = "TURNOVER_RENT";
    public static final String CHARGE_REFERENCE_DISCOUNT = "DISCOUNT";

    @Override
    protected void execute(ExecutionContext executionContext) {
        createCharges(executionContext);
    }

    private void createCharges(ExecutionContext executionContext) {
        createChargeGroupAndCharge(
                CHARGE_GROUP_REFERENCE_RENT, "Rent",
                CHARGE_REFERENCE_RENT, TaxesAndTaxRatesRefData.IT_VATSTD,
                executionContext);
        createChargeGroupAndCharge(
                CHARGE_GROUP_REFERENCE_SERVICE_CHARGE, "Service Charge",
                CHARGE_REFERENCE_SERVICE_CHARGE, TaxesAndTaxRatesRefData.IT_VATSTD,
                executionContext);
        createChargeGroupAndCharge(
                CHARGE_GROUP_REFERENCE_TURNOVER_RENT, "Turnover Rent",
                CHARGE_REFERENCE_TURNOVER_RENT, TaxesAndTaxRatesRefData.IT_VATSTD,
                executionContext);
        createChargeGroupAndCharge(
                CHARGE_GROUP_REFERENCE_DISCOUNT, "Discount",
                CHARGE_REFERENCE_DISCOUNT, TaxesAndTaxRatesRefData.IT_VATSTD,
                executionContext);
    }

    private void createChargeGroupAndCharge(String chargeGroupReference, String description, String chargeReference, String taxReference, ExecutionContext executionContext) {
        ChargeGroup chargeGroup = createChargeGroup(chargeGroupReference, description, executionContext);
        String code = chargeGroupReference; // TODO: for want of anything better...
        createCharge(chargeGroupReference, code, description, taxReference, chargeGroup, executionContext);
    }

    private ChargeGroup createChargeGroup(String chargeGroupReference, String description, ExecutionContext executionContext) {
        final ChargeGroup chargeGroup = chargeGroups.createChargeGroup(chargeGroupReference, description);
        return executionContext.add(this, chargeGroup.getReference(), chargeGroup);
    }

    private Charge createCharge(String chargeReference, String code, String description, String taxReference, ChargeGroup chargeGroup, ExecutionContext executionContext) {
        final Tax tax = taxRepository.findTaxByReference(taxReference);
        Charge charge = charges.newCharge(chargeReference, description, code, tax, chargeGroup);
        charge.setGroup(chargeGroup);
        return executionContext.add(this, charge.getReference(), charge);
    }

    // //////////////////////////////////////

    @Inject
    private ChargeGroups chargeGroups;

    @Inject
    private Charges charges;

    @Inject
    private Taxes taxRepository;

}
