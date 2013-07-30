/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease.invoicing;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
        name = "findByLeaseAndStartDateAndDueDate", language = "JDOQL", 
        value = "SELECT " + 
                "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " + 
                "WHERE leaseTerm.leaseItem.lease.reference.matches(:leaseReference) " + 
                "&& dueDate == :dueDate " + 
                "&& startDate == :startDate"),
    @javax.jdo.annotations.Query(
        name = "findByInvoiceAndLeaseTermAndStartDate", language = "JDOQL", 
        value = "SELECT " + 
                "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " + 
                "WHERE invoice == :invoice " + 
                "&& leaseTerm == :leaseTerm " + 
                "&& startDate == :startDate"),
    @javax.jdo.annotations.Query(
        name = "findByInvoiceAndLeaseTermAndEndDate", language = "JDOQL", 
        value = "SELECT " + 
                "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " + 
                "WHERE invoice == :invoice " + 
                "&& leaseTerm == :leaseTerm " + 
                "&& endDate == :endDate")
})
public class InvoiceItemForLease extends InvoiceItem {

    @javax.jdo.annotations.Column(name="LEASETERM_ID")
    private LeaseTerm leaseTerm;

    @Disabled
    @Hidden(where = Where.REFERENCES_PARENT)
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
            final AgreementRoleType landlord = agreementRoleTypes.findByTitle(LeaseConstants.ART_LANDLORD);
            final AgreementRoleType tenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);

            AgreementRole role = lease.findRoleWithType(landlord, getDueDate());
            Party seller = role.getParty();
            Party buyer = lease.findRoleWithType(tenant, getDueDate()).getParty();
            PaymentMethod paymentMethod = getLeaseTerm().getLeaseItem().getPaymentMethod();
            Invoice invoice = invoices.findMatchingInvoice(seller, buyer, paymentMethod, lease, InvoiceStatus.NEW, getDueDate());
            if (invoice == null) {
                invoice = invoices.newInvoice();
                invoice.setBuyer(buyer);
                invoice.setSeller(seller);
                invoice.setSource(lease);
                invoice.setDueDate(getDueDate());
                invoice.setPaymentMethod(paymentMethod);
                invoice.setStatus(InvoiceStatus.NEW);
            }
//            Integer identityHashCode = System.identityHashCode(this);
            // setSequence(BigInteger.valueOf(identityHashCode.longValue()));
            setSequence(invoice.nextItemSequence());
            this.setInvoice(invoice);
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

    private Invoices invoices;

    public void injectInvoices(Invoices invoices) {
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
            // unnecessary, but keeps findbugs happy...
            if(p == null && q == null) return 0;
            if(p == null) return -1;
            if(q == null) return +1;
            return Ordering.natural().nullsFirst().compare(p.getLeaseTerm(), q.getLeaseTerm());
        }
    };


}
