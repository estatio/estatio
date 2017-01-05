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
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Predicate;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.user.UserService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.types.AtPathType;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyAny;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccount;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.leaseinvoicing.InvoiceItemForLease;
import org.estatio.dom.party.Party;
import org.estatio.dom.roles.EstatioRole;
import org.estatio.numerator.dom.impl.Numerator;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"   // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
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
                name = "findByApplicationTenancyPathAndSellerAndInvoiceDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE " +
                        "seller == :seller && " +
                        "applicationTenancyPath == :applicationTenancyPath && " +
                        "invoiceDate == :invoiceDate " +
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
                name = "findByFixedAssetAndInvoiceDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "invoiceDate == :invoiceDate " +
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
        ,@Index(name = "Invoice_sendTo_IDX",
                members = { "sendTo" })
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.invoice.Invoice"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Invoice
        extends UdoDomainObject2<Invoice>
        implements WithApplicationTenancyAny, WithApplicationTenancyPathPersisted {

    public Invoice() {
        super("invoiceNumber, collectionNumber, buyer, dueDate, lease, uuid");
    }


    @Property(hidden = Where.EVERYWHERE, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String uuid;

    @javax.jdo.annotations.Column(
            length = AtPathType.Meta.MAX_LEN,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

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

    @javax.jdo.annotations.Column(name = "buyerPartyId", allowsNull = "false")
    @Getter @Setter
    private Party buyer;

    @javax.jdo.annotations.Column(name = "sellerPartyId", allowsNull = "false")
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Party seller;

    @javax.jdo.annotations.Column(allowsNull = "true", length = InvoiceNumberType.Meta.MAX_LEN)
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String collectionNumber;

    @javax.jdo.annotations.Column(allowsNull = "true", length = InvoiceNumberType.Meta.MAX_LEN)
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String invoiceNumber;

    @Property(hidden = Where.EVERYWHERE, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String runId;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private Lease lease;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate invoiceDate;

    @javax.jdo.annotations.Column(name = "sendToCommunicationChannelId", allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private CommunicationChannel sendTo;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate dueDate;

    @Mixin
    public static class _changeDueDate {

        private final Invoice invoice;

        public _changeDueDate(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public void $$(
                final LocalDate dueDate) {
            invoice.setDueDate(dueDate);
        }

        public LocalDate default0$$(
                final LocalDate dueDate) {
            return invoice.getDueDate();
        }

        public String disable$$(
                final LocalDate dueDate) {
            if (invoice.isImmutable()) {
                return "Due date can't be changed";
            }
            return null;
        }

    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = InvoiceStatus.Meta.MAX_LEN)
    @Getter @Setter
    private InvoiceStatus status;


    // REVIEW: invoice generation is not populating this field.
    @javax.jdo.annotations.Column(name = "currencyId", allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Currency currency;


    @javax.jdo.annotations.Column(allowsNull = "false", length = PaymentMethod.Meta.MAX_LEN)
    @Getter @Setter
    private PaymentMethod paymentMethod;


    @Mixin
    public static class _changePaymentMethod {

        private final Invoice invoice;

        public _changePaymentMethod(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Invoice $$(
                final PaymentMethod paymentMethod,
                @ParameterLayout(describedAs = "Not currently used")
                final String reason) {
            invoice.setPaymentMethod(paymentMethod);
            return invoice;
        }

        public PaymentMethod default0$$() {
            return invoice.getPaymentMethod();
        }

        public String disable$$(
                final PaymentMethod paymentMethod,
                final String reason) {
            return invoice.getStatus().invoiceIsChangable() ? null : "Invoice cannot be changed";
        }
    }

    @CollectionLayout(defaultView = "table")
    @javax.jdo.annotations.Persistent(mappedBy = "invoice")
    @Getter @Setter
    private SortedSet<InvoiceItem> items = new TreeSet<>();


    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(hidden = Where.EVERYWHERE)
    @Persistent
    @Getter @Setter
    private BigInteger lastItemSequence;

    @Programmatic
    public BigInteger nextItemSequence() {
        BigInteger nextItemSequence = getLastItemSequence() == null
                ? BigInteger.ONE
                : getLastItemSequence().add(BigInteger.ONE);
        setLastItemSequence(nextItemSequence);
        return nextItemSequence;
    }


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

    @Mixin
    public static class _approve {

        private final Invoice invoice;

        public _approve(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Invoice $$() {
            doApprove();
            return invoice;
        }

        public boolean hide$$() {
            return false;
        }

        public String disable$$() {
            return invoice.getStatus() != InvoiceStatus.NEW ? "Can only approve 'new' invoices" : null;
        }

        @Programmatic
        public void doApprove() {
            // Bulk guard
            if (!hide$$() && disable$$() == null) {
                invoice.setStatus(InvoiceStatus.APPROVED);
                invoice.setRunId(null);
            }
        }

    }


    // //////////////////////////////////////

    @Mixin
    public static class _collect {

        private final Invoice invoice;

        public _collect(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Invoice $$() {
            return doCollect();
        }

        public boolean hide$$() {
            // only applies to direct debits
            return !invoice.getPaymentMethod().isDirectDebit();
        }

        public String disable$$() {
            if (invoice.getCollectionNumber() != null) {
                return "Collection number already assigned";
            }
            final Numerator numerator = collectionNumerator();
            if (numerator == null) {
                return "No 'collection number' numerator found for invoice's property";
            }
            if (invoice.getStatus() != InvoiceStatus.APPROVED) {
                return "Must be in status of 'approved'";
            }
            if (invoice.getLease() == null) {
                return "No lease related to invoice";
            }
            if (invoice.getLease().getPaidBy() == null) {
                return String.format("No mandate assigned to invoice's lease");
            }
            final BankAccount bankAccount = (BankAccount) invoice.getLease().getPaidBy().getBankAccount();
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
            if (hide$$()) {
                return invoice;
            }
            if (disable$$() != null) {
                return invoice;
            }
            final Numerator numerator = collectionNumerator();
            invoice.setCollectionNumber(numerator.nextIncrementStr());
            return invoice;
        }

        private Numerator collectionNumerator() {
            return numeratorRepository.findCollectionNumberNumerator();
        }

        @javax.inject.Inject
        NumeratorForCollectionRepository numeratorRepository;

    }



    @Programmatic
    public void createPaymentTerms() {

    }

    // //////////////////////////////////////

    @Mixin
    public static class _invoice {
        private final Invoice invoice;

        public _invoice(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Invoice $$(final LocalDate invoiceDate) {

            if (disable$$(invoiceDate) != null) {
                return invoice; // Safeguard to do nothing when called without a wrapper.
            }

            final Numerator numerator = numeratorRepository
                    .findInvoiceNumberNumerator(invoice.getFixedAsset(), invoice.getApplicationTenancy());

            invoice.setInvoiceNumber(numerator.nextIncrementStr());
            invoice.setInvoiceDate(invoiceDate);
            invoice.setStatus(InvoiceStatus.INVOICED);

            messageService.informUser("Assigned " + invoice.getInvoiceNumber() + " to invoice " + titleService.titleOf(invoice));
            return invoice;
        }

        public String disable$$(final LocalDate invoiceDate) {
            if (invoice.getInvoiceNumber() != null) {
                return "Invoice number already assigned";
            }
            final Numerator numerator = numeratorRepository
                    .findInvoiceNumberNumerator(invoice.getFixedAsset(), invoice.getApplicationTenancy());
            if (numerator == null) {
                return "No 'invoice number' numerator found for invoice's property";
            }
            if (invoice.getStatus() != InvoiceStatus.APPROVED) {
                return "Must be in status of 'Approved'";
            }
            return null;
        }

        public String validate$$(final LocalDate invoiceDate) {
            return validInvoiceDate(invoiceDate);
        }

        String validInvoiceDate(LocalDate invoiceDate) {
            if (invoice.getDueDate() != null && invoice.getDueDate().compareTo(invoiceDate) < 0) {
                return String.format("Invoice date must be on or before the due date (%s)", invoice.getDueDate().toString());
            }
            final ApplicationTenancy applicationTenancy = invoice.getApplicationTenancy();
            final Numerator numerator = numeratorRepository.findInvoiceNumberNumerator(invoice.getFixedAsset(), applicationTenancy);
            if (numerator != null) {
                final String invoiceNumber = numerator.lastIncrementStr();
                if (invoiceNumber != null) {
                    List<Invoice> result = invoiceRepository.findByInvoiceNumber(invoiceNumber);
                    if (result.size() > 0) {
                        final Invoice invoice = result.get(0);
                        if (invoice.getInvoiceDate().isAfter(invoiceDate)){
                            return String.format("Invoice number %s has an invoice date %s which is after %s", invoice.getInvoiceNumber(), invoice.getInvoiceDate().toString(), invoiceDate.toString());
                        }
                    }
                }
            }
            return null;
        }

        @javax.inject.Inject
        NumeratorForCollectionRepository numeratorRepository;

        @javax.inject.Inject
        InvoiceRepository invoiceRepository;

        @javax.inject.Inject
        MessageService messageService;

        @javax.inject.Inject
        TitleService titleService;
    }




    // //////////////////////////////////////

    @Mixin
    public static class _newItem {

        private final Invoice invoice;

        public _newItem(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public InvoiceItem $$(
                final Charge charge,
                final BigDecimal quantity,
                final BigDecimal netAmount,
                final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
                final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
            InvoiceItem invoiceItem = invoiceItemRepository.newInvoiceItem(invoice, invoice.getDueDate());
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
            invoiceItemForLease.setLease(invoice.getLease());
            if (invoice.getLease() != null && invoice.getLease().primaryOccupancy().isPresent()) {
                invoiceItemForLease.setFixedAsset(invoice.getLease().primaryOccupancy().get().getUnit());
            }
            return invoiceItemForLease;
        }

        public BigDecimal default1$$() {
            return BigDecimal.ONE;
        }

        public String validate$$(
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
            if (startDate == null && endDate == null) {
                messageService.warnUser("Both start date and end date are empty. Is this done intentionally?");
            }
            if (startDate == null && endDate != null) {
                messageService.warnUser("Start date is empty. Is this done intentionally?");
            }
            return null;
        }

        public String disable$$(
                final Charge charge,
                final BigDecimal quantity,
                final BigDecimal netAmount,
                final LocalDate startDate,
                final LocalDate endDate){
            return invoice.isImmutable() ? "Cannot add new item" : null;
        }

        @javax.inject.Inject
        InvoiceItemRepository invoiceItemRepository;

        @javax.inject.Inject
        MessageService messageService;

    }



    boolean isImmutable() {
        return !getStatus().invoiceIsChangable();
    }

    // //////////////////////////////////////

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
    @Getter @Setter
    private FixedAsset fixedAsset;


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
    @javax.jdo.annotations.Column(name = "paidByBankMandateId")
    @Getter @Setter
    private BankMandate paidBy;


    // //////////////////////////////////////

    /**
     * It's the responsibility of the invoice to be able to determine which seller's bank account is to be paid into by the buyer.
     */
    @Programmatic
    public FinancialAccount getSellerBankAccount() {
        if(getFixedAsset() == null) {
            return null;
        }
        // TODO: EST-xxxx to enforce the constraint that there can only be one "at any given time".
        final Optional<FixedAssetFinancialAccount> fafrIfAny =
                fixedAssetFinancialAccountRepository.findByFixedAsset(getFixedAsset()).stream().findFirst();
        return fafrIfAny.isPresent()? fafrIfAny.get().getFinancialAccount(): null;
    }


    @Mixin
    public static class _remove {

        private final Invoice invoice;

        public _remove(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public void $$() {
            // Can be called as bulk so have a safeguard
            if (disable$$() == null) {
                for (InvoiceItem item : invoice.getItems()) {
                    item.remove();
                }
                paperclipRepository.deleteIfAttachedTo(invoice, PaperclipRepository.Policy.PAPERCLIPS_AND_DOCUMENTS_IF_ORPHANED);
                repositoryService.remove(invoice);
            }
        }

        public String disable$$() {
            if (!invoice.getStatus().invoiceIsChangable()) {
                return "Only invoices with status New can be removed.";
            }
            return null;
        }

        @javax.inject.Inject
        PaperclipRepository paperclipRepository;

        @javax.inject.Inject
        RepositoryService repositoryService;

    }


    @Mixin
    public static class _saveAsHistoric {
        private final Invoice invoice;
        public _saveAsHistoric(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public void $$() {
            invoice.setStatus(InvoiceStatus.HISTORIC);
            invoice.setRunId(null);
        }

        public boolean hide$$(){
            return !EstatioRole.ADMINISTRATOR.hasRoleWithSuffix(userService.getUser());
        }

        @Inject
        UserService userService;
    }

    @javax.inject.Inject
    NumeratorForCollectionRepository numeratorRepository;

    @javax.inject.Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @javax.inject.Inject
    InvoiceRepository invoiceRepository;


    public static class Predicates {

        public static Predicate<Invoice> isChangeable() {
            return invoice -> invoice.getStatus().invoiceIsChangable();
        }

        public static Predicate<Invoice> noLongerChangeable() {
            return com.google.common.base.Predicates.not(Invoice.Predicates.isChangeable());
        }

    }


    public static class InvoiceNumberType {

        private InvoiceNumberType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public static final int MAX_LEN = 16;

            private Meta() {}

        }

    }
}
