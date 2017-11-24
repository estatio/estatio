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
package org.estatio.module.charge.fixtures;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.base.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeGroupRepository;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.tax.dom.TaxRepository;

public class ChargeGroupRefData extends FixtureScript {

    public static final String REF_RENT = "RENT";
    public static final String REF_SERVICE_CHARGE = "SERVICE_CHARGE";
    public static final String REF_TURNOVER_RENT = "TURNOVER_RENT";
    public static final String REF_PERCENTAGE = "RENTAL_FEE";
    public static final String REF_DEPOSIT = "DEPOSIT";
    public static final String REF_DISCOUNT = "DISCOUNT";
    public static final String REF_ENTRY_FEE = "ENTRY_FEE";
    public static final String REF_TAX = "TAX";
    public static final String REF_SERVICE_CHARGE_INDEXABLE = "SERVICE_CHARGE_INDEXABLE";
    public static final String REF_MARKETING = "MARKETING";


    @Override
    protected void execute(final ExecutionContext executionContext) {
        createCharges(executionContext);
    }

    private void createCharges(final ExecutionContext executionContext) {

        createChargeGroup(REF_RENT, "Rent", executionContext);
        createChargeGroup(REF_SERVICE_CHARGE, "Service Charge", executionContext);
        createChargeGroup(REF_TURNOVER_RENT, "Turnover Rent", executionContext);
        createChargeGroup(REF_PERCENTAGE, "Rental Fee", executionContext);
        createChargeGroup(REF_DEPOSIT, "Deposit", executionContext);
        createChargeGroup(REF_DISCOUNT, "Discount", executionContext);
        createChargeGroup(REF_ENTRY_FEE, "Entry Fee", executionContext);
        createChargeGroup(REF_TAX, "Tax", executionContext);
        createChargeGroup(REF_SERVICE_CHARGE_INDEXABLE, "Service Charge Indexable", executionContext);
        createChargeGroup(REF_MARKETING, "Marketing", executionContext);

    }

    private ChargeGroup createChargeGroup(
            final String chargeGroupReference,
            final String description,
            final ExecutionContext executionContext) {
        final ChargeGroup chargeGroup = chargeGroupRepository.upsert(
                chargeGroupReference, description);
        return executionContext.addResult(this, chargeGroup.getReference(), chargeGroup);
    }


    // //////////////////////////////////////

    @Inject
    private ChargeGroupRepository chargeGroupRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private TaxRepository taxRepository;

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

}
