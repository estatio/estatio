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
package org.estatio.module.charge.fixtures.charges.enums;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.chargegroups.enums.ChargeGroup_enum;
import org.estatio.module.charge.fixtures.charges.builders.ChargeBuilder;
import org.estatio.module.tax.fixtures.data.Tax_enum;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public enum Charge_enum implements PersonaWithBuilderScript<Charge, ChargeBuilder>, PersonaWithFinder<Charge> {

    ItRent ( Country_enum.ITA, ChargeNoCountry_enum.Rent, Tax_enum.IT_VATSTD),
    ItServiceCharge ( Country_enum.ITA, ChargeNoCountry_enum.ServiceCharge, Tax_enum.IT_VATSTD),
    ItServiceCharge2 ( Country_enum.ITA, ChargeNoCountry_enum.ServiceCharge2, Tax_enum.IT_VATSTD),
    ItIncomingCharge1 ( Country_enum.ITA, ChargeNoCountry_enum.IncomingCharge1, Tax_enum.IT_VATSTD),
    ItIncomingCharge2 ( Country_enum.ITA, ChargeNoCountry_enum.IncomingCharge2, Tax_enum.IT_VATSTD),
    ItIncomingCharge3 ( Country_enum.ITA, ChargeNoCountry_enum.IncomingCharge3, Tax_enum.IT_VATSTD),
    ItTurnoverRent ( Country_enum.ITA, ChargeNoCountry_enum.TurnoverRent, Tax_enum.IT_VATSTD),
    ItPercentage ( Country_enum.ITA, ChargeNoCountry_enum.Percentage, Tax_enum.IT_VATSTD),
    ItDeposit ( Country_enum.ITA, ChargeNoCountry_enum.Deposit, Tax_enum.IT_VATSTD),
    ItDiscount ( Country_enum.ITA, ChargeNoCountry_enum.Discount, Tax_enum.IT_VATSTD),
    ItEntryFee ( Country_enum.ITA, ChargeNoCountry_enum.EntryFee, Tax_enum.IT_VATSTD),
    ItTax ( Country_enum.ITA, ChargeNoCountry_enum.Tax, Tax_enum.IT_VATSTD),
    ItServiceChargeIndexable ( Country_enum.ITA, ChargeNoCountry_enum.ServiceChargeIndexable, Tax_enum.IT_VATSTD),
    ItMarketing ( Country_enum.ITA, ChargeNoCountry_enum.Marketing, Tax_enum.IT_VATSTD),

    NlRent ( Country_enum.NLD, ChargeNoCountry_enum.Rent, Tax_enum.NL_VATSTD),
    NlServiceCharge ( Country_enum.NLD, ChargeNoCountry_enum.ServiceCharge, Tax_enum.NL_VATSTD),
    NlServiceCharge2 ( Country_enum.NLD, ChargeNoCountry_enum.ServiceCharge2, Tax_enum.NL_VATSTD),
    NlIncomingCharge1 ( Country_enum.NLD, ChargeNoCountry_enum.IncomingCharge1, Tax_enum.NL_VATSTD),
    NlIncomingCharge2 ( Country_enum.NLD, ChargeNoCountry_enum.IncomingCharge2, Tax_enum.NL_VATSTD),
    NlIncomingCharge3 ( Country_enum.NLD, ChargeNoCountry_enum.IncomingCharge3, Tax_enum.NL_VATSTD),
    NlTurnoverRent ( Country_enum.NLD, ChargeNoCountry_enum.TurnoverRent, Tax_enum.NL_VATSTD),
    NlPercentage ( Country_enum.NLD, ChargeNoCountry_enum.Percentage, Tax_enum.NL_VATSTD),
    NlDeposit ( Country_enum.NLD, ChargeNoCountry_enum.Deposit, Tax_enum.NL_VATSTD),
    NlDiscount ( Country_enum.NLD, ChargeNoCountry_enum.Discount, Tax_enum.NL_VATSTD),
    NlEntryFee ( Country_enum.NLD, ChargeNoCountry_enum.EntryFee, Tax_enum.NL_VATSTD),
    NlTax ( Country_enum.NLD, ChargeNoCountry_enum.Tax, Tax_enum.NL_VATSTD),
    NlServiceChargeIndexable ( Country_enum.NLD, ChargeNoCountry_enum.ServiceChargeIndexable, Tax_enum.NL_VATSTD),
    NlMarketing ( Country_enum.NLD, ChargeNoCountry_enum.Marketing, Tax_enum.NL_VATSTD),

    SeRent ( Country_enum.SWE, ChargeNoCountry_enum.Rent, Tax_enum.SW_VATSTD),
    SeServiceCharge ( Country_enum.SWE, ChargeNoCountry_enum.ServiceCharge, Tax_enum.SW_VATSTD),
    SeServiceCharge2 ( Country_enum.SWE, ChargeNoCountry_enum.ServiceCharge2, Tax_enum.SW_VATSTD),
    SeIncomingCharge1 ( Country_enum.SWE, ChargeNoCountry_enum.IncomingCharge1, Tax_enum.SW_VATSTD),
    SeIncomingCharge2 ( Country_enum.SWE, ChargeNoCountry_enum.IncomingCharge2, Tax_enum.SW_VATSTD),
    SeIncomingCharge3 ( Country_enum.SWE, ChargeNoCountry_enum.IncomingCharge3, Tax_enum.SW_VATSTD),
    SeTurnoverRent ( Country_enum.SWE, ChargeNoCountry_enum.TurnoverRent, Tax_enum.SW_VATSTD),
    SePercentage ( Country_enum.SWE, ChargeNoCountry_enum.Percentage, Tax_enum.SW_VATSTD),
    SeDeposit ( Country_enum.SWE, ChargeNoCountry_enum.Deposit, Tax_enum.SW_VATSTD),
    SeDiscount ( Country_enum.SWE, ChargeNoCountry_enum.Discount, Tax_enum.SW_VATSTD),
    SeEntryFee ( Country_enum.SWE, ChargeNoCountry_enum.EntryFee, Tax_enum.SW_VATSTD),
    SeTax ( Country_enum.SWE, ChargeNoCountry_enum.Tax, Tax_enum.SW_VATSTD),
    SeServiceChargeIndexable ( Country_enum.SWE, ChargeNoCountry_enum.ServiceChargeIndexable, Tax_enum.SW_VATSTD),
    SeMarketing ( Country_enum.SWE, ChargeNoCountry_enum.Marketing, Tax_enum.SW_VATSTD),

    FrRent ( Country_enum.FRA, ChargeNoCountry_enum.Rent, Tax_enum.FR_VATSTD),
    FrServiceCharge ( Country_enum.FRA, ChargeNoCountry_enum.ServiceCharge, Tax_enum.FR_VATSTD),
    FrServiceCharge2 ( Country_enum.FRA, ChargeNoCountry_enum.ServiceCharge2, Tax_enum.FR_VATSTD),
    FrIncomingCharge1 ( Country_enum.FRA, ChargeNoCountry_enum.IncomingCharge1, Tax_enum.FR_VATSTD),
    FrIncomingCharge2 ( Country_enum.FRA, ChargeNoCountry_enum.IncomingCharge2, Tax_enum.FR_VATSTD),
    FrIncomingCharge3 ( Country_enum.FRA, ChargeNoCountry_enum.IncomingCharge3, Tax_enum.FR_VATSTD),
    FrTurnoverRent ( Country_enum.FRA, ChargeNoCountry_enum.TurnoverRent, Tax_enum.FR_VATSTD),
    FrPercentage ( Country_enum.FRA, ChargeNoCountry_enum.Percentage, Tax_enum.FR_VATSTD),
    FrDeposit ( Country_enum.FRA, ChargeNoCountry_enum.Deposit, Tax_enum.FR_VATSTD),
    FrDiscount ( Country_enum.FRA, ChargeNoCountry_enum.Discount, Tax_enum.FR_VATSTD),
    FrEntryFee ( Country_enum.FRA, ChargeNoCountry_enum.EntryFee, Tax_enum.FR_VATSTD),
    FrTax ( Country_enum.FRA, ChargeNoCountry_enum.Tax, Tax_enum.FR_VATSTD),
    FrServiceChargeIndexable ( Country_enum.FRA, ChargeNoCountry_enum.ServiceChargeIndexable, Tax_enum.FR_VATSTD),
    FrMarketing ( Country_enum.FRA, ChargeNoCountry_enum.Marketing, Tax_enum.FR_VATSTD),

    GbRent ( Country_enum.GBR, ChargeNoCountry_enum.Rent, Tax_enum.GB_VATSTD),
    GbServiceCharge ( Country_enum.GBR, ChargeNoCountry_enum.ServiceCharge, Tax_enum.GB_VATSTD),
    GbServiceCharge2 ( Country_enum.GBR, ChargeNoCountry_enum.ServiceCharge2, Tax_enum.GB_VATSTD),
    GbIncomingCharge1 ( Country_enum.GBR, ChargeNoCountry_enum.IncomingCharge1, Tax_enum.GB_VATSTD),
    GbIncomingCharge2 ( Country_enum.GBR, ChargeNoCountry_enum.IncomingCharge2, Tax_enum.GB_VATSTD),
    GbIncomingCharge3 ( Country_enum.GBR, ChargeNoCountry_enum.IncomingCharge3, Tax_enum.GB_VATSTD),
    GbTurnoverRent ( Country_enum.GBR, ChargeNoCountry_enum.TurnoverRent, Tax_enum.GB_VATSTD),
    GbPercentage ( Country_enum.GBR, ChargeNoCountry_enum.Percentage, Tax_enum.GB_VATSTD),
    GbDeposit ( Country_enum.GBR, ChargeNoCountry_enum.Deposit, Tax_enum.GB_VATSTD),
    GbDiscount ( Country_enum.GBR, ChargeNoCountry_enum.Discount, Tax_enum.GB_VATSTD),
    GbEntryFee ( Country_enum.GBR, ChargeNoCountry_enum.EntryFee, Tax_enum.GB_VATSTD),
    GbTax ( Country_enum.GBR, ChargeNoCountry_enum.Tax, Tax_enum.GB_VATSTD),
    GbServiceChargeIndexable( Country_enum.GBR, ChargeNoCountry_enum.ServiceChargeIndexable, Tax_enum.GB_VATSTD),
    GbMarketing ( Country_enum.GBR, ChargeNoCountry_enum.Marketing, Tax_enum.GB_VATSTD);

    private final Country_enum country_d;
    private final ApplicationTenancy_enum applicationTenancy_d;

    private final String chargeSuffix;
    private final ChargeGroup_enum chargeGroup_d;
    private final String namePrefix;
    private final Applicability applicability;
    private final Tax_enum tax_d;

    Charge_enum(
            final Country_enum country_d,
            final ChargeNoCountry_enum chargeNoCountry,
            final Tax_enum tax_d) {
        this.country_d = country_d;
        applicationTenancy_d = country_d.getApplicationTenancy_d();

        chargeSuffix = chargeNoCountry.getChargeSuffix();
        chargeGroup_d = chargeNoCountry.getChargeGroup();
        namePrefix = chargeNoCountry.getDescriptionPrefix();
        applicability = chargeNoCountry.getApplicability();

        this.tax_d = tax_d;
    }

    public String getRef() { return country_d.getRef3() + chargeSuffix; }

    public String getCountry2AlphaCode() {
        return country_d.getApplicationTenancy_d().getPath().substring(1).toUpperCase();
    }

    public String getName() {
        return namePrefix + " (" + getCountry2AlphaCode() + ")";
    }

    @Override
    public Charge findUsing(final ServiceRegistry2 serviceRegistry) {
        final ChargeRepository repository =
                serviceRegistry.lookupService(ChargeRepository.class);
        return repository.findByReference(getRef());
    }


    public ChargeBuilder builder() {
        return new ChargeBuilder()
                .setRef(Charge_enum.this.getRef())
                .setName(Charge_enum.this.getName())
                .setApplicability(applicability)
                .setPrereq((f,ec) -> f.setChargeGroup(f.objectFor(chargeGroup_d, ec)))
                .setPrereq((f,ec) -> f.setApplicationTenancy(f.objectFor(applicationTenancy_d, ec)))
                .setPrereq((f,ec) -> f.setTax(f.objectFor(tax_d, ec)))
                ;
    }

    @Programmatic
    public static class PersistAll extends FixtureScript {

        @Override
        protected void execute(final ExecutionContext executionContext) {
            for (Charge_enum datum : values()) {
                final ChargeBuilder chargeBuilder = datum.builder();
                final Charge charge = executionContext.executeChildT(this, chargeBuilder).getObject();
                executionContext.addResult(this, charge.getReference(), charge);
            }
        }
    }
}
