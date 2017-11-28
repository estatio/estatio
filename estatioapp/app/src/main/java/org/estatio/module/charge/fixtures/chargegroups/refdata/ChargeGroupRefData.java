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
package org.estatio.module.charge.fixtures.chargegroups.refdata;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.base.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.module.charge.dom.ChargeGroupRepository;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.chargegroups.enums.ChargeGroup_enum;
import org.estatio.module.tax.dom.TaxRepository;

public class ChargeGroupRefData extends FixtureScript {

    public static final String REF_RENT = ChargeGroup_enum.Rent.getRef();
    public static final String REF_SERVICE_CHARGE = ChargeGroup_enum.ServiceCharge.getRef();
    public static final String REF_TURNOVER_RENT = ChargeGroup_enum.TurnoverRent.getRef();
    public static final String REF_PERCENTAGE = ChargeGroup_enum.Percentage.getRef();
    public static final String REF_DEPOSIT = ChargeGroup_enum.Deposit.getRef();
    public static final String REF_DISCOUNT = ChargeGroup_enum.Discount.getRef();
    public static final String REF_ENTRY_FEE = ChargeGroup_enum.EntryFee.getRef();
    public static final String REF_TAX = ChargeGroup_enum.Tax.getRef();
    public static final String REF_SERVICE_CHARGE_INDEXABLE = ChargeGroup_enum.ServiceChargeIndexable.getRef();
    public static final String REF_MARKETING = ChargeGroup_enum.Marketing.getRef();


    @Override
    protected void execute(final ExecutionContext executionContext) {
        createCharges(executionContext);
    }

    private void createCharges(final ExecutionContext executionContext) {

        final ChargeGroup_enum[] data = ChargeGroup_enum.values();
        for (final ChargeGroup_enum datum : data) {
            executionContext.executeChild(this, datum.toFixtureScript());
        }
    }

    @Inject
    private ChargeGroupRepository chargeGroupRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private TaxRepository taxRepository;

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

}
