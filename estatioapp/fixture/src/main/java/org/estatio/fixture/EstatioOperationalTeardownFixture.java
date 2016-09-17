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
import javax.jdo.metadata.TypeMetadata;

import com.google.common.base.Strings;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.module.documents.dom.impl.docs.DocumentAbstract;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.links.Paperclip;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.types.DocumentType;

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
import org.estatio.dom.asset.paperclips.PaperclipForFixedAsset;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.budgetassignment.BudgetCalculationLink;
import org.estatio.dom.budgetassignment.ServiceChargeItem;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelOwnerLink;
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
import org.estatio.dom.lease.LeaseItemSource;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.breaks.BreakOption;
import org.estatio.dom.lease.breaks.EventSourceLinkForBreakOption;
import org.estatio.dom.lease.tags.Activity;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Sector;
import org.estatio.dom.lease.tags.UnitSize;
import org.estatio.dom.party.CommunicationChannelOwnerLinkForParty;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationPreviousName;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRegistration;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.project.BusinessCase;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.ProgramRole;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRole;

public class EstatioOperationalTeardownFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteAllDirect();
    }

    protected void deleteAllDirect() {

        deleteFrom(ProgramRole.class);
        deleteFrom(ProjectRole.class);
        deleteFrom(BusinessCase.class);
        deleteFrom(Project.class);
        deleteFrom(Program.class);

        deleteFrom(BudgetCalculationLink.class);
        deleteFrom(BudgetCalculation.class);
        deleteFrom(ServiceChargeItem.class);
        deleteFrom(BudgetItemAllocation.class);
        deleteFrom(BudgetItem.class);
        deleteFrom(KeyItem.class);
        deleteFrom(KeyTable.class);
        deleteFrom(Budget.class);

        deleteFrom(Numerator.class);

        deleteFrom(PaperclipForFixedAsset.class);
        deleteFrom(Paperclip.class);

        deleteFrom(DocumentTemplate.class);
        deleteFrom(DocumentAbstract.class);
        deleteFrom(DocumentType.class);
        deleteFrom(RenderingStrategy.class);

        deleteFrom(InvoiceItem.class);
        deleteFrom(Invoice.class);

        deleteFrom(EventSourceLinkForBreakOption.class);
        deleteFrom(EventSourceLink.class);
        deleteFrom(Event.class);

        deleteFrom(BreakOption.class);
        deleteFrom(LeaseItemSource.class);
        deleteFrom(LeaseTerm.class);
        deleteFrom(LeaseItemSource.class);
        deleteFrom(LeaseItem.class);
        deleteFrom(Occupancy.class);

        deleteFrom(AgreementRoleCommunicationChannel.class);
        deleteFrom(AgreementRole.class);

        deleteFrom(Activity.class);
        deleteFrom(Brand.class);
        deleteFrom(Sector.class);
        deleteFrom(UnitSize.class);

        deleteFrom(Guarantee.class);
        deleteFrom(Lease.class);
        deleteFrom(LeaseType.class);
        deleteFrom(BankMandate.class);

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
        deleteFrom(FixedAssetRegistration.class);
        deleteFrom(FixedAsset.class);

        deleteFrom(OrganisationPreviousName.class);
        deleteFrom(PartyRegistration.class);
        deleteFrom(PartyRelationship.class);
        deleteFrom(Organisation.class);
        deleteFrom(Person.class);
        deleteFrom(Party.class);

        deleteFrom(Numerator.class);
    }

    protected void deleteFrom(final Class cls) {
        preDeleteFrom(cls);
        final TypeMetadata metadata = isisJdoSupport.getJdoPersistenceManager().getPersistenceManagerFactory()
                .getMetadata(cls.getName());
        if(metadata == null) {
            // fall-back
            deleteFrom(cls.getSimpleName());
        } else {
            final String schema = metadata.getSchema();
            String table = metadata.getTable();
            if(Strings.isNullOrEmpty(table)) {
                table = cls.getSimpleName();
            }
            if(Strings.isNullOrEmpty(schema)) {
                deleteFrom(table);
            } else {
                deleteFrom(schema, table);
            }
        }
        postDeleteFrom(cls);
    }

    protected Integer deleteFrom(final String schema, final String table) {
        return isisJdoSupport.executeUpdate(String.format("DELETE FROM \"%s\".\"%s\"", schema, table));
    }

    protected void deleteFrom(final String table) {
        isisJdoSupport.executeUpdate(String.format("DELETE FROM \"%s\"", table));
    }

    protected void preDeleteFrom(final Class cls) {}

    protected void postDeleteFrom(final Class cls) {}

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
