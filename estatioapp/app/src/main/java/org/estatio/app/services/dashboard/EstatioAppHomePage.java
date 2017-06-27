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

import javax.inject.Inject;

import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
import org.estatio.dom.event.Event;
import org.estatio.dom.event.EventRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.dashboard.EstatioAppHomePage"
)
public class EstatioAppHomePage {

    private static final int MONTHS = 3;

    //region > title
    public String title() {
        return "Home Page";
    }
    //endregion

    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
    public List<Task> getTasksForMe() {
        return taskRepository.findTasksIncompleteForMe();
    }

    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
    public List<Task> getTasksForOthers() {
        return taskRepository.findTasksIncompleteForOthers();
    }


    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
    public List<IncomingInvoice> getIncomingInvoicesNew() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.NEW);
    }

    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
    public List<IncomingInvoice> getIncomingInvoicesCompleted() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.COMPLETED);
    }

    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
    public List<IncomingInvoice> getIncomingInvoicesApproved() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.APPROVED);
    }

    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
    public List<IncomingInvoice> getIncomingInvoicesApprovedByCountryDirector() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR);
    }

    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
    public List<IncomingInvoice> getIncomingInvoicesPendingBankAccountCheck() {
        return incomingInvoiceRepository.findByApprovalState(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
    }

    @Collection(notPersisted = true)
    @CollectionLayout(paged = 20)
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
