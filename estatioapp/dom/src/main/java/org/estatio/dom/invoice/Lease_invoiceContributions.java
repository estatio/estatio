package org.estatio.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.leaseinvoicing.InvoiceForLease;
import org.estatio.dom.leaseinvoicing.InvoiceForLeaseRepository;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class Lease_invoiceContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<InvoiceForLease> invoices(final Lease lease) {
        return invoiceRepository.findByLease(lease);
    }

    @Inject
    InvoiceForLeaseRepository invoiceRepository;

}
