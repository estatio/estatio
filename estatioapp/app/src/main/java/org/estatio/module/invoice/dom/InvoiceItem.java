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
import java.math.RoundingMode;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
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
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithDescriptionGetter;
import org.incode.module.base.dom.with.WithInterval;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPropertyLocal;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.platform.applib.ReasonBuffer2;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRate;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a line-item of an {@link #getInvoice() owning} {@link Invoice}.
 * <p/>
 * <p/>
 * This class is, in fact, abstract. The <tt>InvoiceItemForLease</tt> subclass
 * decouples the <tt>invoice</tt> module from the <tt>lease</tt> module, and
 * provides a many-to-many between the two concepts.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"  // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.invoice.InvoiceItem"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public abstract class InvoiceItem<P extends Invoice<P>, T extends InvoiceItem<P,T>>
        extends UdoDomainObject2<T>
        implements WithInterval<T>, WithDescriptionGetter, WithApplicationTenancyPropertyLocal {

    public InvoiceItem() {
        super("invoice, charge, startDate desc nullsLast, description, grossAmount, sequence, uuid");
    }

    public InvoiceItem(final P invoice) {
        this();
        this.invoice = invoice;

        // the least significant part of the item's comparator...
        // ensures that these are always distinct items in the parent invoice's items collection
        setUuid(java.util.UUID.randomUUID().toString());
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getInvoice())
                .withName(getCharge())
                .toString();
    }

    // TODO: added a uuid since there can be similar invoice items having a
    // different source (leaseTerm)
    @Property(optionality = Optionality.OPTIONAL, hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String uuid;

    // //////////////////////////////////////

    /**
     * Optional, for subclasses to indicate their source (UI purposes only)
     */
    @Property(hidden = Where.REFERENCES_PARENT)
    public InvoiceSource getSource() {
        // TODO: this applies to InvoiceItemForLease only, so should probably be moved down.
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property
    @Getter @Setter
    private BigInteger sequence;

    // //////////////////////////////////////

    /**
     * This ought to be of type 'P', but the DataNucleus annotation processor fails to handle.
     *
     * <pre>
     *     [ERROR] Fatal error compiling: java.lang.ClassCastException: com.sun.tools.javac.code.Symbol$TypeVariableSymbol cannot be cast to javax.lang.model.element.TypeElement -> [Help 1]
     * </pre>
     */
    @javax.jdo.annotations.Column(name = "invoiceId", allowsNull = "false")
    @javax.jdo.annotations.Persistent(defaultFetchGroup = "true") // eagerly load
    @Property(hidden = Where.REFERENCES_PARENT)
    @CollectionLayout(render = RenderType.EAGERLY)
    @Getter @Setter
    private Invoice<?> invoice;


    @javax.jdo.annotations.Column(name = "chargeId", allowsNull = "true")
    @Getter @Setter
    private Charge charge;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal quantity;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal netAmount;

    public BigDecimal defaultNetAmount() {
        return BigDecimal.ZERO;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private BigDecimal vatAmount;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal grossAmount;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "taxId", allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Tax tax;

    @MemberOrder(name = "tax", sequence = "1")
    public InvoiceItem changeTax(final Tax tax) {
        setTax(tax);
        this.calculateTax();
        return this;
    }

    public Tax default0ChangeTax(final Tax tax) {
        return getTax();
    }

    public String disableChangeTax() {
        ReasonBuffer2 buf = ReasonBuffer2.forSingle("Can't change tax because");
        appendReasonChangeTaxDisabledIfAny(buf);
        return buf.getReason();
    }

    protected void appendReasonChangeTaxDisabledIfAny(final ReasonBuffer2 buf) {
        buf.append(() -> getInvoice().isImmutableDueToState(), "Invoice can't be changed");
        buf.append(() -> getSource() == null, "Cannot change tax on a generated invoice item");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = DescriptionType.Meta.MAX_LEN)
    @PropertyLayout(multiLine = DescriptionType.Meta.MULTI_LINE)
    @Getter @Setter
    private String description;

    public String disableDescription() {
        return getInvoice().isImmutableDueToState() ? "Invoice can't be changed" : null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public InvoiceItem changeDescription(
            final @ParameterLayout(multiLine = 3) String description) {

        setDescription(description);
        return this;
    }

    public String default0ChangeDescription() {
        return getDescription();
    }

    public String disableChangeDescription() {
        // don't need to worry about generalizing the disable guards for this action on the IncomingInvoiceItem subtype,
        // since is hidden in .layout.xml.
        // (as a consequence, should therefore probably move this action down to InvoiceItemForLease).
        return disableDescription();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate dueDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate startDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate effectiveStartDate;

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate effectiveEndDate;

    public InvoiceItem changeEffectiveDates(
            final LocalDate effectiveStartDate,
            final LocalDate effectiveEndDate) {

        setEffectiveStartDate(effectiveStartDate);
        setEffectiveEndDate(effectiveEndDate);
        return this;
    }

    public LocalDate default0ChangeEffectiveDates() {
        return getEffectiveStartDate();
    }

    public LocalDate default1ChangeEffectiveDates() {
        return getEffectiveEndDate();
    }

    public String disableChangeEffectiveDates() {
        // don't need to worry about generalizing the disable guards for this action on the IncomingInvoiceItem subtype,
        // since is hidden in .layout.xml.
        // (as a consequence, should therefore probably move this action down to InvoiceItemForLease).
        return getInvoice().isImmutableDueToState() ? "Invoice cannot be changed" : null;
    }

    // //////////////////////////////////////

    @Programmatic
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    @Override
    public LocalDateInterval getEffectiveInterval() {
        return LocalDateInterval.including(getEffectiveStartDate(), getEffectiveEndDate());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public InvoiceItem verify() {
        calculateTax();
        return this;
    }

    public boolean hideVerify(){
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
    }

    // //////////////////////////////////////

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Invoice remove() {
        if (!getInvoice().isImmutableDueToState()) {
            repositoryService.remove(this);
        }
        return getInvoice();
    }

    public String disableRemove(){
        // don't need to worry about generalizing the disable guards for this action on the IncomingInvoiceItem subtype,
        // since is hidden in .layout.xml.
        // (as a consequence, should therefore probably move this action down to InvoiceItemForLease).
        //
        // nb: in fact, IncomingInvoiceItem actually has its own (equivalent?) removeItem, so probably should be amalgamated.
        return getInvoice().isImmutableDueToState() ? "Invoice can't be changed" : null;
    }



    @Getter @Setter @Column(name = "taxRateId", allowsNull = "true")
    private TaxRate taxRate;

    public BigDecimal getVatPercentage() {
        if (getTaxRate() != null) {
            return getTaxRate().getPercentage();
        }
        return null;
    }

    @Programmatic
    private void calculateTax() {
        if(getTax() != null && getTaxRate() == null)
            setTaxRate(tax.taxRateFor(ObjectUtils.firstNonNull(getEffectiveEndDate(), getEndDate(), getDueDate(), getInvoice().getDueDate())));
        BigDecimal percentage = getVatPercentage();
        setVatAmount(vatFromNet(getNetAmount(), percentage));
        setGrossAmount(grossFromNet(getNetAmount(), percentage));
    }


    private BigDecimal vatFromNet(final BigDecimal net, final BigDecimal percentage) {
        if (net == null || percentage == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = percentage.divide(InvoiceConstants.PERCENTAGE_DIVISOR);
        return net.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal grossFromNet(final BigDecimal net, final BigDecimal percentage) {
        if (net == null) {
            return BigDecimal.ZERO;
        }
        if (percentage == null) {
            return net;
        }
        return net.add(vatFromNet(net, percentage));
    }

    @Programmatic
    public void initialize() {
        // set defaults
        setVatAmount(BigDecimal.ZERO);
        setGrossAmount(BigDecimal.ZERO);
        setNetAmount(BigDecimal.ZERO);
    }

    /**
     * Lifecycle
     */
    public void created() {
        initialize();
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getInvoice().getApplicationTenancy();
    }

    @javax.inject.Inject
    RepositoryService repositoryService;


    public static class DescriptionType {

        private DescriptionType() {}

        public static class Meta {

            public static final int MAX_LEN = org.incode.module.base.dom.types.DescriptionType.Meta.MAX_LEN;
            public static final int MULTI_LINE = org.incode.module.base.dom.types.DescriptionType.Meta.MULTI_LINE;

            private Meta() {}

        }

    }

}