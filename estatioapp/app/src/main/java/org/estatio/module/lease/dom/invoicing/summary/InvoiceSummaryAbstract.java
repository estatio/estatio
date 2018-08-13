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
package org.estatio.module.lease.dom.invoicing.summary;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.services.wrapper.InteractionException;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancy;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyAny;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;

public abstract class InvoiceSummaryAbstract implements WithApplicationTenancy, WithApplicationTenancyAny {

    public Object verifyAll() {
        for (Invoice invoice : getInvoices()) {
            invoice.verify();
        }
        return this;
    }

    public boolean hideVerifyAll() {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser());
    }

    public Object approveAll() {
        for (Invoice invoice : getInvoices()) {
            mixin(InvoiceForLease._approve.class, invoice).doApprove();
        }
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Object collectAll() {
        for (Invoice invoice : getInvoices()) {
            try {
                wrap(mixin(InvoiceForLease._collect.class, invoice)).$$();
            } catch(InteractionException ex) {
                // we simply ignore any exceptions thrown; we rely on the wrapper around Invoice#collect(...) action
                // to check its invariants.
                //
                // from the end-user's perspective, some invoices simply won't transition into a COLLECTED state
                // and no event will be published (so no posting of prelim letters to Coda)
            }
        }
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object invoiceAll(final LocalDate invoiceDate) {
        for (Invoice invoice : getInvoices()) {
            try {
                wrap(mixin(InvoiceForLease._invoice.class, invoice)).$$(invoiceDate);
            } catch(InteractionException ex) {
                // we simply ignore any exceptions thrown; we rely on the wrapper around Invoice#invoice(...) action
                // to check its invariants.
                //
                // from the end-user's perspective, some invoices simply won't transition into an INVOICED state
                // and no event will be published (so no posting to Coda)
            }
        }
        return this;
    }

//    public String validate0InvoiceAll(final LocalDate invoiceDate) {
//        for (Invoice invoice : getInvoices()) {
//            try {
//                final InvoiceForLease._invoice mixin = mixin(InvoiceForLease._invoice.class, invoice);
//                wrapperFactory.wrapNoExecute(mixin).$$(invoiceDate);
//            } catch (InvalidException ex) {
//                final String reasonMessage =
//                        ex.getInteractionEvent() != null
//                                ? ex.getInteractionEvent().getReason()
//                                : null;
//                return titleService.titleOf(invoice) + ": " +
//                        (reasonMessage != null ? reasonMessage : ex.getMessage());
//            } catch (HiddenException | DisabledException ex) {
//                // ignore
//            }
//        }
//        return null;
//    }

    /**
     * It doesn't harm to do this, but note that
     * @return
     */
    public String disableInvoiceAll() {
        return getInvoices()
                .stream()
                .anyMatch(invoice -> invoice.getStatus().equals(InvoiceStatus.NEW))
                ? "Invoices with status 'New' can't be invoiced"
                : null;
    }

    public LocalDate default0InvoiceAll() {
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object removeAll() {
        for (Invoice invoice : getInvoices()) {
            mixin(Invoice._remove.class, invoice).exec();
        }
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceSummaryAbstract saveAllAsHistoric() {
        for (Invoice invoice : getInvoices()) {
            mixin(InvoiceForLease._saveAsHistoric.class, invoice).$$();
        }
        return this;
    }

    public boolean hideSaveAllAsHistoric() {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser());
    }

    @CollectionLayout(defaultView = "table")
    public abstract List<InvoiceForLease> getInvoices();

    private <T> T wrap(final T mixin) {
        return wrapperFactory.wrap(mixin);
    }

    private <P, T extends P> T mixin(final Class<T> mixinClass, final P mixedIn) {
        return factoryService.mixin(mixinClass, mixedIn);
    }

    @Inject
    protected InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    protected InvoiceRepository invoiceRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    protected WrapperFactory wrapperFactory;

    @Inject
    protected FactoryService factoryService;

    @Inject
    protected ClockService clockService;

    @Inject
    protected UserService userService;

    @Inject
    TitleService titleService;

}
