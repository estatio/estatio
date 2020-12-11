package org.estatio.module.lease.dom.amortisation;

public enum CreationStrategy {
    INVOICE_BASED, // When an invoice containing discount that should be amortised (depreciated) is sent, a schedule can be made
    AMENDMENT_BASED // For leases that we do not invoice but that are invoiced by third parties, we can create a schedule based on an amendment item when APPLIED
}
