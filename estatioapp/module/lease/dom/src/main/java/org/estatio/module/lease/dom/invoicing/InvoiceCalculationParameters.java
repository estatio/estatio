package org.estatio.module.lease.dom.invoicing;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.base.dom.valuetypes.AbstractInterval.IntervalEnding;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.asset.Property;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;

import lombok.Builder;
import lombok.Singular;

@Builder
public class InvoiceCalculationParameters {

    private Property property;

    @Singular
    private List<Lease> leases;

    private LeaseItem leaseItem;

    private LeaseTerm leaseTerm;

    @Singular
    private List<LeaseItemType> leaseItemTypes;

    private InvoiceRunType invoiceRunType;

    private LocalDate invoiceDueDate;

    private LocalDate startDueDate;

    private LocalDate nextDueDate;

    private LocalDate invoiceDate;

    private static Property propertyOf(List<Lease> leases) {
        Property property = leases.get(0).getProperty();
        for(Lease lease: leases) {
            Property p = lease.getProperty();
            if(property != p) {
                throw new IllegalArgumentException("All leases must reside in the same property");
            }
        }
        return property;
    }

    public LocalDateInterval dueDateRange() {
        return new LocalDateInterval(
                startDueDate,
                nextDueDate,
                IntervalEnding.EXCLUDING_END_DATE);
    }

    public Property property() {
        return property != null ? property : propertyOf(leases());
    }

    public List<Lease> leases() {
        return leases.size() > 0 ? leases : leaseItem() != null ?  Lists.newArrayList(leaseItem().getLease()) : leases;
    }

    public List<LeaseItemType> leaseItemTypes() {
        return leaseItemTypes.size() == 0 ? leaseItem() != null ? Lists.newArrayList(leaseItem().getType()) : leaseItemTypes : leaseItemTypes;
    }

    public LeaseItem leaseItem() {
        return leaseItem != null ? leaseItem : leaseTerm != null ? leaseTerm.getLeaseItem() : null;
    }

    public LeaseTerm leaseTerm() {
        return leaseTerm;
    }

    public InvoiceRunType invoiceRunType() {
        return invoiceRunType;
    }

    public LocalDate invoiceDueDate() {
        return invoiceDueDate;
    }

    public String toString() {
        TitleBuffer tb = new TitleBuffer();
        tb
                .append(" -", property().getReference())
                .append(" -", leasesToReferences())
                .append(" -", leaseItemTypes())
                .append(" -", invoiceDueDate())
                .append(" -", new LocalDateInterval(
                        startDueDate,
                        nextDueDate,
                        IntervalEnding.EXCLUDING_END_DATE))
                .toString();
        return tb.toString();
    }

    private String leasesToReferences() {
        if (leases == null || leases.size() == 0) {
            return null;
        }
        return Lists.transform(leases, ReferenceOfLease.INSTANCE).toString();
    }

    private enum ReferenceOfLease implements Function<Lease, String> {
        INSTANCE;

        @Override
        public String apply(Lease input) {
            return input.getReference();
        }
    }

}
