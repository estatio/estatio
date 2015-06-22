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
package org.estatio.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.estatio.dom.JdoColumnLength.Numerator;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannel;
import org.estatio.dom.asset.CommunicationChannelOwnerLinkForFixedAsset;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccount;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.LandRegister;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelOwnerLink;
import org.estatio.dom.document.Document;
import org.estatio.dom.event.Event;
import org.estatio.dom.event.EventSourceLink;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.breaks.BreakOption;
import org.estatio.dom.lease.breaks.EventSourceLinkForBreakOption;
import org.estatio.dom.party.CommunicationChannelOwnerLinkForParty;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRegistration;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.project.BusinessCase;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.ProgramRole;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRole;
import org.estatio.dom.tag.Tag;

public class EstatioOperationalTeardownFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteAllDirect();
    }

    private void deleteAllDirect() {

        deleteFrom(ProgramRole.class);
        deleteFrom(ProjectRole.class);
        deleteFrom(BusinessCase.class);
        deleteFrom(Project.class);
        deleteFrom(Program.class);

        deleteFrom(Numerator.class);

        deleteFrom(Document.class);

        deleteFrom(InvoiceItem.class);
        deleteFrom(Invoice.class);

        deleteFrom(Tag.class);

        deleteFrom(EventSourceLinkForBreakOption.class);
        deleteFrom(EventSourceLink.class);
        deleteFrom(Event.class);

        deleteFrom(BreakOption.class);
        deleteFrom(LeaseTerm.class);
        deleteFrom(LeaseItem.class);
        deleteFrom(Occupancy.class);

        deleteFrom(AgreementRoleCommunicationChannel.class);
        deleteFrom(AgreementRole.class);

        deleteFrom(Guarantee.class);
        deleteFrom(BankMandate.class);
        deleteFrom(Lease.class);

        deleteFrom(FinancialAccountTransaction.class);
        deleteFrom(BankAccount.class);
        deleteFrom(FixedAssetFinancialAccount.class);
        deleteFrom(FinancialAccount.class);

        deleteFrom(Agreement.class);

        deleteFrom(CommunicationChannelOwnerLinkForFixedAsset.class);
        deleteFrom(CommunicationChannelOwnerLinkForParty.class);
        deleteFrom(CommunicationChannelOwnerLink.class);
        deleteFrom(CommunicationChannel.class);

        deleteFrom(Unit.class);
        deleteFrom(Property.class);
        deleteFrom(FixedAssetRole.class);
        deleteFrom(LandRegister.class);
        deleteFrom(FixedAssetRegistration.class);
        deleteFrom(FixedAsset.class);

        deleteFrom(PartyRegistration.class);
        deleteFrom(PartyRelationship.class);
        deleteFrom(Organisation.class);
        deleteFrom(Person.class);
        deleteFrom(Party.class);
    }

    private void deleteFrom(final Class cls) {
        deleteFrom(cls.getSimpleName());

    }

    private void deleteFrom(final String table) {
        isisJdoSupport.executeUpdate("DELETE FROM " + "\"" + table + "\"");
    }

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
