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

import org.estatio.capex.fixture.orderinvoice.OrderInvoiceFixture;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForCARTEST;
import org.estatio.fixture.asset.PropertyForGraIt;
import org.estatio.fixture.asset.PropertyForHanSe;
import org.estatio.fixture.asset.PropertyForMacFr;
import org.estatio.fixture.asset.PropertyForMnsFr;
import org.estatio.fixture.asset.PropertyForVivFr;
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
import org.estatio.fixture.numerator.NumeratorForOrganisationFra;
import org.estatio.fixture.order.OrderFixture;
import org.estatio.fixture.party.PersonForBrunoTreasurerFr;
import org.estatio.fixture.party.PersonForDylanOfficeAdministratorGb;
import org.estatio.fixture.party.PersonForEmmaTreasurerGb;
import org.estatio.fixture.party.PersonForFaithConwayGb;
import org.estatio.fixture.party.PersonForFifineLacroixFr;
import org.estatio.fixture.party.PersonForGabrielHerveFr;
import org.estatio.fixture.party.PersonForGinoVannelliGb;
import org.estatio.fixture.party.PersonForJonathanPropertyManagerGb;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;
import org.estatio.fixture.party.PersonForOlivePropertyManagerFr;
import org.estatio.fixture.party.PersonForOscarCountryDirectorGb;
import org.estatio.fixture.party.PersonForRosaireEvrardFr;
import org.estatio.fixture.party.PersonForThibaultOfficerAdministratorFr;
import org.estatio.fixture.project.ProjectsForGra;
import org.estatio.fixture.project.ProjectsForKal;
import org.estatio.integtests.capex.TickingFixtureClock;

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
        executionContext.executeChild(this, new PersonForLinusTorvaldsNl());
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
        executionContext.executeChild(this, new PersonForGinoVannelliGb());

        executionContext.executeChild(this, new PropertyForGraIt());
        executionContext.executeChild(this, new PropertyForVivFr());
        executionContext.executeChild(this, new PropertyForHanSe());
        executionContext.executeChild(this, new PropertyForMnsFr());
        executionContext.executeChild(this, new PropertyForMacFr());

        executionContext.executeChild(this, new PersonForDylanOfficeAdministratorGb()); // gb mailroom
        executionContext.executeChild(this, new PersonForJonathanPropertyManagerGb());  // gb property mgr for OXF
        executionContext.executeChild(this, new PersonForFaithConwayGb());  // gb country administrator
        executionContext.executeChild(this, new PersonForOscarCountryDirectorGb());  // gb country director
        executionContext.executeChild(this, new PersonForEmmaTreasurerGb());   // gb treasurer

        executionContext.executeChild(this, new PersonForThibaultOfficerAdministratorFr());  // fr mailroom
        executionContext.executeChild(this, new PersonForFifineLacroixFr());  // fr property mgr for VIV and MNS
        executionContext.executeChild(this, new PersonForOlivePropertyManagerFr());  // fr property mgr for MAC
        executionContext.executeChild(this, new PersonForRosaireEvrardFr());  // fr country administrator
        executionContext.executeChild(this, new PersonForGabrielHerveFr());  // fr country director
        executionContext.executeChild(this, new PersonForBrunoTreasurerFr()); // fr treasurer

        executionContext.executeChild(this, new ProjectsForKal());
        executionContext.executeChild(this, new ProjectsForGra());

        executionContext.executeChild(this, new BudgetsForOxf());
        executionContext.executeChild(this, new KeyTablesForOxf());
        executionContext.executeChild(this, new PartitioningAndItemsForOxf());

        executionContext.executeChild(this, new PropertyForCARTEST());
        executionContext.executeChild(this, new NumeratorForOrganisationFra());

        executionContext.executeChild(this, new CreateInvoiceNumerators());

        executionContext.executeChild(this, new OrderInvoiceFixture());

        executionContext.executeChild(this, new IncomingPdfFixture().setRunAs("estatio-user-fr"));

        executionContext.executeChild(this, new OrderFixture());

        executionContext.executeChild(this, new IncomingInvoiceFixture());

    }

}
