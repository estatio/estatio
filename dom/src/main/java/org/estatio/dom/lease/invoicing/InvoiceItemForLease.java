package org.estatio.dom.lease.invoicing;

import java.math.BigInteger;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Query(name = "invoiceItem_findItems", language = "JDOQL", value = "SELECT " + "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " + "WHERE leaseTerm.leaseItem.lease.reference.matches(:leaseReference) " + "&& dueDate == :dueDate " + "&& startDate == :startDate")
public class InvoiceItemForLease extends InvoiceItem {

    private LeaseTerm leaseTerm;

    @Disabled
    @Hidden(where = Where.REFERENCES_PARENT)
    @MemberOrder(sequence = "11")
    public LeaseTerm getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(final LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public void modifyLeaseTerm(final LeaseTerm leaseTerm) {
        LeaseTerm currentLeaseTerm = getLeaseTerm();
        if (leaseTerm == null || leaseTerm.equals(currentLeaseTerm)) {
            return;
        }
        if (currentLeaseTerm != null) {
            currentLeaseTerm.removeFromInvoiceItems(this);
        }
        leaseTerm.addToInvoiceItems(this);
    }

    public void clearLeaseTerm() {
        LeaseTerm currentLeaseTerm = getLeaseTerm();
        if (currentLeaseTerm == null) {
            return;
        }
        currentLeaseTerm.removeFromInvoiceItems(this);
    }

    // //////////////////////////////////////

    @Hidden
    public void attachToInvoice() {
        Lease lease = getLeaseTerm().getLeaseItem().getLease();
        if (lease != null) {
            final AgreementRoleType landlord = agreementRoleTypes.find(LeaseConstants.ART_LANDLORD);
            final AgreementRoleType tenant = agreementRoleTypes.find(LeaseConstants.ART_TENANT);

            AgreementRole role = lease.findRoleWithType(landlord, getDueDate());
            Party seller = role.getParty();
            Party buyer = lease.findRoleWithType(tenant, getDueDate()).getParty();
            PaymentMethod paymentMethod = getLeaseTerm().getLeaseItem().getPaymentMethod();
            Invoice invoice = invoices.findMatchingInvoice(seller, buyer, paymentMethod, lease, InvoiceStatus.NEW, getDueDate());
            if (invoice == null) {
                invoice = invoices.newInvoice();
                invoice.setBuyer(buyer);
                invoice.setSeller(seller);
                invoice.setProvenance(lease);
                invoice.setDueDate(getDueDate());
                invoice.setPaymentMethod(paymentMethod);
                invoice.setStatus(InvoiceStatus.NEW);
            }
//            Integer identityHashCode = System.identityHashCode(this);
            // setSequence(BigInteger.valueOf(identityHashCode.longValue()));
            setSequence(invoice.nextItemSequence());
            this.modifyInvoice(invoice);
        }
    }

    // //////////////////////////////////////

    @Hidden
    public void remove() {
        // no safeguard, assuming being called with precaution
        clearLeaseTerm();
        super.remove();
    }

    // //////////////////////////////////////

    protected AgreementTypes agreementTypes;

    public void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

    protected AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    private InvoicesForLease invoices;

    @Hidden
    public void injectInvoices(InvoicesForLease invoices) {
        this.invoices = invoices;
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(InvoiceItem other) {
        int compare = super.compareTo(other);
        if(compare != 0) {
            return compare;
        }
        if(other instanceof InvoiceItemForLease) {
            return ORDERING_BY_LEASE_TERM.compare(this, (InvoiceItemForLease) other);
        } 
        return getClass().getName().compareTo(other.getClass().getName());
    }

    public final static Ordering<InvoiceItemForLease> ORDERING_BY_LEASE_TERM = new Ordering<InvoiceItemForLease>() {
        public int compare(InvoiceItemForLease p, InvoiceItemForLease q) {
            return Ordering.natural().nullsFirst().compare(p.getLeaseTerm(), q.getLeaseTerm());
        }
    };

}
