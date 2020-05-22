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
import org.apache.isis.applib.services.factory.FactoryService;
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
    private String frequencyApplicableTo;

    @Getter @Setter
    @MemberOrder(sequence = "13")
    private LocalDate frequencyStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "14")
    private LocalDate frequencyEndDate;


    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        final Lease lease = fetchLease(leaseReference);
        final Lease_createLeaseAmendment mixin = factoryService.mixin(Lease_createLeaseAmendment.class, lease);
        wrapperFactory.wrap(mixin).$$(
                leaseAmendmentType,
                startDate,
                endDate,
                discountPercentage,
                LeaseAmendmentItem.applicableToFromString(discountApplicableTo),
                discountStartDate,
                discountEndDate,
                invoicingFrequencyOnLease,
                amendedInvoicingFrequency,
                LeaseAmendmentItem.applicableToFromString(frequencyApplicableTo),
                frequencyStartDate,
                frequencyEndDate
        );
        final LeaseAmendment leaseAmendment = leaseAmendmentRepository.findUnique(lease, leaseAmendmentType);
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
    FactoryService factoryService;

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    WrapperFactory wrapperFactory;
}
