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
import org.apache.isis.applib.services.wrapper.WrapperFactory;

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
        this.leaseAmendmentType = leaseAmendment.getLeaseAmendmentType();
        this.leaseAmendmentState = leaseAmendment.getState();
        this.leaseReference = leaseAmendment.getLease().getReference();
        this.startDate = leaseAmendment.getStartDate();
        this.endDate = leaseAmendment.getEndDate();
        final LeaseAmendmentItemForDiscount discountItem = (LeaseAmendmentItemForDiscount) leaseAmendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT)
                .stream().findFirst().orElse(null);
        if (discountItem!=null) {
            this.discountPercentage = discountItem.getDiscountPercentage();
            this.discountApplicableTo = discountItem.getApplicableTo();
            this.discountStartDate = discountItem.getStartDate();
            this.discountEndDate = discountItem.getEndDate();
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

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private LeaseAmendmentType leaseAmendmentType;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private LeaseAmendmentState leaseAmendmentState;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String leaseReference;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate startDate;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private LocalDate endDate;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal discountPercentage;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private String discountApplicableTo;

    @Getter @Setter
    @MemberOrder(sequence = "8")
    private LocalDate discountStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "9")
    private LocalDate discountEndDate;

    @Getter @Setter
    @MemberOrder(sequence = "10")
    private InvoicingFrequency invoicingFrequencyOnLease;

    @Getter @Setter
    @MemberOrder(sequence = "11")
    private InvoicingFrequency amendedInvoicingFrequency;

    @Getter @Setter
    @MemberOrder(sequence = "12")
    private String frequencyChangeApplicableTo;

    @Getter @Setter
    @MemberOrder(sequence = "13")
    private LocalDate frequencyChangeStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "14")
    private LocalDate frequencyChangeEndDate;


    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {
        final Lease lease = fetchLease(leaseReference);
        if (leaseAmendmentState==LeaseAmendmentState.APPLIED){
            throw new ApplicationException(String.format("State %s for lease %s not allowed.", leaseAmendmentState, leaseReference));
        }
        final LeaseAmendment amendment = leaseAmendmentRepository.upsert(lease, leaseAmendmentType, leaseAmendmentState, startDate, endDate);
        if (amendment.getState()==LeaseAmendmentState.APPLIED) return Lists.newArrayList(amendment);
        
        if (discountPercentage!=null && discountApplicableTo!=null && discountStartDate!=null && discountEndDate!=null) {
            amendment.upsertItem(discountPercentage, LeaseAmendmentItem.applicableToFromString(discountApplicableTo), discountStartDate, discountEndDate);
        }
        if (invoicingFrequencyOnLease!=null && amendedInvoicingFrequency !=null && frequencyChangeApplicableTo !=null && frequencyChangeStartDate
                !=null && frequencyChangeEndDate !=null) {
            amendment.upsertItem(invoicingFrequencyOnLease, amendedInvoicingFrequency, LeaseAmendmentItem.applicableToFromString(
                    frequencyChangeApplicableTo),
                    frequencyChangeStartDate, frequencyChangeEndDate);
        }
        final LeaseAmendment leaseAmendment = leaseAmendmentRepository.findUnique(lease, leaseAmendmentType);
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

    @Inject WrapperFactory wrapperFactory;

    @Inject BackgroundService2 backgroundService2;

}
