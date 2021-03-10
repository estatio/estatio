package org.estatio.module.lease.contributions.amortisation;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleInvoiceLinkRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin
public class InvoiceForLease_amortisationSchedules {

    private final InvoiceForLease invoiceForLease;

    public InvoiceForLease_amortisationSchedules(InvoiceForLease invoiceForLease) {
        this.invoiceForLease = invoiceForLease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<AmortisationSchedule> $$() {
        return amortisationScheduleInvoiceLinkRepository.findByInvoice(invoiceForLease).stream()
                .map(l->l.getAmortisationSchedule())
                .collect(Collectors.toList());

    }

    @Inject
    AmortisationScheduleInvoiceLinkRepository amortisationScheduleInvoiceLinkRepository;

}
