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
import java.util.List;
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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.apptenancy.WithApplicationTenancyAny;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.financial.bankaccount.BankAccount;
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
                name = "findByApplicationTenancyPathAndSellerAndDueDateAndStatus", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE " +
                        "seller == :seller && " +
                        "applicationTenancyPath == :applicationTenancyPath && " +
                        "status == :status && " +
                        "dueDate == :dueDate " +
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
                        "ORDER BY invoiceDate DESC"),
        @javax.jdo.annotations.Query(
                name = "findBySeller", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE seller == :seller " +
                        "ORDER BY invoiceDate DESC"),
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE lease == :lease " +
                        "ORDER BY invoiceDate DESC"),
        @javax.jdo.annotations.Query(
                name = "findByRunId", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE runId == :runId "),
        @javax.jdo.annotations.Query(
                name = "findByRunIdAndApplicationTenancyPath", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE runId == :runId && applicationTenancyPath == :applicationTenancyPath"),
        @javax.jdo.annotations.Query(
                name = "findByInvoiceNumber", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE invoiceNumber.matches(:invoiceNumber) "
                        + "ORDER BY invoiceDate DESC")
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
        @Index(name = "Invoice_invoiceNumber_IDX",
                members = { "invoiceNumber" })
})
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Invoice
        extends EstatioDomainObject<Invoice>
        implements WithApplicationTenancyAny, WithApplicationTenancyPathPersisted {

    public Invoice() {
        super("invoiceNumber, collectionNumber, buyer, dueDate, lease, uuid");
    }

    private String uuid;

    @Property(hidden = Where.EVERYWHERE, optionality = Optionality.OPTIONAL)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    // //////////////////////////////////////

    private String applicationTenancyPath;

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    public String getApplicationTenancyPath() {
        return applicationTenancyPath;
    }

    public void setApplicationTenancyPath(final String applicationTenancyPath) {
        this.applicationTenancyPath = applicationTenancyPath;
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
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

    @Property(hidden = Where.OBJECT_FORMS)
    public String getNumber() {
        return ObjectUtils.firstNonNull(
                getInvoiceNumber(),
                getCollectionNumber(),
                title());
    }

    // //////////////////////////////////////

    private Party buyer;

    @javax.jdo.annotations.Column(name = "buyerPartyId", allowsNull = "false")
    public Party getBuyer() {
        return buyer;

    }

    public void setBuyer(final Party buyer) {
        this.buyer = buyer;
    }

    // //////////////////////////////////////

    private Party seller;

    @javax.jdo.annotations.Column(name = "sellerPartyId", allowsNull = "false")
    @Property(hidden = Where.ALL_TABLES)
    public Party getSeller() {
        return seller;
    }

    public void setSeller(final Party seller) {
        this.seller = seller;
    }

    // //////////////////////////////////////

    private String collectionNumber;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.Invoice.NUMBER)
    @Property(hidden = Where.ALL_TABLES)
    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(final String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    // //////////////////////////////////////

    private String invoiceNumber;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.Invoice.NUMBER)
    @Property(hidden = Where.ALL_TABLES)
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    // //////////////////////////////////////

    private String runId;

    @Property(hidden = Where.EVERYWHERE, optionality = Optionality.OPTIONAL)
    public String getRunId() {
        return runId;
    }

    public void setRunId(final String runId) {
        this.runId = runId;
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
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
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void changeDueDate(
            final @ParameterLayout(named = "Due date") LocalDate dueDate) {
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
    @Property(hidden = Where.ALL_TABLES)
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
            final @ParameterLayout(named = "Reason") String reason) {
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

    @CollectionLayout(render = RenderType.EAGERLY)
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
    @Property(hidden = Where.EVERYWHERE)
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

    @Property(notPersisted = true)
    public BigDecimal getNetAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getNetAmount());
        }
        return total;
    }

    @Property(notPersisted = true, hidden = Where.ALL_TABLES)
    public BigDecimal getVatAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getVatAmount());
        }
        return total;
    }

    @Property(notPersisted = true)
    public BigDecimal getGrossAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : getItems()) {
            total = total.add(item.getGrossAmount());
        }
        return total;
    }

    // //////////////////////////////////////

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
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

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public Invoice collect(
            final @ParameterLayout(named = "Are you sure?") Boolean confirm
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
        final Numerator numerator = collectionNumerator();
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
        final Numerator numerator = collectionNumerator();
        setCollectionNumber(numerator.nextIncrementStr());
        return this;
    }

    private Numerator collectionNumerator() {
        return estatioNumeratorRepository.findCollectionNumberNumerator();
    }

    @Programmatic
    public void createPaymentTerms() {

    }

    // //////////////////////////////////////

    public Invoice invoice(
            final @ParameterLayout(named = "Invoice date") LocalDate invoiceDate,
            final @ParameterLayout(named = "Are you sure?") Boolean confirm) {
        return doInvoice(invoiceDate);
    }

    @Programmatic
    public Invoice doInvoice(
            final @ParameterLayout(named = "Invoice date") LocalDate invoiceDate) {
        // bulk action, so need these guards
        if (disableInvoice(invoiceDate, true) != null) {
            return this;
        }
        if (!validInvoiceDate(invoiceDate)) {
            warnUser(String.format(
                    "Invoice date %d is invalid for %s becuase it's before the invoice date of the last invoice",
                    invoiceDate.toString(),
                    getContainer().titleOf(this)));
            return this;
        }
        final Numerator numerator = estatioNumeratorRepository.findInvoiceNumberNumerator(getFixedAsset(), getApplicationTenancy());
        setInvoiceNumber(numerator.nextIncrementStr());
        setInvoiceDate(invoiceDate);
        this.setStatus(InvoiceStatus.INVOICED);
        informUser("Assigned " + this.getInvoiceNumber() + " to invoice " + getContainer().titleOf(this));
        return this;
    }

    public String disableInvoice(final LocalDate invoiceDate, Boolean confirm) {
        if (getInvoiceNumber() != null) {
            return "Invoice number already assigned";
        }
        final Numerator numerator = estatioNumeratorRepository.findInvoiceNumberNumerator(getFixedAsset(), getApplicationTenancy());
        if (numerator == null) {
            return "No 'invoice number' numerator found for invoice's property";
        }
        if (getStatus() != InvoiceStatus.APPROVED) {
            return "Must be in status of 'Invoiced'";
        }
        return null;
    }

    // //////////////////////////////////////

    @Programmatic
    boolean validInvoiceDate(LocalDate invoiceDate) {
        if (getDueDate() != null && getDueDate().compareTo(invoiceDate) < 0) {
            return false;
        }
        final ApplicationTenancy applicationTenancy = getApplicationTenancy();
        final Numerator numerator = estatioNumeratorRepository.findInvoiceNumberNumerator(getFixedAsset(), applicationTenancy);
        if (numerator != null) {
            final String invoiceNumber = numerator.lastIncrementStr();
            if (invoiceNumber != null) {
                List<Invoice> result = invoices.findByInvoiceNumber(invoiceNumber);
                if (result.size() > 0) {
                    return result.get(0).getInvoiceDate().compareTo(invoiceDate) <= 0;
                }
            }
        }
        return true;
    }

    // //////////////////////////////////////

    public InvoiceItem newItem(
            final Charge charge,
            final @ParameterLayout(named = "Quantity") BigDecimal quantity,
            final @ParameterLayout(named = "Net amount") BigDecimal netAmount,
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
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
    // for the moment, might be generalized (to the user) in the future
    @Property(hidden = Where.PARENTED_TABLES)
    @PropertyLayout(named = "Property")
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
    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    public BankMandate getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(final BankMandate paidBy) {
        this.paidBy = paidBy;
    }

    // //////////////////////////////////////

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
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

    @javax.inject.Inject
    EstatioNumeratorRepository estatioNumeratorRepository;

    @javax.inject.Inject
    Invoices invoices;

    @javax.inject.Inject
    InvoiceItems invoiceItems;

}
