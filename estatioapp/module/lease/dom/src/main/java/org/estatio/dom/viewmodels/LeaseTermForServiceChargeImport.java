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
import org.estatio.dom.index.IndexRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.LeaseAgreementRoleTypeEnum;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForServiceCharge;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForServiceChargeImport"
)
public class LeaseTermForServiceChargeImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseTermForServiceChargeImport.class);

    // leaseItem fields
    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String itemTypeName;

    @Getter @Setter
    private String invoicedBy;

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

    // service charge term fields
    @Getter @Setter
    private BigDecimal auditedValue;

    @Getter @Setter
    private BigDecimal budgetedValue;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(LeaseItemImport.class, ChargeImport.class, IndexImport.class);
//    }

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

        //find or create leaseItem
        final Lease lease = fetchLease(leaseReference);

        final ApplicationTenancy leaseItemApplicationTenancy =
                ObjectUtils.firstNonNull(
                        securityApplicationTenancyRepository.findByPath(leaseItemAtPath),
                        lease.getApplicationTenancy());

        final Charge charge = fetchCharge(itemChargeReference);
        final LeaseItemType itemType = fetchLeaseItemType(itemTypeName);
        final LocalDate itemStartDateToUse = itemStartDate == null ? lease.getStartDate() : itemStartDate;
        final LeaseAgreementRoleTypeEnum invoicedByToUse = this.invoicedBy == null ? LeaseAgreementRoleTypeEnum.LANDLORD : LeaseAgreementRoleTypeEnum
                .valueOf(this.invoicedBy);
        final PaymentMethod paymentMethodToUse = itemPaymentMethod == null ? lease.defaultPaymentMethod() : PaymentMethod.valueOf(itemPaymentMethod);

        LeaseItem item = lease.findItem(
                itemType,
                charge,
                itemStartDateToUse,
                invoicedByToUse);
        if (item == null) {
            item = lease.newItem(
                    itemType,
                    invoicedByToUse,
                    charge,
                    InvoicingFrequency.valueOf(itemInvoicingFrequency),
                    paymentMethodToUse,
                    itemStartDateToUse);
            item.setSequence(itemSequence);
        }
        item.setEpochDate(itemEpochDate);
        item.setNextDueDate(itemNextDueDate);
        final LeaseItemStatus leaseItemStatus = LeaseItemStatus.valueOfElse(itemStatus, LeaseItemStatus.ACTIVE);
        item.setStatus(leaseItemStatus);

        //create term
        LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) item.findTermWithSequence(sequence);
        if (term == null) {
            if (startDate == null) {
                throw new IllegalArgumentException("startDate cannot be empty");
            }
            if (sequence.equals(BigInteger.ONE)) {
                term = (LeaseTermForServiceCharge) item.newTerm(startDate, endDate);
            } else {
                final LeaseTerm previousTerm = item.findTermWithSequence(sequence.subtract(BigInteger.ONE));
                if (previousTerm == null) {
                    throw new IllegalArgumentException("Previous term not found");
                }
                term = (LeaseTermForServiceCharge) previousTerm.createNext(startDate, endDate);
            }
            term.setSequence(sequence);
        }
        term.setStatus(org.estatio.dom.lease.LeaseTermStatus.valueOf(status));

        //set service charge term values
        term.setBudgetedValue(budgetedValue);
        term.setAuditedValue(auditedValue);

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

    @Inject
    private IndexRepository indexRepository;
    //endregion

}
