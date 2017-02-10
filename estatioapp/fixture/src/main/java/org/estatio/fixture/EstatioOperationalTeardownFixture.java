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

import org.isisaddons.module.command.dom.CommandJdo;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink;
import org.incode.module.communications.dom.impl.comms.CommChannelRole;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.paperclips.PaperclipForCommunication;
import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.paperclips.PaperclipForDocument;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.integtestsupport.dom.TeardownFixtureAbstract;

import org.estatio.agreement.dom.Agreement;
import org.estatio.agreement.dom.AgreementRole;
import org.estatio.agreement.dom.AgreementRoleCommunicationChannel;
import org.estatio.asset.dom.CommunicationChannelOwnerLinkForFixedAsset;
import org.estatio.asset.dom.FixedAsset;
import org.estatio.asset.dom.FixedAssetRole;
import org.estatio.asset.dom.Property;
import org.estatio.asset.dom.Unit;
import org.estatio.asset.dom.registration.LandRegister;
import org.estatio.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.asset.dom.paperclips.PaperclipForFixedAsset;
import org.estatio.asset.dom.registration.FixedAssetRegistration;
import org.estatio.bankmandate.dom.BankMandate;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultLink;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideValue;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemValue;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.dom.event.Event;
import org.estatio.dom.event.EventSourceLink;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.paperclips.PaperclipForInvoice;
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
import org.estatio.dom.document.paperclips.PaperclipForParty;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.project.BusinessCase;
import org.estatio.dom.project.Program;
import org.estatio.dom.project.ProgramRole;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRole;
import org.estatio.numerator.dom.impl.Numerator;

public class EstatioOperationalTeardownFixture extends TeardownFixtureAbstract {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteAllDirect();
    }

    @Override
    protected void preDeleteFrom(final Class cls) {
        if(cls == FixedAssetRegistration.class) {
            deleteFrom(LandRegister.class);
        }
    }

    protected void deleteAllDirect() {

        deleteFrom(ProgramRole.class);
        deleteFrom(ProjectRole.class);
        deleteFrom(BusinessCase.class);
        deleteFrom(Project.class);
        deleteFrom(Program.class);

        deleteFrom(BudgetCalculationResultLink.class);
        deleteFrom(BudgetCalculationResult.class);
        deleteFrom(BudgetCalculationRun.class);
        deleteFrom(BudgetOverrideValue.class);
        deleteFrom(BudgetOverride.class);
        deleteFrom(BudgetCalculation.class);
        deleteFrom(PartitionItem.class);
        deleteFrom(Partitioning.class);
        deleteFrom(BudgetItemValue.class);
        deleteFrom(BudgetItem.class);
        deleteFrom(KeyItem.class);
        deleteFrom(KeyTable.class);
        deleteFrom(Budget.class);

        deleteFrom(PaperclipForInvoice.class);
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

        deleteFrom(PaperclipForCommunication.class);
        deleteFrom(CommChannelRole.class); // ie communication correspondent
        deleteFrom(Communication.class);

        deleteFrom(CommunicationChannelOwnerLinkForFixedAsset.class);
        deleteFrom(CommunicationChannelOwnerLinkForParty.class);
        deleteFrom(CommunicationChannelOwnerLink.class);
        deleteFrom(CommunicationChannel.class);

        deleteFrom(PaperclipForFixedAsset.class);
        deleteFrom(Unit.class);
        deleteFrom(Property.class);
        deleteFrom(FixedAssetRole.class);
        deleteFrom(FixedAssetRegistration.class);
        deleteFrom(FixedAsset.class);

        deleteFrom(PaperclipForParty.class);
        deleteFrom(OrganisationPreviousName.class);
        deleteFrom(PartyRegistration.class);
        deleteFrom(PartyRelationship.class);
        deleteFrom(Organisation.class);
        deleteFrom(Person.class);
        deleteFrom(Party.class);

        deleteFrom(PaperclipForDocument.class);
        deleteFrom(Paperclip.class);
        deleteFrom(Applicability.class);
        deleteFrom(Document.class);
        deleteFrom(DocumentTemplate.class);
        deleteFrom(DocumentAbstract.class);
        deleteFrom(DocumentType.class);
        deleteFrom(RenderingStrategy.class);

        deleteFrom(Numerator.class);

        deleteFrom(CommandJdo.class);
    }


}
