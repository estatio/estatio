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
import java.math.RoundingMode;
import java.util.List;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

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
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.IsisMultilineLines;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithDescriptionGetter;
import org.estatio.dom.WithInterval;
import org.estatio.dom.apptenancy.WithApplicationTenancyPropertyLocal;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.TitleBuilder;
import org.estatio.dom.valuetypes.LocalDateInterval;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a line-item of an {@link #getInvoice() owning} {@link Invoice}.
 * 
 * <p>
 * This class is, in fact, abstract. The <tt>InvoiceItemForLease</tt> subclass
 * decouples the <tt>invoice</tt> module from the <tt>lease</tt> module, and
 * provides a many-to-many between the two concepts.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public abstract class InvoiceItem
        extends EstatioDomainObject<InvoiceItem>
        implements WithInterval<InvoiceItem>, WithDescriptionGetter, WithApplicationTenancyPropertyLocal {

    public InvoiceItem() {
        super("invoice, charge, startDate desc nullsLast, description, grossAmount, uuid");
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
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private BigInteger sequence;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "invoiceId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @CollectionLayout(render = RenderType.EAGERLY)
    @Getter @Setter
    private Invoice invoice;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "chargeId", allowsNull = "true")
    @Getter @Setter
    private Charge charge;

    public List<Charge> choicesCharge() {
        return charges.allCharges();
    }

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
    public InvoiceItem changeTax(final Tax tax){
        setTax(tax);
        this.calculateTax();
        return this;
    }

    public Tax default0ChangeTax(final Tax tax){
        return getTax();
    }

    public String disableChangeTax(final Tax tax){
        return getSource() == null ? null : "Cannot change tax on a generated invoice item";
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.DESCRIPTION)
    @PropertyLayout(typicalLength = JdoColumnLength.NAME, multiLine = IsisMultilineLines.NUMBER_OF_LINES)
    @Getter @Setter
    private String description;

    public InvoiceItem changeDescription(
            final @ParameterLayout(multiLine = 3) String description) {
        setDescription(description);
        return this;
    }

    public String default0ChangeDescription() {
        return getDescription();
    }

    public String disableChangeDescription(
            final String description) {
        if (!getInvoice().getStatus().invoiceIsChangable()) {
            return "Invoice can't be changed";
        }
        return null;
    }

    // //////////////////////////////////////


    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate dueDate;

    // //////////////////////////////////////


    @Property(optionality = Optionality.OPTIONAL, hidden = Where.PARENTED_TABLES)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate startDate;

    @Property(optionality = Optionality.OPTIONAL, hidden = Where.PARENTED_TABLES)
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

    // //////////////////////////////////////

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public void remove() {
        if (getInvoice().getStatus().equals(InvoiceStatus.NEW)) {
            getContainer().remove(this);
            getContainer().flush();
        }
    }

    @Programmatic
    private void calculateTax() {
        BigDecimal percentage = null;
        if (getTax() != null) {
            percentage = tax.percentageFor(getDueDate());
        }
        setVatAmount(vatFromNet(getNetAmount(), percentage));
        setGrossAmount(grossFromNet(getNetAmount(), percentage));
    }

    private BigDecimal vatFromNet(final BigDecimal net, final BigDecimal percentage) {
        if (net == null || percentage == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = percentage.divide(LeaseConstants.PERCENTAGE_DIVISOR);
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

    // //////////////////////////////////////

    /**
     * Lifecycle
     */
    public void created() {
        initialize();
    }

    // //////////////////////////////////////

    private Charges charges;

    public final void injectCharges(final Charges charges) {
        this.charges = charges;
    }

    @Override public ApplicationTenancy getApplicationTenancy() {
        return getInvoice().getApplicationTenancy();
    }
}
