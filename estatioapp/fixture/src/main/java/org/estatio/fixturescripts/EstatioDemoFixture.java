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
package org.estatio.fixturescripts;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForDylanOfficeAdministratorGb;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForFaithConwayGb;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForJonathanPropertyManagerGb;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForGraIt;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForMnsFr;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForVivFr;
import org.estatio.module.capex.fixtures.orderinvoice.OrderInvoiceFixture;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForCARTEST;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForHanSe;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForMacFr;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.budget.KeyTablesForOxf;
import org.estatio.fixture.budget.PartitioningAndItemsForOxf;
import org.estatio.fixture.documents.incoming.IncomingPdfFixture;
import org.estatio.fixture.financial.BankAccountAndMandateForPoisonNl;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.financial.BankAccountForAcmeNl;
import org.estatio.fixture.financial.BankAccountForHelloWorldGb;
import org.estatio.fixture.financial.BankAccountForHelloWorldNl;
import org.estatio.fixture.financial.BankAccountForMediaXGb;
import org.estatio.fixture.financial.BankAccountForMiracleGb;
import org.estatio.fixture.financial.BankAccountForPretGb;
import org.estatio.fixture.financial.BankAccountForTopModelGb;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001Gb;
import org.estatio.fixture.invoice.IncomingInvoiceFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease.LeaseForOxfPret004Gb;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.module.party.fixtures.numerator.personas.NumeratorForOrganisationFra;
import org.estatio.fixture.order.OrderFixture;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForBrunoTreasurerFr;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForEmmaTreasurerGb;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForFifineLacroixFr;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForGabrielHerveFr;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForGinoVannelliGb;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForLinusTorvaldsNl;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForOlivePropertyManagerFr;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForOscarCountryDirectorGb;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForRosaireEvrardFr;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForThibaultOfficerAdministratorFr;
import org.estatio.module.application.fixtures.project.personas.ProjectsForGra;
import org.estatio.module.application.fixtures.project.personas.ProjectsForKal;
import org.estatio.module.base.platform.applib.TickingFixtureClock;

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
        executionContext.executeChild(this, new EstatioBaseLineFixture());
        executionContext.executeChild(this, new PersonAndRolesForLinusTorvaldsNl());
        executionContext.executeChild(this, new BankAccountForAcmeNl());
        executionContext.executeChild(this, new BankAccountForHelloWorldNl());
        executionContext.executeChild(this, new BankAccountForHelloWorldGb());
        executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
        executionContext.executeChild(this, new BankAccountForMediaXGb());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());
        executionContext.executeChild(this, new BankAccountForPretGb());
        executionContext.executeChild(this, new LeaseForOxfPret004Gb());
        executionContext.executeChild(this, new BankAccountForMiracleGb());
        executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005Gb());
        executionContext.executeChild(this, new BankAccountAndMandateForPoisonNl());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005());
        executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
        executionContext.executeChild(this, new BankAccountForTopModelGb());
        executionContext.executeChild(this, new PersonAndRolesForGinoVannelliGb());

        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForGraIt());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForVivFr());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForHanSe());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForMnsFr());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForMacFr());

        executionContext.executeChild(this, new PersonAndRolesForDylanOfficeAdministratorGb()); // gb mailroom
        executionContext.executeChild(this, new PersonAndRolesForJonathanPropertyManagerGb());  // gb property mgr for OXF
        executionContext.executeChild(this, new PersonAndRolesForFaithConwayGb());  // gb country administrator
        executionContext.executeChild(this, new PersonAndRolesForOscarCountryDirectorGb());  // gb country director
        executionContext.executeChild(this, new PersonAndRolesForEmmaTreasurerGb());   // gb treasurer

        executionContext.executeChild(this, new PersonAndRolesForThibaultOfficerAdministratorFr());  // fr mailroom
        executionContext.executeChild(this, new PersonAndRolesForFifineLacroixFr());  // fr property mgr for VIV and MNS
        executionContext.executeChild(this, new PersonAndRolesForOlivePropertyManagerFr());  // fr property mgr for MAC
        executionContext.executeChild(this, new PersonAndRolesForRosaireEvrardFr());  // fr country administrator
        executionContext.executeChild(this, new PersonAndRolesForGabrielHerveFr());  // fr country director
        executionContext.executeChild(this, new PersonAndRolesForBrunoTreasurerFr()); // fr treasurer

        executionContext.executeChild(this, new ProjectsForKal());
        executionContext.executeChild(this, new ProjectsForGra());

        executionContext.executeChild(this, new BudgetsForOxf());
        executionContext.executeChild(this, new KeyTablesForOxf());
        executionContext.executeChild(this, new PartitioningAndItemsForOxf());

        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForCARTEST());
        executionContext.executeChild(this, new NumeratorForOrganisationFra());

        executionContext.executeChild(this, new CreateInvoiceNumerators());

        executionContext.executeChild(this, new OrderInvoiceFixture());

        executionContext.executeChild(this, new IncomingPdfFixture().setRunAs("estatio-user-fr"));

        executionContext.executeChild(this, new OrderFixture());

        executionContext.executeChild(this, new IncomingInvoiceFixture());

    }

}
