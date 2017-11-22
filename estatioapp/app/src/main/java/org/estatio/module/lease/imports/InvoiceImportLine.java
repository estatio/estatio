package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.invoice.InvoiceImportLine"
)
public class InvoiceImportLine implements Importable {

    //region > constructors, title
    public String title() {
        return "Invoice Import Line";
    }

    public InvoiceImportLine() {

    }

    public InvoiceImportLine(
            final String leaseReference,
            final LocalDate dueDate,
            final String paymentMethod,
            final String itemChargeReference,
            final String itemDescription,
            final BigDecimal itemNetAmount,
            final LocalDate itemStartDate,
            final LocalDate itemEndDate,
            final String unitReference
    ) {
        this.leaseReference = leaseReference;
        this.dueDate = dueDate;
        this.paymentMethod = paymentMethod;
        this.itemChargeReference = itemChargeReference;
        this.itemDescription = itemDescription;
        this.itemNetAmount = itemNetAmount;
        this.itemStartDate = itemStartDate;
        this.itemEndDate = itemEndDate;
        this.unitReference = unitReference;
    }
    //endregion

    @Getter @Setter
    private String leaseReference;
    @Getter @Setter
    private LocalDate dueDate;
    @Getter @Setter
    private String paymentMethod;
    @Getter @Setter
    private String itemChargeReference;
    @Getter @Setter
    private String itemDescription;
    @Getter @Setter
    private BigDecimal itemNetAmount;
    @Getter @Setter
    private LocalDate itemStartDate;
    @Getter @Setter
    private LocalDate itemEndDate;
    @Getter @Setter
    private String unitReference;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList();
//    }

    // REVIEW: is this view model actually ever surfaced in the UI?
    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, publishing = Publishing.DISABLED, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public List<Object> importData() {
        return importData(null);
    }

    @Override
    @Programmatic
    public List<Object> importData(final Object previousRow) {
        List<Object> result = new ArrayList<>();
        Lease lease = fetchLease(getLeaseReference());
        PaymentMethod paymentMethod = fetchPaymentMethod(getPaymentMethod());
        String atPath = lease.getApplicationTenancyPath().concat("/").concat(lease.getPrimaryParty().getReference());
        ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(atPath);
        Invoice invoice = invoiceForLeaseRepository.newInvoice(applicationTenancy,
                lease.getPrimaryParty(),
                lease.getSecondaryParty(),
                paymentMethod,
                currencyRepository.findCurrency("EUR"),
                getDueDate(),
                lease, null);

        InvoiceItem invoiceItem = factoryService.mixin(InvoiceForLease._newItem.class, invoice).$$(fetchCharge(getItemChargeReference()), BigDecimal.ONE, getItemNetAmount(), getItemStartDate(), getItemEndDate());
        if (getItemDescription() != null) {
            invoiceItem.setDescription(getItemDescription());
        }

        result.add(invoice);

        return result;
    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease = leaseRepository.findLeaseByReference(leaseReference);
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    private PaymentMethod fetchPaymentMethod(final String paymentMethod) {
        try {
            return PaymentMethod.valueOf(paymentMethod);
        } catch (Exception e) {
            throw new ApplicationException(String.format("Paymentmethod with value %s not found.", paymentMethod));
        }
    }

    private Charge fetchCharge(final String chargeReference) {
        final Charge charge = chargeRepository
                .findByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Charge with reference %s not found.", chargeReference));
        }
        return charge;
    }

    //region > injected services
    @Inject
    private FactoryService factoryService;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    private CurrencyRepository currencyRepository;
    //endregion

}
