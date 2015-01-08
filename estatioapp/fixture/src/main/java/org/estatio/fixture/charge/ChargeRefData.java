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

import java.util.List;
import javax.inject.Inject;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.estatio.dom.apptenancy.EstatioApplicationTenancies;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.tax.TaxRefData;

public class ChargeRefData extends EstatioFixtureScript {

    private static final String CHARGE_SUFFIX_RENT = "_RENT";
    private static final String CHARGE_SUFFIX_SERVICE_CHARGE = "_SERVICE_CHARGE";
    private static final String CHARGE_SUFFIX_TURNOVER_RENT = "_TURNOVER_RENT";
    private static final String CHARGE_SUFFIX_DISCOUNT = "_DISCOUNT";
    private static final String CHARGE_SUFFIX_ENTRY_FEE = "_ENTRY_FEE";
    private static final String CHARGE_SUFFIX_TAX = "_TAX";
    private static final String CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE = "_SVC_CHG_INDEXABLE";

    public static final String IT_RENT = CountriesRefData.ITA_2 + CHARGE_SUFFIX_RENT;
    public static final String IT_SERVICE_CHARGE = CountriesRefData.ITA_2 + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String IT_TURNOVER_RENT = CountriesRefData.ITA_2 + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String IT_DISCOUNT = CountriesRefData.ITA_2 + CHARGE_SUFFIX_DISCOUNT;
    public static final String IT_ENTRY_FEE = CountriesRefData.ITA_2 + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String IT_TAX = CountriesRefData.ITA_2 + CHARGE_SUFFIX_TAX;
    public static final String IT_SERVICE_CHARGE_INDEXABLE = CountriesRefData.ITA_2 + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String NL_RENT = CountriesRefData.NLD_2 + CHARGE_SUFFIX_RENT;
    public static final String NL_SERVICE_CHARGE = CountriesRefData.NLD_2 + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String NL_TURNOVER_RENT = CountriesRefData.NLD_2 + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String NL_DISCOUNT = CountriesRefData.NLD_2 + CHARGE_SUFFIX_DISCOUNT;
    public static final String NL_ENTRY_FEE = CountriesRefData.NLD_2 + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String NL_TAX = CountriesRefData.NLD_2 + CHARGE_SUFFIX_TAX;
    public static final String NL_SERVICE_CHARGE_INDEXABLE = CountriesRefData.NLD_2 + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String SE_RENT = CountriesRefData.SWE_2 + CHARGE_SUFFIX_RENT;
    public static final String SE_SERVICE_CHARGE = CountriesRefData.SWE_2 + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String SE_TURNOVER_RENT = CountriesRefData.SWE_2 + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String SE_DISCOUNT = CountriesRefData.SWE_2 + CHARGE_SUFFIX_DISCOUNT;
    public static final String SE_ENTRY_FEE = CountriesRefData.SWE_2 + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String SE_TAX = CountriesRefData.SWE_2 + CHARGE_SUFFIX_TAX;
    public static final String SE_SERVICE_CHARGE_INDEXABLE = CountriesRefData.SWE_2 + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String FR_RENT = CountriesRefData.FRA_2 + CHARGE_SUFFIX_RENT;
    public static final String FR_SERVICE_CHARGE = CountriesRefData.FRA_2 + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String FR_TURNOVER_RENT = CountriesRefData.FRA_2 + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String FR_DISCOUNT = CountriesRefData.FRA_2 + CHARGE_SUFFIX_DISCOUNT;
    public static final String FR_ENTRY_FEE = CountriesRefData.FRA_2 + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String FR_TAX = CountriesRefData.FRA_2 + CHARGE_SUFFIX_TAX;
    public static final String FR_SERVICE_CHARGE_INDEXABLE = CountriesRefData.FRA_2 + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String GB_RENT = CountriesRefData.GBR_2 + CHARGE_SUFFIX_RENT;
    public static final String GB_SERVICE_CHARGE = CountriesRefData.GBR_2 + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String GB_TURNOVER_RENT = CountriesRefData.GBR_2 + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String GB_DISCOUNT = CountriesRefData.GBR_2 + CHARGE_SUFFIX_DISCOUNT;
    public static final String GB_ENTRY_FEE = CountriesRefData.GBR_2 + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String GB_TAX = CountriesRefData.GBR_2 + CHARGE_SUFFIX_TAX;
    public static final String GB_SERVICE_CHARGE_INDEXABLE = CountriesRefData.GBR_2 + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    @Override
    protected void execute(final ExecutionContext executionContext) {
        createCharges(executionContext);
    }

    private void createCharges(final ExecutionContext executionContext) {

        final ChargeGroup chargeGroupRent = chargeGroups.findChargeGroup(ChargeGroupRefData.REF_RENT);
        final ChargeGroup chargeGroupServiceCharge = chargeGroups.findChargeGroup(ChargeGroupRefData.REF_SERVICE_CHARGE);
        final ChargeGroup chargeGroupTurnoverRent = chargeGroups.findChargeGroup(ChargeGroupRefData.REF_TURNOVER_RENT);
        final ChargeGroup chargeGroupDiscount = chargeGroups.findChargeGroup(ChargeGroupRefData.REF_DISCOUNT);
        final ChargeGroup chargeGroupEntryFee = chargeGroups.findChargeGroup(ChargeGroupRefData.REF_ENTRY_FEE);
        final ChargeGroup chargeGroupTax = chargeGroups.findChargeGroup(ChargeGroupRefData.REF_TAX);
        final ChargeGroup chargeGroupServiceChargeIndexable = chargeGroups.findChargeGroup(ChargeGroupRefData.REF_SERVICE_CHARGE_INDEXABLE);

        final List<ApplicationTenancy> countryTenancies = estatioApplicationTenancies.allCountryTenancies();

        for (final ApplicationTenancy countryTenancy : countryTenancies) {

            final String countryAbbrev = countryTenancy.getPath().substring(1).toUpperCase();
            final String countryName = " (" + countryAbbrev + ")";

            createCharge(chargeGroupRent, countryAbbrev + CHARGE_SUFFIX_RENT,
                    "Rent" + countryName,
                    TaxRefData.IT_VATSTD, executionContext);
            createCharge(chargeGroupServiceCharge, countryAbbrev + CHARGE_SUFFIX_SERVICE_CHARGE,
                    "Service Charge" + countryName,
                    TaxRefData.IT_VATSTD, executionContext);
            createCharge(chargeGroupTurnoverRent, countryAbbrev + CHARGE_SUFFIX_TURNOVER_RENT,
                    "Turnover Rent" + countryName,
                    TaxRefData.IT_VATSTD, executionContext);
            createCharge(chargeGroupDiscount, countryAbbrev + CHARGE_SUFFIX_DISCOUNT,
                    "Discount" + countryName,
                    TaxRefData.IT_VATSTD, executionContext);
            createCharge(chargeGroupEntryFee, countryAbbrev + CHARGE_SUFFIX_ENTRY_FEE,
                    "Entry Fee" + countryName,
                    TaxRefData.IT_VATSTD, executionContext);
            createCharge(chargeGroupTax, countryAbbrev + CHARGE_SUFFIX_TAX,
                    "Tax" + countryName,
                    TaxRefData.IT_VATSTD, executionContext);
            createCharge(chargeGroupServiceChargeIndexable, countryAbbrev + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE,
                    "Service Charge Indexable" + countryName,
                    TaxRefData.IT_VATSTD, executionContext);
        }
    }

    private ChargeGroup createChargeGroup(
            final String chargeGroupReference,
            final String description,
            final ExecutionContext executionContext) {
        final ChargeGroup chargeGroup = chargeGroups.createChargeGroup(
                chargeGroupReference, description);
        return executionContext.addResult(this, chargeGroup.getReference(), chargeGroup);
    }

    private Charge createCharge(
            final ChargeGroup chargeGroup,
            final String chargeReference,
            final String description,
            final String taxReference,
            final ExecutionContext executionContext) {

        final String code = chargeReference;

        final Tax tax = taxes.findByReference(taxReference);
        final ApplicationTenancy taxApplicationTenancy = tax.getApplicationTenancy();

        final Charge charge = charges.newCharge(
                taxApplicationTenancy, chargeReference, description, code, tax, chargeGroup);

        charge.setGroup(chargeGroup);
        return executionContext.addResult(this, charge.getReference(), charge);
    }

    // //////////////////////////////////////

    @Inject
    private ChargeGroups chargeGroups;

    @Inject
    private Charges charges;

    @Inject
    private Taxes taxes;

    @Inject
    private EstatioApplicationTenancies estatioApplicationTenancies;


}
