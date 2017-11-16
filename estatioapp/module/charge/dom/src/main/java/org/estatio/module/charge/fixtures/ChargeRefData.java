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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeGroupRepository;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

public class ChargeRefData extends FixtureScript {

    private static final String CHARGE_SUFFIX_RENT = "_RENT";
    private static final String CHARGE_SUFFIX_MARKETING = "_MARKETING";
    private static final String CHARGE_SUFFIX_SERVICE_CHARGE = "_SERVICE_CHARGE";
    private static final String CHARGE_SUFFIX_SERVICE_CHARGE2 = "_SERVICE_CHARGE2";
    private static final String CHARGE_SUFFIX_INCOMING_CHARGE_1 = "_INCOMING_CHARGE_1";
    private static final String CHARGE_SUFFIX_INCOMING_CHARGE_2 = "_INCOMING_CHARGE_2";
    private static final String CHARGE_SUFFIX_INCOMING_CHARGE_3 = "_INCOMING_CHARGE_3";
    private static final String CHARGE_SUFFIX_TURNOVER_RENT = "_TURNOVER_RENT";
    private static final String CHARGE_SUFFIX_PERCENTAGE = "_PERCENTAGE";
    private static final String CHARGE_SUFFIX_DEPOSIT = "_DEPOSIT";
    private static final String CHARGE_SUFFIX_DISCOUNT = "_DISCOUNT";
    private static final String CHARGE_SUFFIX_ENTRY_FEE = "_ENTRY_FEE";
    private static final String CHARGE_SUFFIX_TAX = "_TAX";
    private static final String CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE = "_SVC_CHG_INDEXABLE";

    public static final String IT_RENT = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_RENT;
    public static final String IT_SERVICE_CHARGE = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String IT_TURNOVER_RENT = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String IT_PERCENTAGE = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_PERCENTAGE;
    public static final String IT_DEPOSIT = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_DEPOSIT;
    public static final String IT_DISCOUNT = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_DISCOUNT;
    public static final String IT_ENTRY_FEE = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String IT_TAX = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_TAX;
    public static final String IT_SERVICE_CHARGE_INDEXABLE = Country_enum.ITA.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String NL_RENT = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_RENT;
    public static final String NL_SERVICE_CHARGE = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String NL_SERVICE_CHARGE2 = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE2;
    public static final String NL_INCOMING_CHARGE_1 = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_1;
    public static final String NL_INCOMING_CHARGE_2 = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_2;
    public static final String NL_INCOMING_CHARGE_3 = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_3;
    public static final String NL_TURNOVER_RENT = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String NL_PERCENTAGE = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_PERCENTAGE;
    public static final String NL_DEPOSIT = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_DEPOSIT;
    public static final String NL_DISCOUNT = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_DISCOUNT;
    public static final String NL_ENTRY_FEE = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String NL_TAX = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_TAX;
    public static final String NL_SERVICE_CHARGE_INDEXABLE = Country_enum.NLD.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String SE_RENT = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_RENT;
    public static final String SE_SERVICE_CHARGE = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String SE_TURNOVER_RENT = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String SE_PERCENTAGE = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_PERCENTAGE;
    public static final String SE_DEPOSIT = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_DEPOSIT;
    public static final String SE_DISCOUNT = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_DISCOUNT;
    public static final String SE_ENTRY_FEE = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String SE_TAX = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_TAX;
    public static final String SE_SERVICE_CHARGE_INDEXABLE = Country_enum.SWE.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String FR_RENT = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_RENT;
    public static final String FR_SERVICE_CHARGE = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String FR_SERVICE_CHARGE_ONBUDGET1 = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_1;
    public static final String FR_SERVICE_CHARGE_ONBUDGET2 = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_2;
    public static final String FR_TURNOVER_RENT = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String FR_PERCENTAGE = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_PERCENTAGE;
    public static final String FR_DEPOSIT = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_DEPOSIT;
    public static final String FR_DISCOUNT = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_DISCOUNT;
    public static final String FR_ENTRY_FEE = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String FR_TAX = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_TAX;
    public static final String FR_SERVICE_CHARGE_INDEXABLE = Country_enum.FRA.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;

    public static final String GB_RENT = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_RENT;
    public static final String GB_SERVICE_CHARGE = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE;
    public static final String GB_SERVICE_CHARGE2 = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE2;
    public static final String GB_INCOMING_CHARGE_1 = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_1;
    public static final String GB_INCOMING_CHARGE_2 = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_2;
    public static final String GB_INCOMING_CHARGE_3 = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_INCOMING_CHARGE_3;
    public static final String GB_TURNOVER_RENT = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_TURNOVER_RENT;
    public static final String GB_PERCENTAGE = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_PERCENTAGE;
    public static final String GB_DEPOSIT = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_DEPOSIT;
    public static final String GB_DISCOUNT = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_DISCOUNT;
    public static final String GB_ENTRY_FEE = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_ENTRY_FEE;
    public static final String GB_TAX = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_TAX;
    public static final String GB_SERVICE_CHARGE_INDEXABLE = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE;
    public static final String GB_MARKETING = Country_enum.GBR.getRef3() + CHARGE_SUFFIX_MARKETING;


    @Override
    protected void execute(final ExecutionContext executionContext) {
        createCharges(executionContext);
    }

    private void createCharges(final ExecutionContext executionContext) {

        final ChargeGroup chargeGroupRent = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_RENT);
        final ChargeGroup chargeGroupServiceCharge = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_SERVICE_CHARGE);
        final ChargeGroup chargeGroupTurnoverRent = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_TURNOVER_RENT);
        final ChargeGroup chargeGroupPercentage = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_PERCENTAGE);
        final ChargeGroup chargeGroupDeposit = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_DEPOSIT);
        final ChargeGroup chargeGroupDiscount = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_DISCOUNT);
        final ChargeGroup chargeGroupEntryFee = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_ENTRY_FEE);
        final ChargeGroup chargeGroupTax = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_TAX);
        final ChargeGroup chargeGroupServiceChargeIndexable = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_SERVICE_CHARGE_INDEXABLE);
        final ChargeGroup chargeGroupMarketing = chargeGroupRepository.findChargeGroup(ChargeGroupRefData.REF_MARKETING);

        final List<ApplicationTenancy> countryTenancies = estatioApplicationTenancyRepository.allCountryTenancies();

        for (final ApplicationTenancy countryTenancy : countryTenancies) {

            final String country2AlphaCode = countryTenancy.getPath().substring(1).toUpperCase();
            final String countryName = " (" + country2AlphaCode + ")";

            final String taxReference = vatStdFor(country2AlphaCode);

            createCharge(chargeGroupRent, country2AlphaCode + CHARGE_SUFFIX_RENT,
                    "Rent" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);
            createCharge(chargeGroupServiceCharge, country2AlphaCode + CHARGE_SUFFIX_SERVICE_CHARGE,
                    "Service Charge 1" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);
            createCharge(chargeGroupServiceCharge, country2AlphaCode + CHARGE_SUFFIX_SERVICE_CHARGE2,
                    "Service Charge 2" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);

            createCharge(chargeGroupServiceCharge, country2AlphaCode + CHARGE_SUFFIX_INCOMING_CHARGE_1,
                    "Incoming Charge 1" + countryName,
                    taxReference, Applicability.INCOMING, executionContext);
            createCharge(chargeGroupServiceCharge, country2AlphaCode + CHARGE_SUFFIX_INCOMING_CHARGE_2,
                    "Incoming Charge 2" + countryName,
                    taxReference, Applicability.INCOMING, executionContext);
            createCharge(chargeGroupServiceCharge, country2AlphaCode + CHARGE_SUFFIX_INCOMING_CHARGE_3,
                    "Incoming Charge 3" + countryName,
                    taxReference, Applicability.INCOMING, executionContext);

            createCharge(chargeGroupTurnoverRent, country2AlphaCode + CHARGE_SUFFIX_TURNOVER_RENT,
                    "Turnover Rent" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);
            createCharge(chargeGroupPercentage, country2AlphaCode + CHARGE_SUFFIX_PERCENTAGE,
                    "Percentage" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);
            createCharge(chargeGroupDeposit, country2AlphaCode + CHARGE_SUFFIX_DEPOSIT,
                    "Deposit" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);
            createCharge(chargeGroupDiscount, country2AlphaCode + CHARGE_SUFFIX_DISCOUNT,
                    "Discount" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);
            createCharge(chargeGroupEntryFee, country2AlphaCode + CHARGE_SUFFIX_ENTRY_FEE,
                    "Entry Fee" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);
            createCharge(chargeGroupServiceChargeIndexable, country2AlphaCode + CHARGE_SUFFIX_SERVICE_CHARGE_INDEXABLE,
                    "Service Charge Indexable" + countryName,
                    taxReference, Applicability.OUTGOING, executionContext);

            createCharge(chargeGroupTax, country2AlphaCode + CHARGE_SUFFIX_TAX,
                    "Tax" + countryName,
                    taxReference, Applicability.IN_AND_OUT, executionContext);
            createCharge(chargeGroupMarketing, country2AlphaCode + CHARGE_SUFFIX_MARKETING,
                    "Marketing" + countryName,
                    taxReference, Applicability.IN_AND_OUT, executionContext);
        }
    }

    public static final String vatStdFor(final String country2AlphaCode) {
        return country2AlphaCode.toUpperCase() + "-VATSTD";
    }

    private ChargeGroup createChargeGroup(
            final String chargeGroupReference,
            final String description,
            final ExecutionContext executionContext) {
        final ChargeGroup chargeGroup = chargeGroupRepository.createChargeGroup(
                chargeGroupReference, description);
        return executionContext.addResult(this, chargeGroup.getReference(), chargeGroup);
    }

    private Charge createCharge(
            final ChargeGroup chargeGroup,
            final String chargeReference,
            final String description,
            final String taxReference,
            final Applicability applicability,
            final ExecutionContext executionContext) {

        final String code = chargeReference;

        final Tax tax = taxRepository.findByReference(taxReference);
        final ApplicationTenancy taxApplicationTenancy = tax.getApplicationTenancy();

        final Charge charge = chargeRepository.upsert(
                chargeReference, description, code, taxApplicationTenancy, applicability, tax, chargeGroup);

        return executionContext.addResult(this, charge.getReference(), charge);
    }

    // //////////////////////////////////////

    @Inject
    private ChargeGroupRepository chargeGroupRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private TaxRepository taxRepository;

    @Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

}
