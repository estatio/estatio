package org.estatio.dom.viewmodels;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.AgreementRoleTypeEnum;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTermStatus;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForTurnoverRentImport"
)
public class LeaseTermForTurnoverRentImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseTermForTurnoverRentImport.class);

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
    private String leaseItemAtPath;

    // generic term fields
    @Getter @Setter
    private BigInteger sequence;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String status;

    // turnover rent term fields
    @Getter @Setter
    private String turnoverRentRule;

    @Getter @Setter
    private BigDecimal auditedTurnover;

    @Getter @Setter
    private BigDecimal auditedTurnoverRent;

    // source fields

    @Getter @Setter
    private String sourceItemTypeName;

    @Getter @Setter
    private BigInteger sourceItemSequence;

    @Getter @Setter
    private LocalDate sourceItemStartDate;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(LeaseTermForIndexableRentImport.class, ChargeImport.class, IndexImport.class);
//    }

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object o) {
        return importData(null);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(Object previousRow) {

        //find or create leaseItem
        final Lease lease = fetchLease(leaseReference);

        final ApplicationTenancy leaseItemApplicationTenancy =
                ObjectUtils.firstNonNull(
                        securityApplicationTenancyRepository.findByPath(leaseItemAtPath),
                        lease.getApplicationTenancy());

        final Charge charge = fetchCharge(itemChargeReference);

        final LeaseItemType itemType = fetchLeaseItemType(itemTypeName);
        LeaseItem item = lease.findItem(itemType, itemStartDate, itemSequence);
        if (item == null) {
            item = lease.newItem(itemType, AgreementRoleTypeEnum.LANDLORD, charge, InvoicingFrequency.valueOf(itemInvoicingFrequency), PaymentMethod.valueOf(itemPaymentMethod), itemStartDate);
            item.setSequence(itemSequence);
        }
        item.setEpochDate(itemEpochDate);
        item.setNextDueDate(itemNextDueDate);
        final LeaseItemStatus leaseItemStatus = LeaseItemStatus.valueOfElse(itemStatus, LeaseItemStatus.ACTIVE);
        item.setStatus(leaseItemStatus);

        // Find rent item and create source link
        final LeaseItemType sourceItemType = LeaseItemType.valueOf(sourceItemTypeName);
        LeaseItem rentItem = item.getLease().findItem(sourceItemType, sourceItemStartDate, sourceItemSequence);
        if(rentItem == null) {
            throw new IllegalArgumentException(String.format("No Item foud %s %s %s", getLeaseReference(), getSourceItemTypeName(), getSourceItemStartDate()));
        }
        item.findOrCreateSourceItem(rentItem);

        //create term
        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) item.findTermWithSequence(sequence);
        if (term == null) {
            if (startDate == null) {
                throw new IllegalArgumentException("startDate cannot be empty");
            }
            if (sequence.equals(BigInteger.ONE)) {
                term = (LeaseTermForTurnoverRent) item.newTerm(startDate, endDate);
            } else {
                final LeaseTerm previousTerm = item.findTermWithSequence(sequence.subtract(BigInteger.ONE));
                if (previousTerm == null) {
                    throw new IllegalArgumentException("Previous term not found");
                }
                term = (LeaseTermForTurnoverRent) previousTerm.createNext(startDate, endDate);
            }
            term.setSequence(sequence);
        }
        term.setStatus(LeaseTermStatus.valueOf(status));

        //set turnover rent term values
        term.setTurnoverRentRule(turnoverRentRule);
        term.setAuditedTurnover(auditedTurnover);
        term.setAuditedTurnoverRent(auditedTurnoverRent);

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

    //region > injected services

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    //endregion

}
