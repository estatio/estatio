package org.estatio.dom.invoice;

import org.apache.isis.applib.annotation.*;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;

import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.List;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.4")
public class InvoiceMenu  extends UdoDomainRepositoryAndFactory<Invoice> {

    public InvoiceMenu() {
        super(InvoiceMenu.class, Invoice.class);
    }

    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Invoice newInvoiceForLease(
            final Lease lease,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final Currency currency,
            final ApplicationTenancy applicationTenancy) {
        return invoiceRepository.newInvoice(applicationTenancy,
                lease.getPrimaryParty(),
                lease.getSecondaryParty(),
                paymentMethod,
                currency,
                dueDate,
                lease, null);
    }

    public List<ApplicationTenancy> choices4NewInvoiceForLease() {
        return estatioApplicationTenancyRepository.selfOrChildrenOf(meService.me().getTenancy());
    }

    // //////////////////////////////////////

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

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "98")
    public List<Invoice> allInvoices() {
        return allInstances();
    }

    @Inject
    private InvoiceRepository invoiceRepository;

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    private MeService meService;
}
