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
package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.publishing.InvoiceEagerlyRenderedPayloadFactory;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findMatchingInvoices", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.Invoice "
                        + "WHERE source == :source "
                        + "&& seller == :seller "
                        + "&& buyer == :buyer "
                        + "&& paymentMethod == :paymentMethod "
                        + "&& status == :status "
                        + "&& dueDate == :dueDate"),
        @javax.jdo.annotations.Query(
                name = "findByStatus", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.Invoice "
                        + "WHERE status == :status ")
})
@Bookmarkable
public class Invoice extends EstatioTransactionalObject<Invoice, InvoiceStatus> {

    public Invoice() {
        super("invoiceNumber", InvoiceStatus.NEW, null);
    }

    @Override
    public InvoiceStatus getLockable() {
        return getStatus();
    }

    @Override
    public void setLockable(final InvoiceStatus lockable) {
        setStatus(lockable);
    }

    // //////////////////////////////////////

    public String title() {
        return String.format("%08d", Integer.parseInt(getId()));
    }

    // //////////////////////////////////////

    private Party buyer;

    @javax.jdo.annotations.Column(name = "BUYER_ID", allowsNull="false")
    @Disabled
    public Party getBuyer() {
        return buyer;
    }

    public void setBuyer(final Party buyer) {
        this.buyer = buyer;
    }

    // //////////////////////////////////////

    private Party seller;

    @javax.jdo.annotations.Column(name = "SELLER_ID", allowsNull="false")
    @Disabled
    public Party getSeller() {
        return seller;
    }

    public void setSeller(final Party seller) {
        this.seller = seller;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull="true")
    private String collectionNumber;

    @Optional
    @Disabled
    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(final String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    // //////////////////////////////////////

    private String invoiceNumber;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Disabled
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    // //////////////////////////////////////

    private InvoiceSource source;

    /**
     * Polymorphic association to (any implementation of) {@link InvoiceSource}.
     */
    @javax.jdo.annotations.Persistent(
            extensions = { 
                    @Extension(vendorName = "datanucleus", 
                            key = "mapping-strategy", 
                            value = "per-implementation") })
    @javax.jdo.annotations.Column(name = "SOURCE_ID", allowsNull="false")
    @Disabled
    public InvoiceSource getSource() {
        return source;
    }

    public void setSource(final InvoiceSource source) {
        this.source = source;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate invoiceDate;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Disabled
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(final LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate dueDate;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // //////////////////////////////////////

    private InvoiceStatus status;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(final InvoiceStatus status) {
        this.status = status;
    }


    // //////////////////////////////////////

    private Currency currency;

    // REVIEW: invoice generation is not populating this field.
    @javax.jdo.annotations.Column(name = "CURRENCY_ID", allowsNull="true")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    // //////////////////////////////////////

    private PaymentMethod paymentMethod;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "invoice")
    private SortedSet<InvoiceItem> items = new TreeSet<InvoiceItem>();

    @Disabled
    @Render(Type.EAGERLY)
    public SortedSet<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(final SortedSet<InvoiceItem> items) {
        this.items = items;
    }

    // //////////////////////////////////////

    @Persistent
    private BigInteger lastItemSequence;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Hidden
    public BigInteger getLastItemSequence() {
        return lastItemSequence;
    }

    public void setLastItemSequence(final BigInteger lastItemSequence) {
        this.lastItemSequence = lastItemSequence;
    }

    
    @Programmatic
    public BigInteger nextItemSequence() {
        BigInteger nextItemSequence = getLastItemSequence() == null 
                ? BigInteger.ONE 
                : getLastItemSequence().add(BigInteger.ONE);
        setLastItemSequence(nextItemSequence);
        return nextItemSequence;
    }

    // //////////////////////////////////////

    @NotPersisted
    public BigDecimal getNetAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getNetAmount());
        }
        return total;
    }

    @NotPersisted
    public BigDecimal getVatAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getVatAmount());
        }
        return total;
    }

    @NotPersisted
    public BigDecimal getGrossAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getGrossAmount());
        }
        return total;
    }

    // //////////////////////////////////////


    @Bulk
    public Invoice approve() {
        setStatus(InvoiceStatus.APPROVED);
        return this;
    }

    public boolean hideApprove() {
        return false;
    }

    public String disableApprove() {
        return getStatus() != InvoiceStatus.NEW? "Can only approve 'new' invoices": null;
    }

    // //////////////////////////////////////

    @Bulk
    public Invoice assignCollectionNumber() {
        
        // bulk action, so need these guards
        if(hideAssignCollectionNumber()) {
            return this;
        }
        if(disableAssignCollectionNumber() != null) {
            return this;
        }
        
        final Numerator numerator = invoices.findCollectionNumberNumerator();
        
        setCollectionNumber(numerator.increment());
        this.setStatus(InvoiceStatus.COLLECTED);
        
        informUser("Assigned " + this.getCollectionNumber() + " to invoice " + getContainer().titleOf(this));
        return this;
    }
    
    public boolean hideAssignCollectionNumber() {
        // only applies to direct debits
        return !getPaymentMethod().isDirectDebit();
    }
    
    public String disableAssignCollectionNumber() {
        if(getCollectionNumber() != null) {
            return "Collection number already assigned";
        }
        
        final Numerator numerator = invoices.findCollectionNumberNumerator();
        if(numerator == null) {
            return "No 'collection number' numerator found for invoice's property";
        }
        
        if (getStatus() != InvoiceStatus.APPROVED) {
            return "Must be in status of 'approved'";
        }
        return null;
    }
    
    // //////////////////////////////////////

    @Bulk
    public Invoice assignInvoiceNumber() {
        // bulk action, so need these guards
        if(hideAssignInvoiceNumber()) {
            return this;
        }
        if(disableAssignInvoiceNumber() != null) {
            return this;
        }
        
        final Numerator numerator = invoices.findInvoiceNumberNumerator(getProperty());
        
        setInvoiceNumber(numerator.increment());
        this.setStatus(InvoiceStatus.INVOICED);
        
        informUser("Assigned " + this.getCollectionNumber() + " to invoice " + getContainer().titleOf(this));
        return this;
    }
    
    public boolean hideAssignInvoiceNumber() {
        return false;
    }
    
    public String disableAssignInvoiceNumber() {
        if(getInvoiceNumber() != null) {
            return "Invoice number already assigned";
        }
        final Numerator numerator = invoices.findInvoiceNumberNumerator(getProperty());
        if(numerator == null) {
            return "No 'invoice number' numerator found for invoice's property";
        }
        if (getStatus() != InvoiceStatus.COLLECTED) {
            return "Must be in status of 'collected'";
        }
        return null;
    }

    // //////////////////////////////////////
    
    /**
     * Derived from the {@link #getSource() invoice source}.
     */
    public Property getProperty() {
        return getSource().getProperty();
    }

    // //////////////////////////////////////

    @PublishedAction(InvoiceEagerlyRenderedPayloadFactory.class)
    @Bulk
    @ActionSemantics(Of.IDEMPOTENT)
    public Invoice submitToCoda() {
        assignCollectionNumber();
        return this;
    }

    public String disableSubmitToCoda() {
        if (getPaymentMethod().isDirectDebit()) {
            return getStatus() == InvoiceStatus.COLLECTED ||
                    getStatus() == InvoiceStatus.INVOICED
                    ? null
                    : "Must be collected or invoiced";
        } else {
            return getStatus() == InvoiceStatus.INVOICED
                    ? null
                    : "Must be invoiced";
        }
    }




    // //////////////////////////////////////

    @Prototype
    @Bulk
    public void remove() {
        for (InvoiceItem item : getItems()) {
            item.remove();
        }
        getContainer().remove(this);
    }

    // //////////////////////////////////////

    
    private Invoices invoices;
    public final void injectInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }
}
