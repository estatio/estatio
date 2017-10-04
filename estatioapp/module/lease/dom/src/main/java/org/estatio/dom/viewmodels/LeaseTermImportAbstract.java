package org.estatio.dom.viewmodels;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseAgreementRoleTypeEnum;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

public class LeaseTermImportAbstract {

    @Inject
    LeaseRepository leaseRepository;
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

    @Getter @Setter
    private BigInteger sequence;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String status;

    @Getter @Setter
    private String sourceItemTypeName;

    @Getter @Setter
    private BigInteger sourceItemSequence;

    @Getter @Setter
    private LocalDate sourceItemStartDate;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;


    protected LeaseItem initLeaseItem() {
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

        // Find source item and create source link
        if (getSourceItemTypeName() != null) {
            final LeaseItemType sourceItemType = LeaseItemType.valueOf(sourceItemTypeName);
            LeaseItem sourceItem = item.getLease().findItem(sourceItemType, sourceItemStartDate, sourceItemSequence);
            if (sourceItem != null) {
                item.findOrCreateSourceItem(sourceItem);
            }
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
}
