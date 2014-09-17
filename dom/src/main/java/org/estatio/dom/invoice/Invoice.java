/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findMatchingInvoices", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE " +
                        "lease == :lease && " +
                        "seller == :seller && " +
                        "buyer == :buyer && " +
                        "paymentMethod == :paymentMethod && " +
                        "status == :status && " +
                        "dueDate == :dueDate"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "status == :status " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndDueDateAndStatus", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "status == :status && " +
                        "dueDate == :dueDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndDueDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "dueDate == :dueDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE status == :status " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByBuyer", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE buyer == :buyer " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE lease == :lease "),
        @javax.jdo.annotations.Query(
                name = "findByRunId", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE runId == :runId "),
        @javax.jdo.annotations.Query(
                name = "findByInvoiceNumber", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE invoiceNumber.matches(:invoiceNumber) ")
})
@Indices({
        @Index(name = "Invoice_runId_IDX",
                members = { "runId" }),
        @Index(name = "Invoice_fixedAsset_status_IDX",
                members = { "fixedAsset", "status" }),
        @Index(name = "Invoice_fixedAsset_dueDate_IDX",
                members = { "fixedAsset", "dueDate" }),
        @Index(name = "Invoice_fixedAsset_dueDate_status_IDX",
                members = { "fixedAsset", "dueDate", "status" }),
        @Index(name = "Invoice_Lease_Seller_Buyer_PaymentMethod_DueDate_Status_IDX",
                members = { "lease", "seller", "buyer", "paymentMethod", "dueDate", "status" }),
})
@Bookmarkable
@Immutable
public class Invoice extends EstatioMutableObject<Invoice> {

    public Invoice() {
        super("invoiceNumber, collectionNumber, buyer, dueDate, lease, uuid");
    }

    private String uuid;

    @Hidden
    @Optional
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    // //////////////////////////////////////

    public String title() {
        if (getInvoiceNumber() != null) {
            return String.format("Invoice %s", getInvoiceNumber());
        }
        if (getCollectionNumber() != null) {
            return String.format("Collection %s", getCollectionNumber());
        }
        return String.format("Temp *%08d", Integer.parseInt(getId()));
    }

    // //////////////////////////////////////

    @Hidden(where = Where.OBJECT_FORMS)
    public String getNumber() {
        return ObjectUtils.firstNonNull(
                getInvoiceNumber(),
                getCollectionNumber(),
                title());
    }

    // //////////////////////////////////////

    private Party buyer;

    @javax.jdo.annotations.Column(name = "buyerPartyId", allowsNull = "false")
    @Disabled
    public Party getBuyer() {
        return buyer;

    }

    public void setBuyer(final Party buyer) {
        this.buyer = buyer;
    }

    // //////////////////////////////////////

    private Party seller;

    @javax.jdo.annotations.Column(name = "sellerPartyId", allowsNull = "false")
    @Disabled
    @Hidden(where = Where.ALL_TABLES)
    public Party getSeller() {
        return seller;
    }

    public void setSeller(final Party seller) {
        this.seller = seller;
    }

    // //////////////////////////////////////

    private String collectionNumber;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.Invoice.NUMBER)
    @Disabled
    @Hidden(where = Where.ALL_TABLES)
    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(final String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    // //////////////////////////////////////

    private String invoiceNumber;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.Invoice.NUMBER)
    @Disabled
    @Hidden(where = Where.ALL_TABLES)
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    // //////////////////////////////////////

    private String runId;

    @Disabled
    @Optional
    @Hidden(where = Where.ALL_TABLES)
    public String getRunId() {
        return runId;
    }

    public void setRunId(final String runId) {
        this.runId = runId;
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "true")
    @Optional
    @Disabled
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate invoiceDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
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

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Disabled
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void changeDueDate(
            final @Named("Due date") LocalDate dueDate) {
        setDueDate(dueDate);
    }

    public LocalDate default0ChangeDueDate(
            final LocalDate dueDate) {
        return getDueDate();
    }

    public String disableChangeDueDate(
            final LocalDate dueDate) {
        if (!getStatus().invoiceIsChangable()) {
            return "Due date can't be changed";
        }
        return null;
    }

    // //////////////////////////////////////

    private InvoiceStatus status;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.STATUS_ENUM)
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
    @javax.jdo.annotations.Column(name = "currencyId", allowsNull = "true")
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    // //////////////////////////////////////

    private PaymentMethod paymentMethod;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.PAYMENT_METHOD_ENUM)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Invoice changePaymentMethod(
            final PaymentMethod paymentMethod,
            final @Named("Reason") String reason) {
        setPaymentMethod(paymentMethod);
        return this;
    }

    public PaymentMethod default0ChangePaymentMethod() {
        return getPaymentMethod();
    }

    public String disableChangePaymentMethod(
            final PaymentMethod paymentMethod,
            final String reason) {
        return getStatus().invoiceIsChangable() ? null : "Invoice cannot be changed";
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

    @javax.jdo.annotations.Column(allowsNull = "true")
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

    @Hidden(where = Where.ALL_TABLES)
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
        doApprove();
        return this;
    }

    public boolean hideApprove() {
        return false;
    }

    public String disableApprove() {
        return getStatus() != InvoiceStatus.NEW ? "Can only approve 'new' invoices" : null;
    }

    @Programmatic
    public void doApprove() {
        // Bulk guard
        if (!hideApprove() && disableApprove() == null) {
            setStatus(InvoiceStatus.APPROVED);
            setRunId(null);
        }
    }

    // //////////////////////////////////////

    @Bulk
    public Invoice collect(
            final @Named("Are you sure?") Boolean confirm
            ) {
        return doCollect();
    }

    public boolean hideCollect() {
        // only applies to direct debits
        return !getPaymentMethod().isDirectDebit();
    }

    public String disableCollect(Boolean confirm) {
        if (getCollectionNumber() != null) {
            return "Collection number already assigned";
        }
        final Numerator numerator = invoices.findCollectionNumberNumerator();
        if (numerator == null) {
            return "No 'collection number' numerator found for invoice's property";
        }
        if (getStatus() != InvoiceStatus.APPROVED) {
            return "Must be in status of 'approved'";
        }
        if (getLease() == null) {
            return "No lease related to invoice";
        }
        if (getLease().getPaidBy() == null) {
            return String.format("No mandate assigned to invoice's lease");
        }
        final BankAccount bankAccount = (BankAccount) getLease().getPaidBy().getBankAccount();
        if (!bankAccount.isValidIban()) {
            return "The Iban code is invalid";
        }
        return null;
    }

    // perhaps we should also store the specific bank mandate on the invoice
    // that we want to deduct the money from
    // is this a concept of account then?

    @Programmatic
    public Invoice doCollect() {
        if (hideCollect()) {
            return this;
        }
        if (disableCollect(true) != null) {
            return this;
        }
        final Numerator numerator = invoices.findCollectionNumberNumerator();
        setCollectionNumber(numerator.increment());
        return this;
    }

    @Programmatic
    public void createPaymentTerms() {

    }

    // //////////////////////////////////////

    public Invoice invoice(
            final @Named("Invoice date") LocalDate invoiceDate,
            final @Named("Are you sure?") Boolean confirm) {
        return doInvoice(invoiceDate);
    }

    @Programmatic
    public Invoice doInvoice(
            final @Named("Invoice date") LocalDate invoiceDate) {
        // bulk action, so need these guards
        if (disableInvoice(invoiceDate, true) != null) {
            return this;
        }
        final Numerator numerator = invoices.findInvoiceNumberNumerator(getFixedAsset());
        setInvoiceNumber(numerator.increment());
        setInvoiceDate(invoiceDate);
        this.setStatus(InvoiceStatus.INVOICED);
        informUser("Assigned " + this.getInvoiceNumber() + " to invoice " + getContainer().titleOf(this));
        return this;
    }

    public String disableInvoice(final LocalDate invoiceDate, Boolean confirm) {
        if (getInvoiceNumber() != null) {
            return "Invoice number already assigned";
        }
        final Numerator numerator = invoices.findInvoiceNumberNumerator(getFixedAsset());
        if (numerator == null) {
            return "No 'invoice number' numerator found for invoice's property";
        }
        // TODO: offload valid next states to the InvoiceStatus enum? Eg
        // getStatus.isPossible(InvoiceStatus.APPROVED)
        //
        if (getStatus() != InvoiceStatus.APPROVED) {
            return "Must be in status of 'Invoiced'";
        }
        return null;
    }

    // //////////////////////////////////////

    public InvoiceItem newItem(
            final Charge charge,
            final @Named("Quantity") BigDecimal quantity,
            final @Named("Net amount") BigDecimal netAmount,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
        InvoiceItem invoiceItem = invoiceItems.newInvoiceItem(this, getDueDate());
        invoiceItem.setQuantity(quantity);
        invoiceItem.setCharge(charge);
        invoiceItem.setDescription(charge.getDescription());
        invoiceItem.setTax(charge.getTax());
        invoiceItem.setNetAmount(netAmount);
        invoiceItem.setStartDate(startDate);
        invoiceItem.setEndDate(endDate);
        invoiceItem.verify();
        // TODO: we need to create a new subclass InvoiceForLease but that
        // requires a database change so this is quick fix
        InvoiceItemForLease invoiceItemForLease = (InvoiceItemForLease) invoiceItem;
        invoiceItemForLease.setLease(getLease());
        if (getLease() != null && getLease().getOccupancies() != null && getLease().getOccupancies().first() != null) {
            invoiceItemForLease.setFixedAsset(getLease().getOccupancies().first().getUnit());
        }
        return invoiceItemForLease;
    }

    public BigDecimal default1NewItem() {
        return BigDecimal.ONE;
    }

    public String validateNewItem(
            final Charge charge,
            final BigDecimal quantity,
            final BigDecimal netAmount,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (startDate != null && endDate == null) {
            return "Also enter an end date when using a start date";
        }
        if (ObjectUtils.compare(startDate, endDate) > 0) {
            return "Start date must be before end date";
        }
        return null;
    }

    // //////////////////////////////////////

    private FixedAsset fixedAsset;

    /**
     * Derived from the {@link #getLease() lease}, but safe to persist since
     * business rule states that we never generate invoices for invoice items
     * that relate to different properties.
     * 
     * <p>
     * Another reason for persisting this is that it allows eager validation
     * when attaching additional {@link InvoiceItem}s to an invoice, to check
     * that they relate to the same fixed asset.
     */
    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    @Named("Property")
    // for the moment, might be generalized (to the user) in the future
    @Disabled
    public FixedAsset getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(final FixedAsset fixedAsset) {
        this.fixedAsset = fixedAsset;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "paidByBankMandateId")
    private BankMandate paidBy;

    /**
     * Derived from the {@link #getLease() lease}, but safe to persist since
     * business rule states that all invoice items that are paid by
     * {@link BankMandate} (as opposed to simply by bank transfer) will be for
     * the same bank mandate.
     * 
     * <p>
     * Another reason for persisting this is that it allows eager validation
     * when attaching additional {@link InvoiceItem}s to an invoice, to check
     * that they relate to the same bank mandate (if they are to be paid by bank
     * mandate).
     */
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    public BankMandate getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(final BankMandate paidBy) {
        this.paidBy = paidBy;
    }

    // //////////////////////////////////////

    @Bulk
    @ActionSemantics(Of.IDEMPOTENT)
    public Invoice submitToCoda() {
        doCollect();
        return this;
    }

    public String disableSubmitToCoda() {
        return getStatus() == InvoiceStatus.INVOICED || getStatus() == InvoiceStatus.APPROVED
                ? null
                : "Must be approved or invoiced";
    }

    // //////////////////////////////////////

    @Bulk
    public void remove() {
        // Can be called as bulk so have a safeguard
        if (disableRemove() == null) {
            doRemove();
        }
    }

    public String disableRemove() {
        return getStatus().invoiceIsChangable() ? null : "Only invoices with status New can be removed.";
    }

    @Programmatic
    public void doRemove() {
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

    private InvoiceItems invoiceItems;

    public final void injectInvoiceItemsForLease(final InvoiceItems invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

}
