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
package org.estatio.dom.tax;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByTaxAndDate", language = "JDOQL",
            value = "SELECT "
                    + "FROM org.estatio.dom.tax.TaxRate "
                    + "WHERE tax == :tax"
                    + "  && startDate >= :date"
                    + "  && (endDate == null || endDate <= :date)"),
    @javax.jdo.annotations.Query(
            name = "findByTaxAndStartDate", language = "JDOQL",
            value = "SELECT "
                    + "FROM org.estatio.dom.tax.TaxRate "
                    + "WHERE tax == :tax "
                    + "&& startDate == :startDate"),
    @javax.jdo.annotations.Query(
            name = "findByTaxAndEndDate", language = "JDOQL",
            value = "SELECT "
                    + "FROM org.estatio.dom.tax.TaxRate "
                    + "WHERE tax == :tax "
                    + "&& endDate == :endDate"),
})
@MemberGroups({ "General", "Dates", "Related" })
public class TaxRate extends EstatioTransactionalObject<TaxRate, Status> implements WithIntervalMutable<TaxRate> {

    public TaxRate() {
        super("tax, startDate desc", Status.LOCKED, Status.UNLOCKED);
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

    @javax.jdo.annotations.Column(name = "TAX_ID")
    private Tax tax;

    @Title
    @MemberOrder(sequence = "1")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @MemberOrder(name = "Dates", sequence = "2")
    @Optional
    @Disabled
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }



    private LocalDate endDate;

    @MemberOrder(name = "Dates", sequence = "3")
    @Optional
    @Disabled
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    
    // //////////////////////////////////////

    @MemberOrder(name="endDate", sequence="1")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public TaxRate changeDates(
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        setStartDate(startDate);
        setEndDate(endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate) {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }
    
    @Override
    public LocalDate default0ChangeDates() {
        return getStartDate();
    }
    @Override
    public LocalDate default1ChangeDates() {
        return getEndDate();
    }
    
    @Override
    public String validateChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate) {
        return startDate.isBefore(endDate)?null:"Start date must be before end date";
    }


    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    private BigDecimal percentage;

    @Title
    @MemberOrder(sequence = "4")
    @Column(scale = 2)
    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(final BigDecimal percentage) {
        this.percentage = percentage;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "PREVIOUS_ID")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private TaxRate previous;

    @Hidden(where = Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.1")
    @Named("Previous Rate")
    @Disabled
    @Optional
    @Override
    public TaxRate getPrevious() {
        return previous;
    }

    public void setPrevious(final TaxRate previous) {
        this.previous = previous;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "NEXT_ID")
    private TaxRate next;

    @Hidden(where = Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.2")
    @Named("Next Rate")
    @Disabled
    @Optional
    @Override
    public TaxRate getNext() {
        return next;
    }

    public void setNext(final TaxRate next) {
        this.next = next;
    }

    public void modifyNext(final TaxRate next) {
        TaxRate currentNextRate = getNext();
        if (next == null || next.equals(currentNextRate)) {
            return;
        }
        clearNext();
        next.setPrevious(this);
        setNext(next);
    }

    public void clearNext() {
        TaxRate currentNext = getNext();
        if (currentNext == null) {
            return;
        }
        currentNext.setPrevious(null);
        setNext(null);
    }

    // //////////////////////////////////////

    public TaxRate newRate(
            final @Named("Start Date") LocalDate startDate,
            final @Named("Percentage") BigDecimal percentage) {
        TaxRate rate = this.getTax().newRate(startDate, percentage);
        setNext(rate);
        rate.setPrevious(this);
        return rate;
    }

}
