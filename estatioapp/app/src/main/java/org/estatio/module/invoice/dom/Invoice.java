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
package org.estatio.module.invoice.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javax.inject.Inject;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

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
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ObjectUpdatingEvent;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.NotesType;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.types.AtPathType;

import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyAny;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.invoice.dom.attr.InvoiceAttribute;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeRepository;
import org.estatio.module.party.dom.Party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "dbo"   // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.invoice.InvoiceAbstract" // dummy value required because the InvoiceForLease subclass uses this class' FQCN (for backward compatibility; see EST-1084)
)
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE status == :status " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByBuyer", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE buyer == :buyer " +
                        "ORDER BY invoiceDate DESC"),
        @javax.jdo.annotations.Query(
                name = "findBySeller", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE seller == :seller " +
                        "ORDER BY invoiceDate DESC"),
        @javax.jdo.annotations.Query(
                name = "findMatchingInvoiceNumber", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.invoice.dom.Invoice " +
                        "WHERE invoiceNumber.matches(:invoiceNumber) "
                        + "ORDER BY invoiceDate DESC")
})
@Indices({
        @Index(name = "Invoice_invoiceNumber_IDX",
                members = { "invoiceNumber" })
        , @Index(name = "Invoice_sendTo_IDX",
        members = { "sendTo" })
})
@DomainObject(
        editing = Editing.DISABLED
        //        ,
        //        updatingLifecycleEvent = Invoice.UpdatingEvent.class
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public abstract class Invoice<T extends Invoice<T>>
        extends UdoDomainObject2<T>
        implements WithApplicationTenancyAny, WithApplicationTenancyPathPersisted {


    public static class UpdatingEvent extends ObjectUpdatingEvent<Invoice> {
    }

    public Invoice(final String keyProperties) {
        super(keyProperties);
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

    @Property(hidden = Where.ALL_TABLES)
    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    public String title() {
        if (getInvoiceNumber() != null) {
            return String.format("Invoice %s", getInvoiceNumber());
        }
        if (getCollectionNumber() != null) {
            return String.format("Collection %s", getCollectionNumber());
        }
        return String.format("Temp *%08d (%s)", Integer.parseInt(getId()), getBuyer().getName());
    }

    @Property(hidden = Where.OBJECT_FORMS)
    public String getNumber() {
        return ObjectUtils.firstNonNull(
                getInvoiceNumber(),
                getCollectionNumber(),
                title());
    }

    @javax.jdo.annotations.Column(name = "buyerPartyId", allowsNull = "true")
    @Getter @Setter
    private Party buyer;

    @javax.jdo.annotations.Column(name = "sellerPartyId", allowsNull = "true")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Party seller;

    @javax.jdo.annotations.Column(allowsNull = "true", length = InvoiceNumberType.Meta.MAX_LEN)
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String collectionNumber;

    @javax.jdo.annotations.Column(allowsNull = "true", length = 128)
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String invoiceNumber;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate invoiceDate;

    @javax.jdo.annotations.Column(name = "sendToCommunicationChannelId", allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private CommunicationChannel sendTo;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate dueDate;

    @Persistent(mappedBy = "invoice", dependentElement = "false")
    @Getter @Setter
    private SortedSet<InvoiceAttribute> attributes = new TreeSet<InvoiceAttribute>();

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            restrictTo = RestrictTo.PROTOTYPING
    )
    public Invoice updateAttribute(
            final InvoiceAttributeName name,
            @Parameter(maxLength = NotesType.Meta.MAX_LEN)
            @ParameterLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE) final String value,
            InvoiceAttributeAction action
    ) {
        final InvoiceAttribute invoiceAttribute = invoiceAttributeRepository.findByInvoiceAndName(this, name);
        if (invoiceAttribute == null) {
            invoiceAttributeRepository.newAttribute(this, name, value, action.isOverride());
        } else {
            if (action.isForceful())
                invoiceAttribute.setValue(value);
            invoiceAttribute.setOverridden(action.isOverride());
        }
        return this;
    }

    @AllArgsConstructor
    public enum InvoiceAttributeAction {
        UPDATE(false, false),
        RESET(false, true),
        OVERRIDE(true, true);

        @Getter
        private boolean override;

        @Getter
        private boolean forceful;

    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void changeDueDate(final LocalDate dueDate) {
        this.setDueDate(dueDate);
    }

    public LocalDate default0ChangeDueDate(final LocalDate dueDate) {
        return this.getDueDate();
    }

    public String disableChangeDueDate() {
        if (this.isImmutableDueToState()) {
            return "Due date can't be changed";
        }
        return null;
    }

    @Inject protected
    InvoiceAttributeRepository invoiceAttributeRepository;

    @javax.jdo.annotations.Column(allowsNull = "false", length = InvoiceStatus.Meta.MAX_LEN)
    @Getter @Setter
    private InvoiceStatus status;

    @javax.jdo.annotations.Column(name = "currencyId", allowsNull = "true")
    @Getter @Setter
    private Currency currency;

    @javax.jdo.annotations.Column(allowsNull = "true", length = PaymentMethod.Meta.MAX_LEN)
    @Getter @Setter
    private PaymentMethod paymentMethod;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Invoice changePaymentMethod(final PaymentMethod paymentMethod) {
        setPaymentMethod(paymentMethod);
        return this;
    }

    public PaymentMethod default0ChangePaymentMethod() {
        return getPaymentMethod();
    }

    public String disableChangePaymentMethod() {
        return reasonDisabledFinanceDetailsDueToState(this);
    }

    public String validateChangePaymentMethod(final PaymentMethod paymentMethod){
        if (paymentMethod==PaymentMethod.MANUAL_PROCESS){
            return "Manual process is not in use anymore.";
        }
        return null;
    }


    /**
     * Mandatory hook
     *
     * @param viewContext
     * @return
     */
    protected abstract String reasonDisabledDueToState(final Object viewContext);

    /**
     * Mandatory hook
     *
     * @param viewContext
     * @return
     */
    protected abstract String reasonDisabledFinanceDetailsDueToState(final Object viewContext);

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
    public BigDecimal getTotalNetAmount() {
        return sum(InvoiceItem::getNetAmount);
    }

    @Property(notPersisted = true, hidden = Where.ALL_TABLES)
    public BigDecimal getTotalVatAmount() {
        return sum(InvoiceItem::getVatAmount);
    }

    @Property(notPersisted = true)
    public BigDecimal getTotalGrossAmount() {
        return sum(InvoiceItem::getGrossAmount);
    }

    private BigDecimal sum(final Function<InvoiceItem, BigDecimal> x) {
        return Lists.newArrayList(getItems()).stream()
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * This does not enumerate correctly, it would seem. TODO: can be removed when DN issue solved?
     */
    private BigDecimal sumFunctionalUsingLazyStreamBROKEN(final Function<InvoiceItem, BigDecimal> x) {
        // as per http://www.datanucleus.org/servlet/jira/browse/NUCAPIJDO-77
        return getItems().stream()
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Default implementation based on {@link #getStatus() status}, but subclasses can override based on their own
     * interpretation of "status" (and <tt>IncomingInvoice</tt> does, using its approval cycle).
     */
    @Programmatic
    public boolean isImmutableDueToState() {
        return !getStatus().invoiceIsChangable();
    }

    @Property(hidden = Where.ALL_TABLES)
    @javax.jdo.annotations.Column(allowsNull = "true", name = "paidByBankMandateId")
    @Getter @Setter
    private BankMandate paidBy;

    @Programmatic
    public InvoiceItem findFirstItemWithCharge(final Charge charge) {
        for (InvoiceItem item : getItems()) {
            if (item.getCharge().equals(charge)) {
                return item;
            }
        }
        return null;
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "exec")
    public static class _remove {

        private final Invoice<?> invoice;

        public _remove(final Invoice invoice) {
            this.invoice = invoice;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public void exec() {
            // Can be called as bulk so have a safeguard
            if (disableExec() == null) {
                for (InvoiceItem item : invoice.getItems()) {
                    item.remove();
                }
                paperclipRepository.deleteIfAttachedTo(invoice, PaperclipRepository.Policy.PAPERCLIPS_AND_DOCUMENTS_IF_ORPHANED);
                repositoryService.remove(invoice);
            }
        }

        public String disableExec() {
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

    public static class Predicates {

        public static Predicate<Invoice> isChangeable() {
            return invoice -> invoice.getStatus().invoiceIsChangable();
        }

        public static Predicate<Invoice> noLongerChangeable() {
            return com.google.common.base.Predicates.not(Invoice.Predicates.isChangeable());
        }

    }

    public Invoice verify() {
        for (InvoiceItem ii : getItems()) {
            ii.verify();
        }
        return this;
    }

    public boolean hideVerify() {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
    }

    public static class InvoiceNumberType {

        private InvoiceNumberType() {
        }

        public static class Meta {

            /**
             * TODO: review
             */
            public static final int MAX_LEN = 16;

            private Meta() {
            }

        }

    }

    public static class DescriptionType {

        private DescriptionType() {
        }

        public static class Meta {

            public static final int MAX_LEN = InvoiceAttribute.ValueType.Meta.MAX_LEN;
            public static final int MULTI_LINE = 10;

            private Meta() {
            }
        }
    }

}
