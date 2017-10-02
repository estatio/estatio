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
import org.estatio.dom.lease.LeaseAgreementRoleTypeEnum;
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
public class LeaseTermForTurnoverRentImport extends LeaseTermImportAbstract implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseTermForTurnoverRentImport.class);

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
        final Lease lease = fetchLease(getLeaseReference());

        final ApplicationTenancy leaseItemApplicationTenancy =
                ObjectUtils.firstNonNull(
                        securityApplicationTenancyRepository.findByPath(getLeaseItemAtPath()),
                        lease.getApplicationTenancy());

        final Charge charge = fetchCharge(getItemChargeReference());

        final LeaseItemType itemType = fetchLeaseItemType(getItemTypeName());
        LeaseItem item = lease.findItem(itemType, getItemStartDate(), getItemSequence());
        if (item == null) {
            item = lease.newItem(itemType, LeaseAgreementRoleTypeEnum.LANDLORD, charge, InvoicingFrequency.valueOf(getItemInvoicingFrequency()), PaymentMethod.valueOf(getItemPaymentMethod()), getItemStartDate());
            item.setSequence(getItemSequence());
        }
        item.setEpochDate(getItemEpochDate());
        item.setNextDueDate(getItemNextDueDate());
        final LeaseItemStatus leaseItemStatus = LeaseItemStatus.valueOfElse(getItemStatus(), LeaseItemStatus.ACTIVE);
        item.setStatus(leaseItemStatus);

        // Find rent item and create source link
        final LeaseItemType sourceItemType = LeaseItemType.valueOf(sourceItemTypeName);
        LeaseItem rentItem = item.getLease().findItem(sourceItemType, sourceItemStartDate, sourceItemSequence);
        if(rentItem != null) {
            item.findOrCreateSourceItem(rentItem);
        }

        //create term
        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) item.findTermWithSequence(getSequence());
        if (term == null) {
            if (getStartDate() == null) {
                throw new IllegalArgumentException("startDate cannot be empty");
            }
            if (getSequence().equals(BigInteger.ONE)) {
                term = (LeaseTermForTurnoverRent) item.newTerm(getStartDate(), getEndDate());
            } else {
                final LeaseTerm previousTerm = item.findTermWithSequence(getSequence().subtract(BigInteger.ONE));
                if (previousTerm == null) {
                    throw new IllegalArgumentException("Previous term not found");
                }
                term = (LeaseTermForTurnoverRent) previousTerm.createNext(getStartDate(), getEndDate());
            }
            term.setSequence(getSequence());
        }
        term.setStatus(LeaseTermStatus.valueOf(getStatus()));

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
