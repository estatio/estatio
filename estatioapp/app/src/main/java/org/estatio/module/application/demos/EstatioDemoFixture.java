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

import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.fixtures.TickingFixtureClock;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.seed.scripts.AbstractUserAndRolesFixtureScript;

import org.incode.module.country.CountryModule;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.assetfinancial.fixtures.enums.BankAccountFaFa_enum;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.keytables.enums.KeyTable_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.fixtures.order.enums.NumeratorForOrder_enum;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.fixtures.orderinvoice.OrderInvoiceImportForDemoXlsxFixture;
import org.estatio.module.capex.fixtures.project.enums.ProjectItemTerm_enum;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.EstatioChargeModule;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;
import org.estatio.module.currency.EstatioCurrencyModule;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.guarantee.fixtures.enums.Guarantee_enum;
import org.estatio.module.index.EstatioIndexModule;
import org.estatio.module.lease.fixtures.bankaccount.enums.BankMandate_enum;
import org.estatio.module.lease.fixtures.breakoptions.enums.BreakOption_enum;
import org.estatio.module.lease.fixtures.docfrag.enums.DocFragment_demo_enum;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRentFixed_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.seed.DocFragment_enum;
import org.estatio.module.lease.seed.DocumentTypesAndTemplatesForLeaseFixture;
import org.estatio.module.party.fixtures.numerator.enums.NumeratorForOrganisation_enum;
import org.estatio.module.party.fixtures.roles.enums.PartyRole_enum;
import org.estatio.module.tax.EstatioTaxModule;

import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.Global;

@DomainObject(
        objectType = "org.estatio.module.application.demos.EstatioDemoFixture"
)
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

    private void doExecute(final ExecutionContext ec) {

        ec.executeChild(this, new AbstractUserAndRolesFixtureScript(
                "initialisation", "pass",
                null, // email address
                Global.getPath(), AccountType.LOCAL,
                Lists.newArrayList("estatio-admin")) {
        });

        Stream.of(
                new CountryModule(),
                new EstatioCurrencyModule(),
                new EstatioTaxModule(),
                new EstatioChargeModule(),
                new EstatioIndexModule()
        )
                .map(ModuleAbstract::getRefDataSetupFixture)
                .filter(Objects::nonNull)
                .forEach(fixtureScript -> ec.executeChild(this, fixtureScript));

        ec.executeChildren(this,
                DocFragment_demo_enum.InvoicePreliminaryLetterDescription_DemoGbr,
                DocFragment_demo_enum.InvoicePreliminaryLetterDescription_DemoNld,
                DocFragment_demo_enum.InvoiceDescription_DemoGbr,
                DocFragment_demo_enum.InvoiceDescription_DemoNld,
                DocFragment_demo_enum.InvoiceItemDescription_DemoGbr,
                DocFragment_demo_enum.InvoiceItemDescription_DemoNld
        );

        ec.executeChildren(this,
                DocFragment_enum.InvoiceDescriptionFra,
                DocFragment_enum.InvoiceDescriptionIta,
                DocFragment_enum.InvoiceItemDescriptionFra,
                DocFragment_enum.InvoiceItemDescriptionIta,
                DocFragment_enum.InvoicePreliminaryLetterDescriptionFra,
                DocFragment_enum.InvoicePreliminaryLetterDescriptionIta
        );

        ec.executeChildren(this,
                Person_enum.LinusTorvaldsNl,
                Person_enum.GinoVannelliGb,
                Person_enum.DylanOfficeAdministratorGb,
                Person_enum.DanielOfficeAdministratorFr,
                Person_enum.JonathanIncomingInvoiceManagerGb,
                Person_enum.BertrandIncomingInvoiceManagerFr,
                Person_enum.FaithConwayGb,
                Person_enum.OscarCountryDirectorGb,
                Person_enum.EmmaTreasurerGb,
                Person_enum.BrunoTreasurerFr,
                Person_enum.DanielOfficeAdministratorFr,
                Person_enum.FifineLacroixFr,
                Person_enum.OlivePropertyManagerFr,
                Person_enum.RosaireEvrardFr,
                Person_enum.GabrielCountryDirectorFr,
                Person_enum.BrunoTreasurerFr);

        ec.executeChildren(this,
                PropertyAndUnitsAndOwnerAndManager_enum.GraIt,
                PropertyAndUnitsAndOwnerAndManager_enum.VivFr,
                PropertyAndUnitsAndOwnerAndManager_enum.HanSe,
                PropertyAndUnitsAndOwnerAndManager_enum.MnsFr,
                PropertyAndUnitsAndOwnerAndManager_enum.MacFr,
                PropertyAndUnitsAndOwnerAndManager_enum.RonIt);

        ec.executeChildren(this,
                NumeratorForOrganisation_enum.Fra);

        ec.executeChildren(this,
                NumeratorForOrder_enum.ItaScopedToHelloWorldIt);

        ec.executeChildren(this,
                PartyRole_enum.HelloWorldIt_as_ECP);

        ec.executeChildren(this,
                BankAccount_enum.AcmeNl,
                BankAccount_enum.HelloWorldGb,
                BankAccount_enum.TopModelGb,
                BankAccount_enum.TopSellerGb,
                BankAccount_enum.TopModelFr,
                BankAccount_enum.TopSellerFr,
                BankAccount_enum.MediaXGb,
                BankAccount_enum.PretGb,
                BankAccount_enum.MiracleGb,
                BankAccount_enum.HelloWorldNl,
                BankAccountFaFa_enum.AcmeNl);

        ec.executeChildren(this,
                BankAccountFaFa_enum.HelloWorldNl,
                BankAccountFaFa_enum.HelloWorldGb);

        ec.executeChildren(this,
                BankMandate_enum.OxfTopModel001Gb_1,
                BankMandate_enum.KalPoison001Nl_2);

        ec.executeChildren(this,
                Guarantee_enum.OxfTopModel001Gb);

        ec.executeChildren(this,
                Lease_enum.OxfMediaX002Gb,
                Lease_enum.OxfPret004Gb,
                Lease_enum.OxfMiracl005Gb,
                Lease_enum.KalPoison001Nl,
                Lease_enum.OxfTopModel001Gb,
                Lease_enum.HanOmsHyral003Se,
                Lease_enum.RonTopModel001It);

        ec.executeChildren(this,
                BreakOption_enum.OxfPoison003Gb_FIXED,
                BreakOption_enum.OxfPoison003Gb_ROLLING,
                BreakOption_enum.OxfPoison003Gb_FIXED,
                BreakOption_enum.OxfPoison003Gb_ROLLING,
                BreakOption_enum.OxfTopModel001Gb_FIXED,
                BreakOption_enum.OxfTopModel001Gb_ROLLING);

        ec.executeChildren(this,
                LeaseItemForRent_enum.OxfMiracl005Gb,
                LeaseItemForServiceCharge_enum.OxfMiracl005Gb,
                LeaseItemForTurnoverRent_enum.OxfMiracl005Gb,
                LeaseItemForDiscount_enum.OxfMiracle005bGb,
                LeaseItemForDeposit_enum.OxfMiracle005bGb,
                LeaseItemForRent_enum.KalPoison001Nl,
                LeaseItemForRent_enum.OxfTopModel001Gb,
                LeaseItemForServiceCharge_enum.OxfTopModel001Gb,
                LeaseItemForTurnoverRent_enum.OxfTopModel001Gb,
                LeaseItemForDiscount_enum.OxfTopModel001Gb,
                LeaseItemForDeposit_enum.OxfTopModel001Gb,
                LeaseItemForTax_enum.OxfTopModel001Gb,
                LeaseItemForMarketing_enum.OxfTopModel001Gb,
                LeaseItemForRent_enum.HanPoison001Se,
                LeaseItemForRent_enum.HanTopModel002Se,
                LeaseItemForTurnoverRentFixed_enum.HanPoison001Se,
                LeaseItemForTurnoverRentFixed_enum.HanTopModel002Se,
                LeaseItemForRent_enum.RonTopModel001It,
                LeaseItemForTax_enum.RonTopModel001It);

        ec.executeChildren(this,
                InvoiceForLease_enum.OxfPoison003Gb,
                InvoiceForLease_enum.KalPoison001Nl,
                InvoiceForLease_enum.OxfMiracl005Gb);


        ec.executeChildren(this,
                Budget_enum.OxfBudget2015,
                Budget_enum.OxfBudget2016);

        ec.executeChildren(this,
                KeyTable_enum.Oxf2015Area,
                KeyTable_enum.Oxf2015Count);

        ec.executeChildren(this,
                Partitioning_enum.OxfPartitioning2015);

        ec.executeChild(this,
                new IncomingChargesFraXlsxFixture());
        ec.executeChild(this,
                new IncomingChargesItaXlsxFixture());
        ec.executeChild(this,
                new OrderInvoiceImportForDemoXlsxFixture());

        ec.executeChildren(this,
                Project_enum.KalProject1,
                Project_enum.KalProject2,
                Project_enum.GraProject,
                Project_enum.VivProjectFr,
                Project_enum.RonProjectIt);

        ec.executeChildren(this,
                ProjectItemTerm_enum.RonProjectItem1Term1,
                ProjectItemTerm_enum.RonProjectItem1Term2,
                ProjectItemTerm_enum.RonProjectItem2Term1,
                ProjectItemTerm_enum.RonProjectItem2Term2,
                ProjectItemTerm_enum.RonProjectItem2Term3,
                ProjectItemTerm_enum.RonProjectItem2Term4
                );

        ec.executeChildren(this,
                new DocumentTypesAndTemplatesForCapexFixture(),
                new DocumentTypesAndTemplatesForLeaseFixture());

        ec.executeChildren(this,
                IncomingPdf_enum.FakeOrder1.builder().setRunAs("estatio-user-fr"),
                IncomingPdf_enum.FakeInvoice1.builder().setRunAs("estatio-user-fr"));

        ec.executeChildren(this,
                Order_enum.fakeOrder2Pdf,
                Order_enum.italianOrder,
                Order_enum.italianOrder4112,
                IncomingInvoice_enum.fakeInvoice2Pdf,
                IncomingInvoice_enum.fakeInvoice3Pdf);

    }

}
