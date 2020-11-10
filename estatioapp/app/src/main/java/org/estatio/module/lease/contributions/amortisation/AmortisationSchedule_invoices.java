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
public class AmortisationSchedule_invoices {

    private final AmortisationSchedule amortisationSchedule;

    public AmortisationSchedule_invoices(AmortisationSchedule amortisationSchedule) {
        this.amortisationSchedule = amortisationSchedule;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<InvoiceForLease> $$() {
        return amortisationScheduleInvoiceItemLinkRepository.findBySchedule(amortisationSchedule)
        .stream()
        .map(l->l.getInvoice())
        .collect(Collectors.toList());
    }

    @Inject
    AmortisationScheduleInvoiceLinkRepository amortisationScheduleInvoiceItemLinkRepository;

}
