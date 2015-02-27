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
package org.estatio.dom.tax;

import java.math.BigDecimal;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.Chained;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTaxAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.tax.TaxRate "
                        + "WHERE tax == :tax"
                        + "  && startDate <= :date"
                        + "  && (endDate == null || endDate >= :date)")
})
@DomainObject(editing = Editing.DISABLED)
public class TaxRate
        extends EstatioDomainObject<TaxRate>
        implements Chained<TaxRate>, WithIntervalMutable<TaxRate>, WithApplicationTenancyCountry {

    public TaxRate() {
        super("tax, startDate desc nullsLast");
    }

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getTax().getApplicationTenancy();
    }

    // //////////////////////////////////////

    private Tax tax;

    @javax.jdo.annotations.Column(name = "taxId", allowsNull = "false")
    @Title
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalDate endDate;

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    private String externalReference;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.NAME)
    @Property(optionality = Optionality.OPTIONAL)
    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(final String externalReference) {
        this.externalReference = externalReference;
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<TaxRate> changeDates = new WithIntervalMutable.Helper<TaxRate>(this);

    WithIntervalMutable.Helper<TaxRate> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public TaxRate changeDates(
            final @ParameterLayout(named = "Start Date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End Date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getChangeDates().default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getChangeDates().default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return getChangeDates().validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Override
    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    private BigDecimal percentage;

    @Title
    @javax.jdo.annotations.Column(scale = 2)
    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(final BigDecimal percentage) {
        this.percentage = percentage;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "previousTaxRateId")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private TaxRate previous;

    @Override
    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @PropertyLayout(named = "Previous Rate")
    public TaxRate getPrevious() {
        return previous;
    }

    public void setPrevious(final TaxRate previous) {
        this.previous = previous;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "nextTaxRateId")
    private TaxRate next;

    @Override
    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @PropertyLayout(named = "Next Rate")
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
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "Percentage") BigDecimal percentage) {
        TaxRate rate = this.getTax().newRate(startDate, percentage);
        setNext(rate);
        rate.setPrevious(this);
        return rate;
    }

    public TaxRate change(
            final @ParameterLayout(named = "Tax") Tax tax,
            final @ParameterLayout(named = "Percentage") @Parameter(optionality = Optionality.OPTIONAL) BigDecimal percentage,
            final @ParameterLayout(named = "External Reference") @Parameter(optionality = Optionality.OPTIONAL) String externalReference) {

        setTax(tax);
        setPercentage(percentage);
        setExternalReference(externalReference);
        return this;
    }

    public Tax default0Change() {
        return getTax();
    }

    public BigDecimal default1Change() {
        return getPercentage();
    }

    public String default2Change() {
        return getExternalReference();
    }

}
