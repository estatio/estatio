package org.estatio.dom.invoice;

import java.util.List;

import com.google.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.FixedAsset;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(named = "Invoices")
public class InvoiceMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Invoice> findInvoices(
            final FixedAsset fixedAsset,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate dueDate,
            final InvoiceStatus status) {
        if (status == null) {
            return invoiceRepository.findByFixedAssetAndDueDate(fixedAsset, dueDate);
        } else if (dueDate == null) {
            return invoiceRepository.findByFixedAssetAndStatus(fixedAsset, status);
        } else {
            return invoiceRepository.findByFixedAssetAndDueDateAndStatus(fixedAsset, dueDate, status);
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Invoice> findInvoicesByInvoiceNumber(
            final String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    @Inject
    Invoices invoiceRepository;

}
