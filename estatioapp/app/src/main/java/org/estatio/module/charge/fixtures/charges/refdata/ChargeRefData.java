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
package org.estatio.module.charge.fixtures.charges.refdata;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.builders.ChargeBuilder;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;

public class ChargeRefData extends FixtureScript {

    public static final String IT_RENT = Charge_enum.ItRent.getRef();
    public static final String IT_SERVICE_CHARGE = Charge_enum.ItServiceCharge.getRef();
    public static final String IT_TURNOVER_RENT = Charge_enum.ItTurnoverRent.getRef();
    public static final String IT_PERCENTAGE = Charge_enum.ItPercentage.getRef();
    public static final String IT_DEPOSIT = Charge_enum.ItDeposit.getRef();
    public static final String IT_DISCOUNT = Charge_enum.ItDiscount.getRef();
    public static final String IT_ENTRY_FEE = Charge_enum.ItEntryFee.getRef();
    public static final String IT_TAX = Charge_enum.ItTax.getRef();
    public static final String IT_SERVICE_CHARGE_INDEXABLE = Charge_enum.ItServiceChargeIndexable.getRef();

    public static final String NL_RENT = Charge_enum.NlRent.getRef();
    public static final String NL_SERVICE_CHARGE = Charge_enum.NlServiceCharge.getRef();
    public static final String NL_SERVICE_CHARGE2 = Charge_enum.NlServiceCharge2.getRef();
    public static final String NL_INCOMING_CHARGE_1 = Charge_enum.NlIncomingCharge1.getRef();
    public static final String NL_INCOMING_CHARGE_2 = Charge_enum.NlIncomingCharge2.getRef();
    public static final String NL_INCOMING_CHARGE_3 = Charge_enum.NlIncomingCharge3.getRef();
    public static final String NL_TURNOVER_RENT = Charge_enum.NlTurnoverRent.getRef();
    public static final String NL_PERCENTAGE = Charge_enum.NlPercentage.getRef();
    public static final String NL_DEPOSIT = Charge_enum.NlDeposit.getRef();
    public static final String NL_DISCOUNT = Charge_enum.NlDiscount.getRef();
    public static final String NL_ENTRY_FEE = Charge_enum.NlEntryFee.getRef();
    public static final String NL_TAX = Charge_enum.NlTax.getRef();
    public static final String NL_SERVICE_CHARGE_INDEXABLE = Charge_enum.NlServiceChargeIndexable.getRef();

    public static final String SE_RENT = Charge_enum.SeRent.getRef();
    public static final String SE_SERVICE_CHARGE = Charge_enum.SeServiceCharge.getRef();
    public static final String SE_TURNOVER_RENT = Charge_enum.SeTurnoverRent.getRef();
    public static final String SE_PERCENTAGE = Charge_enum.SePercentage.getRef();
    public static final String SE_DEPOSIT = Charge_enum.SeDeposit.getRef();
    public static final String SE_DISCOUNT = Charge_enum.SeDiscount.getRef();
    public static final String SE_ENTRY_FEE = Charge_enum.SeEntryFee.getRef();
    public static final String SE_TAX = Charge_enum.SeTax.getRef();
    public static final String SE_SERVICE_CHARGE_INDEXABLE = Charge_enum.SeServiceChargeIndexable.getRef();

    public static final String FR_RENT = Charge_enum.FrRent.getRef();
    public static final String FR_SERVICE_CHARGE = Charge_enum.FrServiceCharge.getRef();
    public static final String FR_SERVICE_CHARGE_ONBUDGET1 = Charge_enum.FrServiceCharge.getRef();
    public static final String FR_SERVICE_CHARGE_ONBUDGET2 = Charge_enum.FrServiceCharge2.getRef();
    public static final String FR_TURNOVER_RENT = Charge_enum.FrTurnoverRent.getRef();
    public static final String FR_PERCENTAGE = Charge_enum.FrPercentage.getRef();
    public static final String FR_DEPOSIT = Charge_enum.FrDeposit.getRef();
    public static final String FR_DISCOUNT = Charge_enum.FrDiscount.getRef();
    public static final String FR_ENTRY_FEE = Charge_enum.FrEntryFee.getRef();
    public static final String FR_TAX = Charge_enum.FrTax.getRef();
    public static final String FR_SERVICE_CHARGE_INDEXABLE = Charge_enum.FrServiceChargeIndexable.getRef();

    public static final String GB_RENT = Charge_enum.GbRent.getRef();
    public static final String GB_SERVICE_CHARGE = Charge_enum.GbServiceCharge.getRef();
    public static final String GB_SERVICE_CHARGE2 = Charge_enum.GbServiceCharge2.getRef();
    public static final String GB_INCOMING_CHARGE_1 = Charge_enum.GbIncomingCharge1.getRef();
    public static final String GB_INCOMING_CHARGE_2 = Charge_enum.GbIncomingCharge2.getRef();
    public static final String GB_INCOMING_CHARGE_3 = Charge_enum.GbIncomingCharge3.getRef();
    public static final String GB_TURNOVER_RENT = Charge_enum.GbTurnoverRent.getRef();
    public static final String GB_PERCENTAGE = Charge_enum.GbPercentage.getRef();
    public static final String GB_DEPOSIT = Charge_enum.GbDeposit.getRef();
    public static final String GB_DISCOUNT = Charge_enum.GbDiscount.getRef();
    public static final String GB_ENTRY_FEE = Charge_enum.GbEntryFee.getRef();
    public static final String GB_TAX = Charge_enum.GbTax.getRef();
    public static final String GB_SERVICE_CHARGE_INDEXABLE = Charge_enum.GbServiceChargeIndexable.getRef();
    public static final String GB_MARKETING = Charge_enum.GbMarketing.getRef();


    @Override
    protected void execute(final ExecutionContext executionContext) {
        createCharges(executionContext);
    }

    private void createCharges(final ExecutionContext executionContext) {

        for (Charge_enum data : Charge_enum.values()) {
            final ChargeBuilder chargeBuilder = data.toFixtureScript();
            final Charge charge = executionContext.executeChildT(this, chargeBuilder).getObject();
            executionContext.addResult(this, charge.getReference(), charge);
        }
    }

}
