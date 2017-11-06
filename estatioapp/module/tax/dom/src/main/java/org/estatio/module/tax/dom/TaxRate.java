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
package org.estatio.module.tax.dom;

import java.math.BigDecimal;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Chained;
import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"  // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
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
                        + "FROM org.estatio.module.tax.dom.TaxRate "
                        + "WHERE tax == :tax"
                        + "  && startDate <= :date"
                        + "  && (endDate == null || endDate >= :date)")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.tax.TaxRate"
)
public class TaxRate
        extends UdoDomainObject2<TaxRate>
        implements Chained<TaxRate>, WithIntervalMutable<TaxRate>, WithApplicationTenancyCountry {

    public TaxRate() {
        super("tax, startDate desc nullsLast");
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getTax())
                .withName(getPercentage())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getTax().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "taxId", allowsNull = "false")
    @Getter @Setter
    private Tax tax;

    // //////////////////////////////////////

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate startDate;

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = NameType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String externalReference;

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<TaxRate> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<TaxRate> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public TaxRate changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }

    public String disableChangeDates() {
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

    @javax.jdo.annotations.Column(scale = 2)
    @Getter @Setter
    private BigDecimal percentage;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @PropertyLayout(named = "Previous Rate")
    @javax.jdo.annotations.Column(name = "previousTaxRateId")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    @Getter @Setter
    private TaxRate previous;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @PropertyLayout(named = "Next Rate")
    @Getter @Setter
    @javax.jdo.annotations.Column(name = "nextTaxRateId")
    private TaxRate next;

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
            final LocalDate startDate,
            final BigDecimal percentage) {
        TaxRate rate = this.getTax().newRate(startDate, percentage);
        setNext(rate);
        rate.setPrevious(this);
        return rate;
    }

    public TaxRate change(
            final Tax tax,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal percentage,
            final @Parameter(optionality = Optionality.OPTIONAL) String externalReference) {

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
