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
package org.estatio.module.application.demos;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.seed.scripts.AbstractUserAndRolesFixtureScript;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccountFaFa_enum;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.base.platform.applib.TickingFixtureClock;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;
import org.estatio.module.budget.fixtures.partitioning.personas.PartitioningAndItemsForOxfBudget2015;
import org.estatio.module.capex.fixtures.IncomingInvoiceFixture;
import org.estatio.module.capex.fixtures.OrderFixture;
import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.capex.fixtures.document.IncomingPdfFixture;
import org.estatio.module.capex.fixtures.orderinvoice.OrderInvoiceFixture;
import org.estatio.module.capex.fixtures.project.personas.ProjectForGra;
import org.estatio.module.capex.fixtures.project.personas.ProjectsForKal;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.EstatioChargeModule;
import org.estatio.module.country.IncodeDomCountryModule;
import org.estatio.module.currency.EstatioCurrencyModule;
import org.estatio.module.guarantee.fixtures.personas.GuaranteeForOxfTopModel001Gb;
import org.estatio.module.index.EstatioIndexModule;
import org.estatio.module.lease.fixtures.DocFragmentDemoFixture;
import org.estatio.module.lease.fixtures.bankaccount.personas.BankAccountAndMandateForPoisonNl;
import org.estatio.module.lease.fixtures.bankaccount.personas.BankAccountAndMandateForTopModelGb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.module.lease.fixtures.invoicing.personas.InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005;
import org.estatio.module.lease.fixtures.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.module.lease.fixtures.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.deposits.personas.LeaseItemAndLeaseTermForDepositForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.discount.personas.LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.percentage.personas.LeaseItemAndLeaseTermForPercentageForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentOf2ForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas.LeaseItemAndLeaseTermForServiceChargeOf2ForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.personas.LeaseItemAndLeaseTermForTurnoverRentForOxfMiracl005Gb;
import org.estatio.module.lease.migrations.CreateInvoiceNumerators;
import org.estatio.module.lease.seed.DocFragmentSeedFixture;
import org.estatio.module.lease.seed.DocumentTypesAndTemplatesForLeaseFixture;
import org.estatio.module.party.fixtures.numerator.personas.NumeratorForOrganisationFra;
import org.estatio.module.tax.EstatioTaxModule;

import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Global;

public class EstatioDemoFixture extends DiscoverableFixtureScript {

    public EstatioDemoFixture() {
        this(null, "demo");
    }

    public EstatioDemoFixture(final String friendlyName, final String name) {
        super(friendlyName, name);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        TickingFixtureClock.replaceExisting();
        doExecute(executionContext);
    }

    private void doExecute(final ExecutionContext executionContext) {

        final AbstractUserAndRolesFixtureScript initialisationUser =
                new AbstractUserAndRolesFixtureScript(
                        "initialisation", "pass", null,
                        Global.getPath(), AccountType.LOCAL,
                        Lists.newArrayList("estatio-admin")) {
                };
        executionContext.executeChild(this, "'initialisation' user", initialisationUser);
        executionContext.executeChild(this, "countries", new IncodeDomCountryModule().getRefDataSetupFixture());
        executionContext.executeChild(this, "currencies", new EstatioCurrencyModule().getRefDataSetupFixture());
        executionContext.executeChild(this, "taxes", new EstatioTaxModule().getRefDataSetupFixture());
        executionContext.executeChild(this, "incomingCharges", new EstatioChargeModule().getRefDataSetupFixture());
        executionContext.executeChild(this, "indices", new EstatioIndexModule().getRefDataSetupFixture());

        executionContext.executeChild(this, new DocFragmentDemoFixture());
        executionContext.executeChild(this, new DocFragmentSeedFixture());
        executionContext.executeChild(this, Person_enum.LinusTorvaldsNl.toBuilderScript());

        executionContext.executeChild(this, BankAccount_enum.AcmeNl.toBuilderScript());
        executionContext.executeChild(this, BankAccountFaFa_enum.AcmeNl.toBuilderScript());

        executionContext.executeChild(this, BankAccount_enum.HelloWorldNl.toBuilderScript());
        executionContext.executeChild(this, BankAccountFaFa_enum.HelloWorldNl.toBuilderScript());
        executionContext.executeChild(this, BankAccount_enum.HelloWorldGb.toBuilderScript());
        executionContext.executeChild(this, BankAccountFaFa_enum.HelloWorldGb.toBuilderScript());
        executionContext.executeChild(this, BankAccount_enum.TopModelGb.toBuilderScript());
        executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
        executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.toBuilderScript());
        executionContext.executeChild(this, BankAccount_enum.MediaXGb.toBuilderScript());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());
        executionContext.executeChild(this, Lease_enum.OxfPret004Gb.toBuilderScript());
        executionContext.executeChild(this, BankAccount_enum.PretGb.toBuilderScript());
        executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb.toBuilderScript());
        executionContext.executeChild(this, BankAccount_enum.MiracleGb.toBuilderScript());

        executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentOf2ForOxfMiracl005Gb());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForServiceChargeOf2ForOxfMiracl005Gb());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForTurnoverRentForOxfMiracl005Gb());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForPercentageForOxfMiracl005Gb());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForDepositForOxfMiracl005Gb());

        executionContext.executeChild(this, Lease_enum.KalPoison001Nl.toBuilderScript());
        executionContext.executeChild(this, new BankAccountAndMandateForPoisonNl());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005());
        executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
        executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toBuilderScript());
        executionContext.executeChild(this, BankAccount_enum.TopModelGb.toBuilderScript());
        executionContext.executeChild(this, Person_enum.GinoVannelliGb.toBuilderScript());

        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.GraIt.toBuilderScript());
        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.VivFr.toBuilderScript());
        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.HanSe.toBuilderScript());
        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.MnsFr.toBuilderScript());
        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.MacFr.toBuilderScript());

        executionContext.executeChild(this, Person_enum.DylanOfficeAdministratorGb.toBuilderScript()); // gb mailroom
        executionContext.executeChild(this, Person_enum.JonathanPropertyManagerGb.toBuilderScript());  // gb property mgr for OXF
        executionContext.executeChild(this, Person_enum.FaithConwayGb.toBuilderScript());  // gb country administrator
        executionContext.executeChild(this, Person_enum.OscarCountryDirectorGb.toBuilderScript());  // gb country director
        executionContext.executeChild(this, Person_enum.EmmaTreasurerGb.toBuilderScript());   // gb treasurer
        executionContext.executeChild(this, Person_enum.ThibaultOfficerAdministratorFr.toBuilderScript());  // fr mailroom
        executionContext.executeChild(this, Person_enum.FifineLacroixFr.toBuilderScript());  // fr property mgr for VIV and MNS
        executionContext.executeChild(this, Person_enum.OlivePropertyManagerFr.toBuilderScript());  // fr property mgr for MAC
        executionContext.executeChild(this, Person_enum.RosaireEvrardFr.toBuilderScript());  // fr country administrator
        executionContext.executeChild(this, Person_enum.GabrielHerveFr.toBuilderScript());  // fr country director
        executionContext.executeChild(this, Person_enum.BrunoTreasurerFr.toBuilderScript()); // fr treasurer

        executionContext.executeChild(this, new ProjectsForKal());
        executionContext.executeChild(this, new ProjectForGra());

        executionContext.executeChild(this, Budget_enum.OxfBudget2015.toBuilderScript());
        executionContext.executeChild(this, Budget_enum.OxfBudget2016.toBuilderScript());
        executionContext.executeChild(this, KeyTable_enum.Oxf2015Area.toBuilderScript());
        executionContext.executeChild(this, KeyTable_enum.Oxf2015Count.toBuilderScript());

        executionContext.executeChild(this, new PartitioningAndItemsForOxfBudget2015());

        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.CARTEST.toBuilderScript());
        executionContext.executeChild(this, new NumeratorForOrganisationFra());

        executionContext.executeChild(this, new CreateInvoiceNumerators());

        executionContext.executeChild(this, new IncomingChargeFixture());
        executionContext.executeChild(this, new OrderInvoiceFixture());

        executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
        executionContext.executeChild(this, new DocumentTypesAndTemplatesForLeaseFixture());
        executionContext.executeChild(this, new IncomingPdfFixture().setRunAs("estatio-user-fr"));

        executionContext.executeChild(this, new OrderFixture());

        executionContext.executeChild(this, new IncomingInvoiceFixture());

    }

}
