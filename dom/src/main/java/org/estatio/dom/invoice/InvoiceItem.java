/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithDescriptionGetter;
import org.estatio.dom.WithInterval;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@MemberGroups({ "General", "Amounts", "Dates", "Related" })
public abstract class InvoiceItem extends EstatioTransactionalObject<InvoiceItem, Status> implements WithInterval<InvoiceItem>, WithDescriptionGetter {

    public InvoiceItem() {
        super("invoice, startDate desc, charge, description, sequence", null, null);
    }

    // //////////////////////////////////////

    private Status status;

    @Hidden
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private BigInteger sequence;

    @Hidden
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(BigInteger sequence) {
        this.sequence = sequence;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "INVOICE_ID")
    private Invoice invoice;

    @Render(Type.EAGERLY)
    @Disabled
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Title(sequence = "1", append = ":")
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(final Invoice invoice) {
        this.invoice = invoice;
    }

    public void modifyInvoice(final Invoice invoice) {
        Invoice currentInvoice = getInvoice();
        if (invoice == null || invoice.equals(currentInvoice)) {
            return;
        }
        invoice.addToItems(this);
    }

    public void clearInvoice() {
        Invoice currentInvoice = getInvoice();
        if (currentInvoice == null) {
            return;
        }
        currentInvoice.removeFromItems(this);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "CHARGE_ID")
    private Charge charge;

    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    @Disabled
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

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal quantity;

    @MemberOrder(sequence = "3")
    @Disabled
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal netAmount;

    @MemberOrder(name = "Amounts", sequence = "4")
    @Disabled
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

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal vatAmount;

    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    @MemberOrder(name = "Amounts", sequence = "5")
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(final BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal grossAmount;

    @MemberOrder(name = "Amounts", sequence = "6")
    @Disabled
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "TAX_ID")
    private Tax tax;

    @MemberOrder(sequence = "7")
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private String description;

    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    @MemberOrder(sequence = "8")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate dueDate;

    @MemberOrder(name = "Dates", sequence = "9")
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

    @MemberOrder(name = "Dates", sequence = "10")
    @Disabled
    @Optional
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

    @MemberOrder(name = "Dates", sequence = "11")
    @Disabled
    @Optional
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @Hidden
    @Override
    public WithInterval<?> getParentWithInterval() {
        return null;
    }

    @Hidden
    @Override
    public LocalDate getEffectiveStartDate() {
        return WithInterval.Util.effectiveStartDateOf(this);
    }

    @Hidden
    @Override
    public LocalDate getEffectiveEndDate() {
        return WithInterval.Util.effectiveEndDateOf(this);
    }

    @Programmatic
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    @Hidden
    // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.1")
    @Named("Previous Item")
    @Disabled
    @Optional
    @Override
    public InvoiceItem getPrevious() {
        return null;
    }

    @Hidden
    // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.2")
    @Named("Next Item")
    @Disabled
    @Optional
    @Override
    public InvoiceItem getNext() {
        return null;
    }

    // //////////////////////////////////////

    /**
     * Attaches this item to an invoice with similar attributes. Creates a new
     * invoice when no matching found.
     */
    public abstract void attachToInvoice();

    // //////////////////////////////////////

    @Bulk
    public InvoiceItem verify() {
        calculateTax();
        return this;
    }

    // //////////////////////////////////////

    @Programmatic
    public void remove() {
        // no safeguard, assuming being called with precaution
        clearInvoice();
        getContainer().flush();
        getContainer().remove(this);
    }

    @Programmatic
    private void calculateTax() {
        BigDecimal vatAmount = BigDecimal.ZERO;
        if (getTax() != null) {
            BigDecimal percentage = tax.percentageFor(getDueDate());
            if (percentage != null) {
                BigDecimal rate = percentage.divide(BigDecimal.valueOf(100));
                vatAmount = getNetAmount().multiply(rate).setScale(2, RoundingMode.HALF_UP);
            }
        }
        BigDecimal currentVatAmount = getVatAmount();
        if (currentVatAmount == null || vatAmount.compareTo(currentVatAmount) != 0) {
            setVatAmount(vatAmount);
            setGrossAmount(getNetAmount().add(vatAmount));
        }
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

    public void injectCharges(Charges charges) {
        this.charges = charges;
    }

}