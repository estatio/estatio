package org.estatio.module.lease.dom.invoicing;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseStatus;

@Mixin(method="coll")
public class Lease_invoices {

    private final Lease lease;

    public Lease_invoices(final Lease lease) {
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<InvoiceForLease> coll() {
        return invoiceRepository.findByLease(lease);
    }

    public boolean hideColl(){
        if (lease.getStatus()== LeaseStatus.PREVIEW) return true;
        return false;
    }

    @Inject
    InvoiceForLeaseRepository invoiceRepository;

}

