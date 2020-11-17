package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.message.MessageService;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.lease.dom.amendments.LeaseAmendmentImportLine"
)
public class LeaseAmendmentImportLine implements ExcelFixtureRowHandler, Importable {

    public LeaseAmendmentImportLine(){};

    public LeaseAmendmentImportLine(final LeaseAmendment leaseAmendment){
        this();
        this.leaseAmendmentTemplate = leaseAmendment.getLeaseAmendmentTemplate();
        this.leaseAmendmentState = leaseAmendment.getState();
        this.dateSigned = leaseAmendment.getDateSigned();
        this.dateApplied = leaseAmendment.getDateApplied();
        this.leaseReference = leaseAmendment.getLease().getReference();
        this.startDate = leaseAmendment.getStartDate();
        this.endDate = leaseAmendment.getEndDate();
        final LeaseAmendmentItemForDiscount discountItem = (LeaseAmendmentItemForDiscount) leaseAmendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT)
                .stream().findFirst().orElse(null);
        if (discountItem!=null) {
            this.discountPercentage = discountItem.getDiscountPercentage();
            this.manualDiscountAmount = discountItem.getManualDiscountAmount();
            this.discountApplicableTo = discountItem.getApplicableTo();
            this.discountStartDate = discountItem.getStartDate();
            this.discountEndDate = discountItem.getEndDate();
            this.calculatedDiscountAmount = discountItem.getCalculatedDiscountAmount();
        }
        final LeaseAmendmentItemForFrequencyChange freqItem = (LeaseAmendmentItemForFrequencyChange) leaseAmendment.findItemsOfType(LeaseAmendmentItemType.INVOICING_FREQUENCY_CHANGE)
                .stream().findFirst().orElse(null);
        if (freqItem!=null) {
            this.invoicingFrequencyOnLease = freqItem.getInvoicingFrequencyOnLease();
            this.amendedInvoicingFrequency = freqItem.getAmendedInvoicingFrequency();
            this.frequencyChangeApplicableTo = freqItem.getApplicableTo();
            this.frequencyChangeStartDate = freqItem.getStartDate();
            this.frequencyChangeEndDate = freqItem.getEndDate();
        }
    }

    public LeaseAmendmentImportLine(final LeaseAmendment leaseAmendment, final LeaseAmendmentItemForDiscount item){
        this();
        this.leaseAmendmentTemplate = leaseAmendment.getLeaseAmendmentTemplate();
        this.leaseAmendmentState = leaseAmendment.getState();
        this.dateSigned = leaseAmendment.getDateSigned();
        this.dateApplied = leaseAmendment.getDateApplied();
        this.leaseReference = leaseAmendment.getLease().getReference();
        this.startDate = leaseAmendment.getStartDate();
        this.endDate = leaseAmendment.getEndDate();
        if (item!=null) {                                               // should not be possible, but still ....
            this.discountPercentage = item.getDiscountPercentage();
            this.manualDiscountAmount = item.getManualDiscountAmount();
            this.discountApplicableTo = item.getApplicableTo();
            this.discountStartDate = item.getStartDate();
            this.discountEndDate = item.getEndDate();
            this.calculatedDiscountAmount = item.getCalculatedDiscountAmount();
        }
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private LeaseAmendmentTemplate leaseAmendmentTemplate;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private LeaseAmendmentState leaseAmendmentState;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private LocalDate dateSigned;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate dateApplied;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private String leaseReference;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private LocalDate startDate;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private LocalDate endDate;

    @Getter @Setter
    @MemberOrder(sequence = "8")
    private BigDecimal discountPercentage;

    @Getter @Setter
    @MemberOrder(sequence = "9")
    private BigDecimal manualDiscountAmount;

    @Getter @Setter
    @MemberOrder(sequence = "10")
    private BigDecimal calculatedDiscountAmount;

    @Getter @Setter
    @MemberOrder(sequence = "11")
    private String discountApplicableTo;

    @Getter @Setter
    @MemberOrder(sequence = "12")
    private LocalDate discountStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "13")
    private LocalDate discountEndDate;

    @Getter @Setter
    @MemberOrder(sequence = "14")
    private InvoicingFrequency invoicingFrequencyOnLease;

    @Getter @Setter
    @MemberOrder(sequence = "15")
    private InvoicingFrequency amendedInvoicingFrequency;

    @Getter @Setter
    @MemberOrder(sequence = "16")
    private String frequencyChangeApplicableTo;

    @Getter @Setter
    @MemberOrder(sequence = "17")
    private LocalDate frequencyChangeStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "18")
    private LocalDate frequencyChangeEndDate;


    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {
        final Lease lease = fetchLease(leaseReference);
        LeaseAmendmentImportLine previousLine = previousRow!=null ? (LeaseAmendmentImportLine) previousRow : null;
        final Lease leasePreviousLine = previousLine!=null ? fetchLease(previousLine.getLeaseReference()) : null;
        if (leaseAmendmentState==LeaseAmendmentState.APPLIED){
            throw new ApplicationException(String.format("State %s for lease %s not allowed.", leaseAmendmentState, leaseReference));
        }
        final LeaseAmendment amendment = leaseAmendmentRepository.upsert(lease, leaseAmendmentTemplate, leaseAmendmentState, startDate, endDate);
        if (amendment.getState()==LeaseAmendmentState.APPLIED) return Lists.newArrayList(amendment);
        if (amendment.getState()==LeaseAmendmentState.SIGNED && dateSigned!=null) amendment.setDateSigned(dateSigned);
        
        if (discountPercentage!=null && discountApplicableTo!=null && discountStartDate!=null && discountEndDate!=null) {
            if (!lease.equals(leasePreviousLine)) {
                // ECP-1283: delete any items for discount created earlier in the process
                for (LeaseAmendmentItem item : amendment.getItems()) {
                    if (item.getType() == LeaseAmendmentItemType.DISCOUNT) {
                        item.remove();
                    }
                }
            }
            try {
                amendment.upsertItem(discountPercentage, manualDiscountAmount,
                        LeaseAmendmentItem.applicableToFromString(discountApplicableTo), discountStartDate,
                        discountEndDate);
            } catch (IllegalArgumentException e){
                messageService.raiseError(e.getMessage());
            }
        }
        if (invoicingFrequencyOnLease!=null && amendedInvoicingFrequency !=null && frequencyChangeApplicableTo !=null && frequencyChangeStartDate
                !=null && frequencyChangeEndDate !=null) {
            if (!lease.equals(leasePreviousLine)) {
                // ECP-1283: delete any items for discount created earlier in the process
                for (LeaseAmendmentItem item : amendment.getItems()) {
                    if (item.getType() == LeaseAmendmentItemType.INVOICING_FREQUENCY_CHANGE) {
                        item.remove();
                    }
                }
            }
            amendment.upsertItem(invoicingFrequencyOnLease, amendedInvoicingFrequency, LeaseAmendmentItem.applicableToFromString(
                    frequencyChangeApplicableTo),
                    frequencyChangeStartDate, frequencyChangeEndDate);
        }
        final LeaseAmendment leaseAmendment = leaseAmendmentRepository.findUnique(lease, leaseAmendmentTemplate);
        backgroundService2.execute(leaseAmendment).createOrRenewLeasePreview();
        return Lists.newArrayList(leaseAmendment);
    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    BackgroundService2 backgroundService2;

    @Inject MessageService messageService;

}
