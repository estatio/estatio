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

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.TypicalLength;
import org.apache.isis.applib.annotation.Where;

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
import org.estatio.dom.valuetypes.LocalDateInterval;

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
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@Immutable
public abstract class InvoiceItem
        extends EstatioDomainObject<InvoiceItem>
        implements WithInterval<InvoiceItem>, WithDescriptionGetter, WithApplicationTenancyPropertyLocal {

    public InvoiceItem() {
        super("invoice, charge, startDate desc nullsLast, description, grossAmount, uuid");
    }

    // TODO: added a uuid since there can be similar invoice items having a
    // different source (leaseTerm)
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

    /**
     * Optional, for subclasses to indicate their source (UI purposes only)
     */
    @Hidden(where = Where.REFERENCES_PARENT)
    public InvoiceSource getSource() {
        return null;
    }

    // //////////////////////////////////////

    private BigInteger sequence;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Hidden
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    // //////////////////////////////////////

    private Invoice invoice;

    @javax.jdo.annotations.Column(name = "invoiceId", allowsNull = "flase")
    @Render(Type.EAGERLY)
    @Hidden(where = Where.REFERENCES_PARENT)
    @Title(sequence = "1", append = ":")
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(final Invoice invoice) {
        this.invoice = invoice;
    }

    // //////////////////////////////////////

    private Charge charge;

    @javax.jdo.annotations.Column(name = "chargeId", allowsNull = "true")
    @Title(sequence = "2")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(final Charge charge) {
        this.charge = charge;
    }

    public List<Charge> choicesCharge() {
        return charges.allCharges();
    }

    // //////////////////////////////////////

    private BigDecimal quantity;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    // //////////////////////////////////////

    private BigDecimal netAmount;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal defaultNetAmount() {
        return BigDecimal.ZERO;
    }

    // //////////////////////////////////////

    private BigDecimal vatAmount;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Hidden(where = Where.ALL_TABLES)
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(final BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    // //////////////////////////////////////

    private BigDecimal grossAmount;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    // //////////////////////////////////////

    private Tax tax;

    @javax.jdo.annotations.Column(name = "taxId", allowsNull = "true")
    @Hidden(where = Where.PARENTED_TABLES)
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private String description;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.DESCRIPTION)
    @TypicalLength(JdoColumnLength.NAME)
    @MultiLine(numberOfLines = IsisMultilineLines.NUMBER_OF_LINES)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public InvoiceItem changeDescription(
            final @Named("Description") @MultiLine(numberOfLines = 3) String description) {
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

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Optional
    @Hidden(where = Where.PARENTED_TABLES)
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Optional
    @Hidden(where = Where.PARENTED_TABLES)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate effectiveStartDate;

    @Optional
    public LocalDate getEffectiveStartDate() {
        return this.effectiveStartDate;
    }

    public void setEffectiveStartDate(final LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate effectiveEndDate;

    @Optional
    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(final LocalDate effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public InvoiceItem changeEffectiveDates(
            final @Named("Effective start date") LocalDate effectiveStartDate,
            final @Named("Effective end date") LocalDate effectiveEndDate) {
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

    @Bulk
    public InvoiceItem verify() {
        calculateTax();
        return this;
    }

    // //////////////////////////////////////

    @Bulk
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

}