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
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.paperclips.PaperclipForDocument;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.integtestsupport.dom.TeardownFixtureAbstract;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.coda.CodaElement;
import org.estatio.capex.dom.coda.CodaMapping;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.paperclips.PaperclipForOrder;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectRole;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannel;
import org.estatio.module.asset.dom.CommunicationChannelOwnerLinkForFixedAsset;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.paperclips.PaperclipForFixedAsset;
import org.estatio.module.asset.dom.registration.FixedAssetRegistration;
import org.estatio.module.registration.dom.LandRegister;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLink;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRun;
import org.estatio.module.budgetassignment.dom.override.BudgetOverride;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideValue;
import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItem;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItemValue;
import org.estatio.module.budgeting.dom.keyitem.KeyItem;
import org.estatio.module.budgeting.dom.keytable.KeyTable;
import org.estatio.module.budgeting.dom.partioning.PartitionItem;
import org.estatio.module.budgeting.dom.partioning.Partitioning;
import org.estatio.module.party.dom.paperclips.PaperclipForParty;
import org.estatio.module.event.dom.Event;
import org.estatio.module.event.dom.EventSourceLink;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.bankaccount.dom.paperclips.PaperclipForBankAccount;
import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceAttribute;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.paperclips.PaperclipForInvoice;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemSource;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.breaks.BreakOption;
import org.estatio.module.lease.dom.breaks.EventSourceLinkForBreakOption;
import org.estatio.module.lease.dom.occupancy.tags.Activity;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.UnitSize;
import org.estatio.module.party.dom.CommunicationChannelOwnerLinkForParty;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationPreviousName;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRegistration;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.relationship.PartyRelationship;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.numerator.dom.Numerator;

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

        deleteFrom(CodaMapping.class);
        deleteFrom(CodaElement.class);

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

        deleteFrom(InvoiceAttribute.class);
        deleteFrom(PaperclipForInvoice.class);

        // these subclasses are rolled up...
//        deleteFrom(InvoiceItemForLease.class);
//        deleteFrom(InvoiceForLease.class);
//
//        deleteFrom(IncomingInvoiceItem.class);
//        deleteFrom(IncomingInvoice.class);

        deleteFrom(PaymentBatchApprovalStateTransition.class);
        deleteFrom(PaymentBatch.class);

        deleteFrom(IncomingInvoiceApprovalStateTransition.class);
        deleteFrom(OrderItemInvoiceItemLink.class);
        deleteFrom(InvoiceItem.class);
        deleteFrom(Invoice.class);

        deleteFrom(OrderApprovalStateTransition.class);
        deleteFrom(PaperclipForOrder.class);
        deleteFrom(OrderItem.class);
        deleteFrom(Order.class);

        deleteFrom(ProjectRole.class);
        deleteFrom(ProjectItem.class);
        deleteFrom(Project.class);

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

        deleteFrom(BankAccountVerificationStateTransition.class);
        deleteFrom(PaperclipForBankAccount.class);
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

        deleteFrom(IncomingDocumentCategorisationStateTransition.class);
        deleteFrom(PaperclipForDocument.class);

        deleteFrom(Task.class);

        deleteFrom(PaperclipForParty.class);
        deleteFrom(OrganisationPreviousName.class);
        deleteFrom(PartyRegistration.class);
        deleteFrom(PartyRelationship.class);
        deleteFrom(PartyRole.class);
        deleteFrom(Organisation.class);
        deleteFrom(Person.class);
        deleteFrom(Party.class);


        deleteFrom(Paperclip.class);
        deleteFrom(Document.class);

        deleteFrom(Numerator.class);

        deleteFrom(CommandJdo.class);
    }


}
