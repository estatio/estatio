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

import java.util.Collections;
import java.util.List;

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
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
import org.estatio.capex.dom.task.Task_checkState;
import org.estatio.dom.event.Event;
import org.estatio.dom.event.EventRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.party.PersonRepository;

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
                queryResultsCache.execute(this::doGetTasksForMe, EstatioAppHomePage.class, "getTasksForMe");

        sort(tasksForMe);
        return tasksForMe;
    }

    private List<Task> doGetTasksForMe() {
        return taskRepository.findTasksIncompleteForMe();
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
        final List<Task> tasksIncomplete = taskRepository.findTasksIncompleteForOthers();
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

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesApprovedByCountryDirector() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesPendingBankAccountCheck() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
    }

    @Collection(notPersisted = true)
    public List<IncomingInvoice> getIncomingInvoicesPayable() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.PAYABLE);
    }




    @Collection(notPersisted = true)
    public List<Lease> getLeasesAboutToExpire() {
        return leaseRepository.findExpireInDateRange(clockService.now(), clockService.now().plusMonths(MONTHS));
    }

    @Collection(notPersisted = true)
    public List<Event> getUpcomingEvents() {
        return eventRepository.findEventsInDateRange(clockService.now(), clockService.now().plusMonths(MONTHS));
    }



    private void sort(final List<Task> tasks) {
        Collections.sort(tasks, Ordering.natural().nullsFirst().onResultOf(Task::getCreatedOn));
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
                return Lists.newArrayList("seller", "property", "netAmount", "invoiceDate", "number");
            }
            return null;
        }

        @Override
        public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
            return null;
        }
    }

    @Inject
    FactoryService factoryService;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PersonRepository personRepository;

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
