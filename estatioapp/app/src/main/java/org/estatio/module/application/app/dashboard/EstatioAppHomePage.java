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
package org.estatio.module.application.app.dashboard;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.estatio.module.capex.app.DirectDebitsMenu;
import org.estatio.module.capex.app.PaymentBatchMenu;
import org.estatio.module.capex.app.UpcomingPaymentFraService;
import org.estatio.module.capex.app.invoice.UpcomingPaymentTotal;
import org.estatio.module.capex.app.paydd.DirectDebitsFraManager;
import org.estatio.module.capex.app.paymentbatch.PaymentBatchFraManager;
import org.estatio.module.capex.app.taskreminder.TaskOverview;
import org.estatio.module.capex.app.taskreminder.TaskReminderService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentBatchRepository;
import org.estatio.module.capex.dom.payment.PaymentLine;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.dom.task.Task_checkState;
import org.estatio.module.coda.contributions.IncomingInvoice_codaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;
import org.estatio.module.event.dom.Event;
import org.estatio.module.event.dom.EventRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

import static org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE;
import static org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository.AT_PATHS_ITA_OFFICE;

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

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public EstatioAppHomePage assignTasksToMe(final List<Task> unassignedTasks) {
        for (Task unassignedTask : unassignedTasks) {
            unassignedTask.setPersonAssignedTo(personRepository.me());
        }

        return this;
    }

    public List<Task> choices0AssignTasksToMe() {
        return taskRepository.findIncompleteForMyRolesAndUnassigned();
    }

    public String disableAssignTasksToMe() {
        if (personRepository.me() == null) {
            return "No Person set up for current user";
        }
        return choices0AssignTasksToMe().isEmpty() ? "No tasks to assign" : null;
    }

    @Collection(notPersisted = true)
    public List<TaskOverview> getAssignedTasksPerPerson() {
        return taskReminderService.getTaskOverviews();
    }

    public EstatioAppHomePage sendReminder(final Person recipient) {
        taskReminderService.getTaskOverviewForPerson(recipient).sendReminder();
        return this;
    }

    public String disableSendReminder() {
        return choices0SendReminder().isEmpty() ? "There are no persons with overdue tasks" : null;
    }

    public List<Person> choices0SendReminder() {
        return getAssignedTasksPerPerson().stream()
                .filter(to -> !to.getListOfTasksOverdue().isEmpty())
                .map(TaskOverview::getPerson)
                .collect(Collectors.toList());
    }

    public String validateSendReminder(final Person recipient) {
        return taskReminderService.disableSendReminder(recipient, taskReminderService.getTaskOverviewForPerson(recipient).getListOfTasksOverdue());
    }

    ////////////////////////////////////////////////

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraNew() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_FRA_OFFICE,IncomingInvoiceApprovalState.NEW);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraCompleted() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_FRA_OFFICE,IncomingInvoiceApprovalState.COMPLETED);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraApproved() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.APPROVED);
    }

    public List<IncomingInvoice> getIncomingInvoicesFraPendingBankAccountCheck() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraPayableByTransferNotInBatch() {
        return incomingInvoiceRepository.findNotInAnyPaymentBatchByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.BANK_TRANSFER);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraPayableByDirectDebit() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.DIRECT_DEBIT);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraPayableByManualProcess() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.MANUAL_PROCESS);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraPayableByOther() {
        final List<IncomingInvoice> invoices = Lists.newArrayList(
                incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                        AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAYABLE));

        final List<IncomingInvoice> byDirectDebit = getIncomingInvoicesFraPayableByDirectDebit();
        final List<IncomingInvoice> byTransfer = getIncomingInvoicesFraPayableAndBankTransfer();
        final List<IncomingInvoice> byManualProcess = getIncomingInvoicesFraPayableByManualProcess();

        invoices.removeAll(byDirectDebit);
        invoices.removeAll(byTransfer);
        invoices.removeAll(byManualProcess);

        return invoices;
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraInNewBatch() {
        final List<PaymentBatch> newBatches = paymentBatchRepository.findNewBatches();
        return findIncomingInvoicesWithin(newBatches);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesFraInCompletedBatch() {
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

    private List<IncomingInvoice> getIncomingInvoicesFraPayableAndBankTransfer() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.BANK_TRANSFER);
    }

    ////////////////////////////////////////////////

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaNew() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_ITA_OFFICE, IncomingInvoiceApprovalState.NEW);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaCompleted() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.COMPLETED);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaPendingAdvise() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.PENDING_ADVISE);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaApprovedByCenterManager() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaApproved() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.APPROVED);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaPendingCodaBooks() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoiceWhenPayableBankTransfer> getIncomingInvoicesItaPayableBankTransfer() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.BANK_TRANSFER).stream()
                .map(incomingInvoice -> {
                    final CodaDocHead codaDocHead = factoryService.mixin(
                            IncomingInvoice_codaDocHead.class, incomingInvoice).prop();
                    return new IncomingInvoiceWhenPayableBankTransfer(incomingInvoice, codaDocHead);
                })
                .collect(Collectors.toList());
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaPayableDirectDebit() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL, PaymentMethod.DIRECT_DEBIT);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaPayableManualProcess() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL, PaymentMethod.MANUAL_PROCESS);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaPayableOther() {
        List<PaymentMethod> paymentMethods = Lists.newArrayList(PaymentMethod.values());
        paymentMethods.remove(PaymentMethod.BANK_TRANSFER);
        paymentMethods.remove(PaymentMethod.DIRECT_DEBIT);
        paymentMethods.remove(PaymentMethod.MANUAL_PROCESS);
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethods(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.PAYABLE, paymentMethods);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesItaSuspended() {
        return incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(
                AT_PATHS_ITA_OFFICE,IncomingInvoiceApprovalState.SUSPENDED);
    }


    ////////////////////////////////////////////////////////////


    @Collection
    @CollectionLayout(defaultView = "table")
    public List<CodaDocHead> getInvalidAndUnpaidCodaDocumentsIta() {
        return codaDocHeadRepository.findUnpaidAndInvalid().stream().sorted(Comparator.comparing(CodaDocHead::getInputDate).reversed()).collect(Collectors.toList());
    }

    ////////////////////////////////////////////////////////////

    @Collection(notPersisted = true)
    public List<Lease> getLeasesAboutToExpire() {
        final LocalDate now = clockService.now();
        return leaseRepository.findExpireInDateRange(now, now.plusMonths(MONTHS));
    }

    @Collection(notPersisted = true)
    public List<Event> getUpcomingEvents() {
        final LocalDate now = clockService.now();
        return eventRepository.findEventsInDateRange(now, now.plusMonths(MONTHS));
    }

    @Collection(notPersisted = true)
    public List<UpcomingPaymentTotal> getUpcomingPaymentsFra() {
        return upcomingPaymentFraService.getUpcomingPaymentsFra();
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
            if (parent instanceof EstatioAppHomePage && IncomingInvoice.class.isAssignableFrom(collectionType)) {
                return collectionId.contains("Ita") ?
                        Lists.newArrayList("seller", "property", "grossAmount", "dateReceived", "codaDocHead") :
                        Lists.newArrayList("seller", "property", "grossAmount", "dateReceived", "number");
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
    public PaymentBatchFraManager openPaymentBatchManagerFra() {
        return paymentBatchMenu.preparePaymentBatchesFra();
    }

    @Inject
    PaymentBatchMenu paymentBatchMenu;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-check-square-o")
    public DirectDebitsFraManager openDirectDebitManagerFra() {
        return directDebitsMenu.directDebitFraManager();
    }

    @Inject DirectDebitsMenu directDebitsMenu;

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

    @Inject
    PersonRepository personRepository;

    @Inject
    UpcomingPaymentFraService upcomingPaymentFraService;

    @Inject
    TaskReminderService taskReminderService;

    @Inject
    CodaDocHeadRepository codaDocHeadRepository;


}
