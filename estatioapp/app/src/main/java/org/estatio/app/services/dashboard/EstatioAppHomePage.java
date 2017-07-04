/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.app.services.dashboard;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Ordering;

import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.PaymentBatchMenu;
import org.estatio.capex.dom.payment.PaymentBatchRepository;
import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.capex.dom.payment.manager.PaymentBatchManager;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
import org.estatio.capex.dom.task.Task_checkState;
import org.estatio.dom.event.Event;
import org.estatio.dom.event.EventRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.dashboard.EstatioAppHomePage"
)
public class EstatioAppHomePage {

    private static final int MONTHS = 3;

    public String title() {
        return "Home Page";
    }


    ////////////////////////////////////////////////

    @Collection(notPersisted = true)
    public List<Task> getTasksForMe() {

        List<Task> tasksForMe =
                queryResultsCache.execute(
                        this::doGetTasksForMe, EstatioAppHomePage.class, "getTasksForMe");

        return tasksForMe;
    }

    private List<Task> doGetTasksForMe() {
        return taskRepository.findIncompleteForMe();
    }


    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-question-circle" // override isis-non-changing.properties
    )
    public EstatioAppHomePage checkStateOfTasksForMe() {
        return checkStateOf(getTasksForMe());
    }


    ////////////////////////////////////////////////

    @Collection(notPersisted = true)
    public List<Task> getTasksForOthers() {
        final List<Task> tasksIncomplete = taskRepository.findIncompleteForOthers();
        return tasksIncomplete;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-question-circle" // override isis-non-changing.properties
    )
    public EstatioAppHomePage checkStateOfTasksForOthers() {
        return checkStateOf(getTasksForOthers());
    }


    ////////////////////////////////////////////////

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesNew() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.NEW);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesCompleted() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.COMPLETED);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesApproved() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.APPROVED);
    }

    public List<IncomingInvoice> getIncomingInvoicesPendingBankAccountCheck() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
    }


    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesPayableByTransferNotInBatch() {
        return incomingInvoiceRepository.findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod(
                                                IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.BANK_TRANSFER);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesPayableByDirectDebit() {
        return incomingInvoiceRepository.findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.DIRECT_DEBIT);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesPayableByManualProcess() {
        return incomingInvoiceRepository.findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.MANUAL_PROCESS);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesPayableByOther() {
        final List<IncomingInvoice> invoices = Lists.newArrayList(
                incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.PAYABLE) );

        final List<IncomingInvoice> byDirectDebit = getIncomingInvoicesPayableByDirectDebit();
        final List<IncomingInvoice> byTransfer = getIncomingInvoicesPayableAndBankTransfer();
        final List<IncomingInvoice> byManualProcess = getIncomingInvoicesPayableByManualProcess();

        invoices.removeAll(byDirectDebit);
        invoices.removeAll(byTransfer);
        invoices.removeAll(byManualProcess);

        return invoices;
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesInNewBatch() {
        final List<PaymentBatch> newBatches = paymentBatchRepository.findNewBatches();
        return findIncomingInvoicesWithin(newBatches);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesInCompletedBatch() {
        final List<PaymentBatch> completedBatches = paymentBatchRepository.findCompletedBatches();
        return findIncomingInvoicesWithin(completedBatches);
    }

    private List<IncomingInvoice> findIncomingInvoicesWithin(final List<PaymentBatch> batches) {
        final List<IncomingInvoice> invoices = Lists.newArrayList();
        for (PaymentBatch completedBatch : batches) {
            invoices.addAll(
                    Lists.newArrayList(completedBatch.getLines())
                            .stream()
                            .map(PaymentLine::getInvoice)
                            .collect(Collectors.toList()));
        }
        invoices.sort(Ordering.natural().nullsLast().onResultOf(IncomingInvoice::getInvoiceDate));
        return invoices;
    }

    private List<IncomingInvoice> getIncomingInvoicesPayableAndBankTransfer() {
        return incomingInvoiceRepository.findByApprovalStateAndPaymentMethod(
                IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.BANK_TRANSFER);
    }

    ////////////////////////////////////////////////////////////

    @Collection(notPersisted = true)
    public List<Lease> getLeasesAboutToExpire() {
        return leaseRepository.findExpireInDateRange(clockService.now(), clockService.now().plusMonths(MONTHS));
    }

    @Collection(notPersisted = true)
    public List<Event> getUpcomingEvents() {
        return eventRepository.findEventsInDateRange(clockService.now(), clockService.now().plusMonths(MONTHS));
    }




    private EstatioAppHomePage checkStateOf(final List<Task> tasks) {
        for (Task task : tasks) {
            factoryService.mixin(Task_checkState.class, task).act();
        }
        return this;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TableColumnOrderServiceForIncomingInvoices implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object parent,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            if(parent instanceof EstatioAppHomePage && IncomingInvoice.class.isAssignableFrom(collectionType)) {
                return Lists.newArrayList("seller", "property", "grossAmount", "invoiceDate", "number");
            }
            return null;
        }

        @Override
        public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
            return null;
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-magic")
    public PaymentBatchManager preparePaymentBatches() {
        return paymentBatchMenu.preparePaymentBatches();
    }

    @Inject
    PaymentBatchMenu paymentBatchMenu;

    @Inject
    FactoryService factoryService;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PaymentBatchRepository paymentBatchRepository;

    @Inject
    TaskRepository taskRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    ClockService clockService;

}
