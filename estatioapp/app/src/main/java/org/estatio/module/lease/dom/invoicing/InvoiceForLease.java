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
package org.estatio.module.lease.dom.invoicing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.InheritanceStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccount;
import org.estatio.module.assetfinancial.dom.FixedAssetFinancialAccountRepository;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.platform.docfragment.FragmentRenderService;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.invoicing.ssrs.InvoiceAttributesVM;
import org.estatio.module.lease.dom.invoicing.ssrs.InvoiceItemAttributesVM;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.numerator.dom.Numerator;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"    // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(
        "org.estatio.dom.invoice.Invoice" // backward compatibility, so don't have to migrate all bookmarks in auditing etc
        // (would rather use "lease.InvoiceForLease", but @Discriminator currently takes precedence over @DomainObject#objectType); see EST-1084
)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findMatchingInvoices", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE " +
                        "lease == :lease && " +
                        "seller == :seller && " +
                        "buyer == :buyer && " +
                        "paymentMethod == :paymentMethod && " +
                        "status == :status && " +
                        "dueDate == :dueDate"),
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE lease == :lease " +
                        "ORDER BY dueDate DESC"),
        @javax.jdo.annotations.Query(
                   name = "findByFixedAssetAndStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "status == :status " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndDueDateAndStatus", language = "JDOQL",
                value = "SELECT FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "status == :status && " +
                        "dueDate == :dueDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndDueDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "dueDate == :dueDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndInvoiceDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "invoiceDate == :invoiceDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByRunId", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE runId == :runId "),
        @javax.jdo.annotations.Query(
                name = "findByRunIdAndApplicationTenancyPath", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE runId == :runId && applicationTenancyPath == :applicationTenancyPath"),
        @javax.jdo.annotations.Query(
                name = "findByApplicationTenancyPathAndSellerAndDueDateAndStatus", language = "JDOQL",
                value = "SELECT FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE " +
                        "seller == :seller && " +
                        "applicationTenancyPath == :applicationTenancyPath && " +
                        "status == :status && " +
                        "dueDate == :dueDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByApplicationTenancyPathAndSellerAndInvoiceDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE " +
                        "seller == :seller && " +
                        "applicationTenancyPath == :applicationTenancyPath && " +
                        "invoiceDate == :invoiceDate " +
                        "ORDER BY invoiceNumber"),
})
@Indices({
        @Index(name = "Invoice_runId_IDX",
                members = { "runId" }),
        @Index(name = "Invoice_Lease_Seller_Buyer_PaymentMethod_DueDate_Status_IDX",
                members = { "lease", "seller", "buyer", "paymentMethod", "dueDate", "status" }),
        @Index(name = "Invoice_fixedAsset_status_IDX",
                members = { "fixedAsset", "status" }),
        @Index(name = "Invoice_fixedAsset_dueDate_IDX",
                members = { "fixedAsset", "dueDate" }),
        @Index(name = "Invoice_fixedAsset_dueDate_status_IDX",
                members = { "fixedAsset", "dueDate", "status" }),
})
@DomainObject(
        editing = Editing.DISABLED
        // objectType inferred from @Discriminator
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class InvoiceForLease
        extends Invoice<InvoiceForLease> {

    public InvoiceForLease() {
        super("invoiceNumber, collectionNumber, buyer, dueDate, lease, uuid");
    }

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private Lease lease;

    /**
     * Derived from the {@link #getLease() lease}, but safe to persist since
     * business rule states that we never generate invoices for invoice items
     * that relate to different properties.
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


    @Property(hidden = Where.EVERYWHERE, optionality = Optionality.OPTIONAL)
    @Column(length = 512)
    @Getter @Setter
    private String runId;

    @Programmatic
    public Occupancy getCurrentOccupancy() {
        final InvoiceForLease invoice =
                this;
        final Lease leaseIfAny = invoice.getLease();
        if (leaseIfAny == null) {
            return null;
        }
        final SortedSet<Occupancy> occupancies = leaseIfAny.getOccupancies();
        if (occupancies.isEmpty()) {
            return null;
        }
        return occupancies.first();
    }

    @Override
    protected String reasonDisabledDueToState(final Object viewContext) {
        return getStatus().invoiceIsChangable() ? null : "Invoice cannot be changed";
    }


    @Override
    protected String reasonDisabledFinanceDetailsDueToState(final Object viewContext) {
        return reasonDisabledDueToState(viewContext);
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin
    public static class _newItem {

        private final InvoiceForLease invoice;

        public _newItem(final InvoiceForLease invoice) {
            this.invoice = invoice;
        }

        @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public InvoiceItem $$(
                final Charge charge,
                final BigDecimal quantity,
                final BigDecimal netAmount,
                @Nullable
                final LocalDate startDate,
                @Nullable
                final LocalDate endDate) {

            InvoiceItemForLease invoiceItem = invoiceItemForLeaseRepository.newInvoiceItem(invoice, invoice.getDueDate());

            invoiceItem.setQuantity(quantity);
            invoiceItem.setCharge(charge);
            invoiceItem.setTax(charge.getTax());
            invoiceItem.setNetAmount(netAmount);
            invoiceItem.setStartDate(startDate);
            invoiceItem.setEndDate(endDate);

            final InvoiceItemAttributesVM vm = new InvoiceItemAttributesVM(invoiceItem);
            final String description = fragmentRenderService.render(vm, "description");
            invoiceItem.setDescription(description);

            invoiceItem.verify();
            // TODO: we need to create a new subclass InvoiceForLease but that
            // requires a database change so this is quick fix
            InvoiceItemForLease invoiceItemForLease = (InvoiceItemForLease) invoiceItem;
            invoiceItemForLease.setLease(invoice.getLease());
            if (invoice.getLease() != null && invoice.getLease().primaryOccupancy().isPresent()) {
                invoiceItemForLease.setFixedAsset(invoice.getLease().primaryOccupancy().get().getUnit());
            }
            invoice.updateDescriptions();
            return invoiceItemForLease;
        }

        public List<Charge> choices0$$() {
            return chargeRepository.allOutgoing();
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

        public String disable$$() {
            return invoice.isImmutableDueToState() ? "Cannot add new item" : null;
        }

        @Inject
        FragmentRenderService fragmentRenderService;

        @Inject
        InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

        @Inject
        MessageService messageService;

        @Inject
        ChargeRepository chargeRepository;

    }

    /**
     * TODO: inline this mixin: nb will need to update camel-config.xml when do so, and allow for a transition period
     */
    @Mixin
    public static class _collect {

        private final InvoiceForLease invoice;

        public _collect(final InvoiceForLease invoice) {
            this.invoice = invoice;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Invoice $$() {

            // Safeguards to do nothing if erroneously called without a wrapper.
            if (hide$$() || disable$$() != null) {
                return invoice;
            }

            final Numerator numerator = collectionNumerator();
            invoice.setCollectionNumber(numerator.nextIncrementStr());
            return invoice;
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
                // This is what disables the 'collect' functionality outside of Italy.
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

        // TODO: REVIEW, perhaps we should also store the specific bank mandate on the invoice that we want to deduct the money from.  Is this a concept of account then?

        private Numerator collectionNumerator() {
            return numeratorRepository.findCollectionNumberNumerator();
        }

        @javax.inject.Inject
        NumeratorForCollectionRepository numeratorRepository;

    }

    /**
     * TODO: inline this mixin
     */
    @Mixin
    public static class _approve {

        private final InvoiceForLease invoiceForLease;

        public _approve(final InvoiceForLease invoiceForLease) {
            this.invoiceForLease = invoiceForLease;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Invoice $$() {
            doApprove();
            return invoiceForLease;
        }

        public boolean hide$$() {
            return false;
        }

        public String disable$$() {
            return invoiceForLease.getStatus() != InvoiceStatus.NEW ? "Can only approve 'new' invoices" : null;
        }

        @Programmatic
        public void doApprove() {
            // Bulk guard
            if (!hide$$() && disable$$() == null) {
                invoiceForLease.setStatus(InvoiceStatus.APPROVED);
                invoiceForLease.setRunId(null);
                invoiceForLease.resetDescriptions();
            }
        }

    }

    /**
     * TODO: inline this mixin: nb will need to update camel-config.xml when do so, and allow for a transition period
     */
    @Mixin
    public static class _invoice {
        private final InvoiceForLease invoiceForLease;

        public _invoice(final InvoiceForLease invoiceForLease) {
            this.invoiceForLease = invoiceForLease;
        }

        @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Invoice $$(final LocalDate invoiceDate) {

            // Safeguards to do nothing if erroneously called without a wrapper.
            if (disable$$() != null || validate0$$(invoiceDate) != null) {
                return invoiceForLease;
            }

            final Numerator numerator = numeratorRepository
                    .findInvoiceNumberNumerator(invoiceForLease.getFixedAsset(), invoiceForLease.getApplicationTenancy());

            invoiceForLease.setInvoiceNumber(numerator.nextIncrementStr());
            invoiceForLease.setInvoiceDate(invoiceDate);
            invoiceForLease.setStatus(InvoiceStatus.INVOICED);

            messageService.informUser(
                    String.format("Assigned %s to invoice %s",
                            invoiceForLease.getInvoiceNumber(),
                            titleService.titleOf(invoiceForLease)));
            return invoiceForLease;
        }

        public String disable$$() {
            if (invoiceForLease.getInvoiceNumber() != null) {
                return "Invoice number already assigned";
            }
            final Numerator numerator = numeratorRepository
                    .findInvoiceNumberNumerator(invoiceForLease.getFixedAsset(), invoiceForLease.getApplicationTenancy());
            if (numerator == null) {
                return "No 'invoice number' numerator found for invoice's property";
            }
            if (invoiceForLease.getStatus() != InvoiceStatus.APPROVED) {
                return "Must be in status of 'Approved'";
            }
            return null;
        }

        public String validate0$$(final LocalDate invoiceDate) {
            return validInvoiceDate(invoiceDate);
        }

        public LocalDate default0$$() {
            LocalDate today = clockService.now();
            LocalDate dueDate = invoiceForLease.getDueDate();
            return today.isAfter(dueDate) ? dueDate : null;
        }

        String validInvoiceDate(LocalDate invoiceDate) {
            if (invoiceForLease.getDueDate() != null && invoiceForLease.getDueDate().compareTo(invoiceDate) < 0) {
                return String.format("Invoice date must be on or before the due date (%s)", invoiceForLease.getDueDate().toString());
            }
            final ApplicationTenancy applicationTenancy = invoiceForLease.getApplicationTenancy();
            final Numerator numerator = numeratorRepository.findInvoiceNumberNumerator(invoiceForLease.getFixedAsset(), applicationTenancy);
            if (numerator != null) {
                final String invoiceNumber = numerator.lastIncrementStr();
                if (invoiceNumber != null) {
                    List<Invoice> result = invoiceRepository.findMatchingInvoiceNumber(invoiceNumber);
                    if (result.size() > 0) {
                        final Invoice invoice = result.get(0);
                        if (invoice.getInvoiceDate().isAfter(invoiceDate)) {
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
        ClockService clockService;

        @javax.inject.Inject
        InvoiceRepository invoiceRepository;

        @javax.inject.Inject
        MessageService messageService;

        @javax.inject.Inject
        TitleService titleService;
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin
    public static class _saveAsHistoric {
        private final InvoiceForLease invoiceForLease;

        public _saveAsHistoric(final InvoiceForLease invoiceForLease) {
            this.invoiceForLease = invoiceForLease;
        }

        @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public void $$() {
            invoiceForLease.setStatus(InvoiceStatus.HISTORIC);
            invoiceForLease.setRunId(null);
        }

        public boolean hide$$() {
            return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser());
        }

        @Inject
        UserService userService;
    }

    public void updating() {
        updateDescriptions();
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            restrictTo = RestrictTo.PROTOTYPING
    )
    public Invoice updateDescriptions() {
        updateAttribute(this, InvoiceAttributeName.INVOICE_DESCRIPTION);
        updateAttribute(this, InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION);
        return this;
    }

    public InvoiceAttributesVM updateAttribute(
            final InvoiceForLease invoice,
            final InvoiceAttributeName attributeName) {
        final InvoiceAttributesVM vm = new InvoiceAttributesVM(invoice);
        updateAttribute(attributeName,
                fragmentRenderService.render(vm, attributeName.getFragmentName()), InvoiceAttributeAction.UPDATE);
        return vm;
    }

    @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
    public String getPreliminaryLetterDescription() {
        return attributeValueFor(InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION);
    }

    //region > _preliminaryLetterComment (derived property)
    @Mixin(method="prop")
    public static class _preliminaryLetterComment {
        private final InvoiceForLease invoiceForLease;
        public _preliminaryLetterComment(final InvoiceForLease invoiceForLease) {
            this.invoiceForLease = invoiceForLease;
        }
        @Action(semantics = SemanticsOf.SAFE)
        @ActionLayout(contributed=Contributed.AS_ASSOCIATION)
        @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
        public String prop() {
            invoiceForLease.attributeValueFor(InvoiceAttributeName.PRELIMINARY_LETTER_COMMENT);
            return null;
        }
        public boolean hideProp() {
            return false;
        }
    }
    //endregion


    @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
    public String getDescription() {
        return attributeValueFor(InvoiceAttributeName.INVOICE_DESCRIPTION);
    }

    @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
    public String getComment() {
        return attributeValueFor(InvoiceAttributeName.INVOICE_COMMENT);
    }

    /**
     * It's the responsibility of the invoice to be able to determine which seller's bank account is to be paid into by the buyer.
     */
    @Programmatic
    public FinancialAccount getSellerBankAccount() {
        if (getFixedAsset() == null) {
            return null;
        }
        // TODO: EST-xxxx to enforce the constraint that there can only be one "at any given time".
        Predicate<FixedAssetFinancialAccount> bankaccountOwnerEqualsSeller = x->x.getFinancialAccount().getOwner().equals(getSeller());
        final Optional<FixedAssetFinancialAccount> fafrIfAny =
                fixedAssetFinancialAccountRepository.findByFixedAsset(getFixedAsset())
                        .stream()
                        .filter(bankaccountOwnerEqualsSeller)
                        .findFirst();
        return fafrIfAny.isPresent() ? fafrIfAny.get().getFinancialAccount() : null;
    }

    @javax.inject.Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @javax.inject.Inject
    NumeratorForCollectionRepository numeratorRepository;


    @Programmatic
    public LocalDate getCodaValDate() {
        // for invoices (status == INVOICED), the invoiceDate will be set.
        // for prelim letters (status == COLLECTED), only the dueDate will be set.
        return coalesce(getInvoiceDate(), getDueDate());
    }

    private static <T> T coalesce(final T... values) {
        for (final T value : values) {
            if(value != null) return value;
        }
        return null;
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class _overridePreliminaryLetterDescription extends _overrideAttributeAbstract {
        public _overridePreliminaryLetterDescription(final InvoiceForLease invoice) {
            super(invoice, InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION);
        }
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class _resetPreliminaryLetterDescription extends _resetAttributeAbstract {

        public _resetPreliminaryLetterDescription(final InvoiceForLease invoice) {
            super(invoice, InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION);
        }

        @Override protected Object viewModelFor(final Invoice invoice) {
            return new InvoiceAttributesVM((InvoiceForLease) invoice);
        }
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class _overrideInvoiceDescription extends _overrideAttributeAbstract {
        public _overrideInvoiceDescription(final InvoiceForLease invoice) {
            super(invoice, InvoiceAttributeName.INVOICE_DESCRIPTION);
        }
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class _resetInvoiceDescription extends _resetAttributeAbstract {

        public _resetInvoiceDescription(final InvoiceForLease invoice) {
            super(invoice, InvoiceAttributeName.INVOICE_DESCRIPTION);
        }

        @Override
        protected Object viewModelFor(final Invoice invoice) {
            return new InvoiceAttributesVM((InvoiceForLease) invoice);
        }
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class _changePreliminaryLetterComment extends _overrideAttributeAbstract {
        public _changePreliminaryLetterComment(final InvoiceForLease invoice) {
            super(invoice, InvoiceAttributeName.PRELIMINARY_LETTER_COMMENT);
        }
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class _changeInvoiceComment extends _overrideAttributeAbstract {
        public _changeInvoiceComment(final InvoiceForLease invoice) {
            super(invoice, InvoiceAttributeName.INVOICE_COMMENT);
        }
    }

    @Programmatic
    public InvoiceForLease resetDescriptions() {
        updateAttribute(InvoiceAttributeName.INVOICE_DESCRIPTION, fragmentRenderService.render(new InvoiceAttributesVM(this), InvoiceAttributeName.INVOICE_DESCRIPTION.getFragmentName()),
                Invoice.InvoiceAttributeAction.RESET);
        updateAttribute(InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION, fragmentRenderService.render(new InvoiceAttributesVM(this), InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION.getFragmentName()),
                Invoice.InvoiceAttributeAction.RESET);
        return this;
    }

    @javax.inject.Inject
    FragmentRenderService fragmentRenderService;

}
