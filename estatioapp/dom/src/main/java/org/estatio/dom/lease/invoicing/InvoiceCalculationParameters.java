package org.estatio.dom.lease.invoicing;

import java.util.Arrays;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.valuetypes.AbstractInterval.IntervalEnding;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;
import org.apache.isis.applib.util.TitleBuffer;

public class InvoiceCalculationParameters {

    private Property property;

    private List<Lease> leases;

    private LeaseItem leaseItem;

    private LeaseTerm leaseTerm;

    private LocalDateInterval dueDateRange;

    private List<LeaseItemType> leaseItemTypes;

    private InvoiceRunType invoiceRunType;

    private LocalDate invoiceDueDate;

    private LocalDate invoiceDate;

    public InvoiceCalculationParameters(
            final InvoiceRunType invoicRunType,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        this.dueDateRange = new LocalDateInterval(
                startDueDate,
                nextDueDate,
                IntervalEnding.EXCLUDING_END_DATE);
        this.invoiceDueDate = invoiceDueDate;
        this.invoiceRunType = invoicRunType;
    }

    public InvoiceCalculationParameters(
            final Property property,
            final List<LeaseItemType> leaseItemTypes,
            final InvoiceRunType invoiceRunType,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        this(
                invoiceRunType,
                invoiceDueDate,
                startDueDate,
                nextDueDate);
        this.leaseItemTypes = leaseItemTypes;
        this.property = property;
    }
    
    private static Property propertyOf(List<Lease> leases) {
        if(leases.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one lease.");
        }
        Property property = leases.get(0).getProperty();
        for(Lease lease: leases) {
            Property p = lease.getProperty();
            if(property != p) {
                throw new IllegalArgumentException("All leases must reside in the same property");
            }
        }
        return property;
    }

    public InvoiceCalculationParameters(
            final List<Lease> leases,
            final List<LeaseItemType> leaseItemTypes,
            final InvoiceRunType invoiceRunType,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        this(
                propertyOf(leases),
                leaseItemTypes,
                invoiceRunType,
                invoiceDueDate,
                startDueDate,
                nextDueDate);
        this.leaseItemTypes = leaseItemTypes;
        this.leases = leases;
    }

    
    
    public InvoiceCalculationParameters(
            final Lease lease,
            final List<LeaseItemType> leaseItemTypes,
            final InvoiceRunType invoiceRunType,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        this(
                lease.getProperty(),
                leaseItemTypes,
                invoiceRunType,
                invoiceDueDate,
                startDueDate,
                nextDueDate);
        this.leases = Arrays.asList(lease);
    }

    public InvoiceCalculationParameters(
            final LeaseItem leaseItem,
            final InvoiceRunType invoicRunType,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        this(
                leaseItem.getLease(),
                Arrays.asList(leaseItem.getType()),
                invoicRunType,
                invoiceDueDate,
                startDueDate,
                nextDueDate);
        this.leaseItem = leaseItem;
    }

    public InvoiceCalculationParameters(
            final LeaseTerm leaseTerm,
            final InvoiceRunType invoiceRunType,
            final LocalDate invoiceDueDate,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        this(leaseTerm.getLeaseItem(), invoiceRunType, invoiceDueDate, startDueDate, nextDueDate);
        this.leaseTerm = leaseTerm;
    }

    public LocalDateInterval dueDateRange() {
        return dueDateRange;
    }

    public Property property() {
        return property;
    }

    public List<Lease> leases() {
        return leases;
    }

    public List<LeaseItemType> leaseItemTypes() {
        return leaseItemTypes;
    }

    public LeaseItem leaseItem() {
        return leaseItem;
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

    public LocalDate invoiceDate() {
        return invoiceDueDate;
    }

    public String toString() {
        TitleBuffer tb = new TitleBuffer();
        tb
                .append(" -", property.getReference())
                .append(" -", leasesToReferences())
                .append(" -", leaseItemTypes())
                .append(" -", invoiceDueDate)
                .append(" -", dueDateRange)
                .toString();
        return tb.toString();
    }

    private String leasesToReferences() {
        if (leases == null) {
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
