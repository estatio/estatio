package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermFrequency;
import org.estatio.module.lease.dom.indexation.IndexationMethod;
import org.estatio.module.lease.dom.LeaseTermStatus;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForIndexableRentImport"
)
public class LeaseTermForIndexableRentImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseTermForIndexableRentImport.class);

    // leaseItem fields
    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String itemTypeName;

    @Getter @Setter
    private BigInteger itemSequence;

    @Getter @Setter
    private LocalDate itemStartDate;

    @Getter @Setter
    private LocalDate itemNextDueDate;

    @Getter @Setter
    private String itemChargeReference;

    @Getter @Setter
    private LocalDate itemEpochDate;

    @Getter @Setter
    private String itemInvoicingFrequency;

    @Getter @Setter
    private String itemPaymentMethod;

    @Getter @Setter
    private String itemStatus;

    @Getter @Setter
    private String itemAtPath;

    // generic term fields
    @Getter @Setter
    private BigInteger sequence;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String status;

    // indexation term fields
    @Getter @Setter
    private String indexationMethod;

    @Getter @Setter
    private LocalDate reviewDate;

    @Getter @Setter
    private LocalDate effectiveDate;

    @Getter @Setter
    private BigDecimal baseValue;

    @Getter @Setter
    private BigDecimal indexedValue;

    @Getter @Setter
    private BigDecimal settledValue;

    @Getter @Setter
    private BigDecimal levellingValue;

    @Getter @Setter
    private BigDecimal levellingPercentage;

    @Getter @Setter
    private String indexReference;

    @Getter @Setter
    private String indexationFrequency;

    @Getter @Setter
    private BigDecimal indexationPercentage;

    @Getter @Setter
    private LocalDate baseIndexStartDate;

    @Getter @Setter
    private LocalDate baseIndexEndDate;

    @Getter @Setter
    private BigDecimal baseIndexValue;

    @Getter @Setter
    private LocalDate nextIndexStartDate;

    @Getter @Setter
    private LocalDate nextIndexEndDate;

    @Getter @Setter
    private BigDecimal nextIndexValue;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(LeaseItemImport.class, ChargeImport.class, IndexImport.class);
//    }

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

        final LeaseItemImport leaseItemImport = new LeaseItemImport(
                leaseReference,
                itemTypeName,
                itemSequence,
                itemStartDate,
                itemChargeReference,
                itemEpochDate,
                itemNextDueDate,
                itemInvoicingFrequency,
                itemPaymentMethod,
                itemStatus,
                itemAtPath);

        domainObjectContainer.injectServicesInto(leaseItemImport);
        LeaseItem item = leaseItemImport.importItem(false);

        //create term
        LeaseTermForIndexable term = (LeaseTermForIndexable) item.findTermWithSequence(sequence);
        if (term == null) {
            if (startDate == null) {
                throw new IllegalArgumentException("startDate cannot be empty");
            }
            if (sequence.equals(BigInteger.ONE) || !item.getType().autoCreateTerms()) {
                // create a standalone term on the first or when autoCreateTerms is false
                term = (LeaseTermForIndexable) item.newTerm(startDate, endDate);
            } else {
                // join the current with the previous
                final LeaseTerm previousTerm = item.findTermWithSequence(sequence.subtract(BigInteger.ONE));
                if (previousTerm == null) {
                    throw new IllegalArgumentException(String.format("Previous term not found: %s", this.toString()));
                }
                term = (LeaseTermForIndexable) previousTerm.createNext(startDate, endDate);
            }
            term.setSequence(sequence);
        }
        term.setStatus(LeaseTermStatus.valueOf(status));
        final ApplicationTenancy applicationTenancy = term.getLeaseItem().getApplicationTenancy();

        //set indexation term values
        term.setIndexationMethod(indexationMethod == null ? null : IndexationMethod.valueOf(indexationMethod));
        term.setIndex(indexReference == null ? null : indexRepository.findOrCreateIndex(applicationTenancy, indexReference, indexReference));
        term.setFrequency(indexationFrequency == null ? null : LeaseTermFrequency.valueOf(indexationFrequency));
        term.setEffectiveDate(effectiveDate);
        term.setBaseValue(baseValue);
        term.setIndexedValue(indexedValue);
        term.setSettledValue(settledValue);
        term.setBaseIndexStartDate(baseIndexStartDate);
        term.setBaseIndexValue(baseIndexValue);
        term.setNextIndexStartDate(nextIndexStartDate);
        term.setNextIndexValue(nextIndexValue);
        term.setIndexationPercentage(indexationPercentage);
        term.setLevellingPercentage(levellingPercentage);
        return Lists.newArrayList(term);

    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    private LeaseItemType fetchLeaseItemType(final String type) {
        final LeaseItemType itemType = LeaseItemType.valueOf(type);
        if (itemType == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", type));
        }
        return itemType;
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
    LeaseRepository leaseRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    private IndexRepository indexRepository;

    @Inject
    private DomainObjectContainer domainObjectContainer;
}
