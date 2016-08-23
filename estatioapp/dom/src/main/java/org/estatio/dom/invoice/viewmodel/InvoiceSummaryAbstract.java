/*
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
package org.estatio.dom.invoice.viewmodel;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;

import org.estatio.dom.EstatioUserRole;
import org.estatio.dom.apptenancy.WithApplicationTenancyAny;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceRepository;

public abstract class InvoiceSummaryAbstract implements WithApplicationTenancy, WithApplicationTenancyAny {

    public Object approveAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.doApprove();
        }
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Object collectAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.doCollect();
        }
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object invoiceAll(final LocalDate invoiceDate) {
        for (Invoice invoice : getInvoices()) {
            wrapperFactory.wrap(invoice).invoice(invoiceDate);
        }
        return this;
    }

    public LocalDate default0InvoiceAll() {
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object removeAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.remove();
        }
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceSummaryAbstract saveAllAsHistoric() {
        for (Invoice invoice : getInvoices()) {
            invoice.saveAsHistoric();
        }
        return this;
    }

    public boolean hideSaveAllAsHistoric(){
        return !EstatioUserRole.ADMIN_ROLE.isApplicableTo(userService.getUser());
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    public abstract List<Invoice> getInvoices();

    @Inject
    protected InvoiceRepository invoiceRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    protected WrapperFactory wrapperFactory;

    @Inject
    protected ClockService clockService;

    @Inject
    protected UserService userService;


}
