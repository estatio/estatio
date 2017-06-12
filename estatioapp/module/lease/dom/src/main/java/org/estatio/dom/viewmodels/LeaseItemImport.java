package org.estatio.dom.viewmodels;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

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

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseItemImport"
)
public class LeaseItemImport implements ExcelFixtureRowHandler, Importable {

    @Deprecated
    public LeaseItemImport() {
    }

    public LeaseItemImport(
            final String leaseReference,
            final String itemTypeName,
            final BigInteger sequence,
            final LocalDate startDate,
            final String chargeReference,
            final LocalDate epochDate,
            final LocalDate nextDueDate,
            final String invoicingFrequency,
            final String paymentMethod,
            final String status,
            final String atPath
    ) {
        this.leaseReference = leaseReference;
        this.itemTypeName = itemTypeName;
        this.sequence = sequence;
        this.startDate = startDate;
        this.chargeReference = chargeReference;
        this.epochDate = epochDate;
        this.nextDueDate = nextDueDate;
        this.invoicingFrequency = invoicingFrequency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.atPath = atPath;

    }

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String itemTypeName;

    @Getter @Setter
    private BigInteger sequence;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private String chargeReference;

    @Getter @Setter
    private LocalDate epochDate;

    @Getter @Setter
    private LocalDate nextDueDate;

    @Getter @Setter
    private String invoicingFrequency;

    @Getter @Setter
    private String paymentMethod;

    @Getter @Setter
    private String status;

    @Getter @Setter
    private String atPath;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(LeaseImport.class);
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
        LeaseItem item = importItem(true);
        return Lists.newArrayList(item);
    }

    @Programmatic
    public LeaseItem importItem(boolean updateExisting) {
        final Lease lease = fetchLease(leaseReference);
        final Charge charge = fetchCharge(chargeReference);
        final LeaseItemType itemType = fetchLeaseItemType(itemTypeName);

        // for deposit items the start date defaults to the start date of the lease
        LocalDate startDateOrDefault;
        if (itemType == LeaseItemType.DEPOSIT) {
            startDateOrDefault = ObjectUtils.firstNonNull(startDate, lease.getStartDate());
        } else {
            startDateOrDefault = startDate;
        }
        LeaseItem item = lease.findItem(itemType, startDateOrDefault, sequence);
        if (item == null) {
            item = lease.newItem(itemType, AgreementRoleTypeEnum.LANDLORD, charge, InvoicingFrequency.valueOf(invoicingFrequency), PaymentMethod.valueOf(paymentMethod), startDateOrDefault);
            item.setSequence(sequence);
        }
        if (updateExisting) {
            item.setApplicationTenancyPath(atPath);
            item.setEpochDate(epochDate);
            item.setNextDueDate(nextDueDate);
            item.setStatus(LeaseItemStatus.valueOfElse(status, LeaseItemStatus.ACTIVE));
        }
        return item;
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

}
