package org.estatio.module.lease.dom.invoicing;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.lease.dom.Lease;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public class InvoiceForLease_resetSendTo {

    private final InvoiceForLease invoiceForLease;

    public InvoiceForLease_resetSendTo(final InvoiceForLease invoiceForLease) {
        this.invoiceForLease = invoiceForLease;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceForLease act() {
        service.apply(invoiceForLease);
        return invoiceForLease;
    }

    public String disableAct() {
        final CommunicationChannel communicationChannel = service.determineSendToFor(invoiceForLease);
        return invoiceForLease.getSendTo() == communicationChannel ? "Not overridden": null;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Service {

        @Programmatic
        public void apply(final InvoiceForLease invoiceForLease) {
            final CommunicationChannel sendTo = determineSendToFor(invoiceForLease);
            invoiceForLease.setSendTo(sendTo);
        }

        @Programmatic
        CommunicationChannel determineSendToFor(final InvoiceForLease invoiceForLease) {
            final Lease lease = invoiceForLease.getLease();
            return repository.firstCurrentTenantInvoiceAddress(lease);
        }

        @javax.inject.Inject
        InvoiceForLeaseRepository repository;
    }

    @javax.inject.Inject
    Service service;
}
