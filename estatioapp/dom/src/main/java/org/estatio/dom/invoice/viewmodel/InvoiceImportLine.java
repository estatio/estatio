package org.estatio.dom.invoice.viewmodel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class InvoiceImportLine implements Importable {

    public String title() {
        return "Invoice Import";
    }

    public InvoiceImportLine(){
    }

    public InvoiceImportLine(
            final String leaseReference,
            final LocalDate dueDate,
            final String paymentMethod,
            final String itemChargeReference,
            final String itemDescription,
            final BigDecimal itemNetAmount,
            final LocalDate itemStartDate,
            final LocalDate itemEndDate
            ){
        this.leaseReference = leaseReference;
        this.dueDate = dueDate;
        this.paymentMethod = paymentMethod;
        this.itemChargeReference = itemChargeReference;
        this.itemDescription = itemDescription;
        this.itemNetAmount = itemNetAmount;
        this.itemStartDate = itemStartDate;
        this.itemEndDate = itemEndDate;
    }

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


    @ActionLayout(hidden = Where.EVERYWHERE)
    @Override public List<Class> importAfter() {
        return null;
    }

    @Override
    @Action(invokeOn= InvokeOn.OBJECT_AND_COLLECTION, publishing = Publishing.DISABLED, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public List<Object> importData() {

        List<Object> result = new ArrayList<>();
        Lease lease = fetchLease(getLeaseReference());
        PaymentMethod paymentMethod = fetchPaymentMethod(getPaymentMethod());
        String atPath = lease.getApplicationTenancyPath().concat("/").concat(lease.getPrimaryParty().getReference());
        ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(atPath);
        Invoice invoice = invoiceRepository.newInvoice(applicationTenancy,
                lease.getPrimaryParty(),
                lease.getSecondaryParty(),
                paymentMethod,
                currencies.findCurrency("EUR"),
                getDueDate(),
                lease, null);

        InvoiceItem invoiceItem = invoice.newItem(fetchCharge(getItemChargeReference()),BigDecimal.ONE,getItemNetAmount(),getItemStartDate(),getItemEndDate());
        if (getItemDescription() != null){
            invoiceItem.setDescription(getItemDescription());
        }

        result.add(invoice);

        return result;
    }

    private Lease fetchLease(final String leaseReference){
        final Lease lease = leaseRepository.findLeaseByReference(leaseReference);
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    private PaymentMethod fetchPaymentMethod(final String paymentMethod){
        try {
            return PaymentMethod.valueOf(paymentMethod);
        } catch (Exception e){
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


    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private InvoiceRepository invoiceRepository;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    private Currencies currencies;


}
