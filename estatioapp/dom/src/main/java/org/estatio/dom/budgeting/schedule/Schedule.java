/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.budgeting.schedule;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;

import javax.jdo.annotations.*;
import java.util.SortedSet;
import java.util.TreeSet;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
//       ,schema = "budget"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.schedule.Schedule " +
                        "WHERE property == :property "),
        @Query(
                name = "findByBudget", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.schedule.Schedule " +
                        "WHERE budget == :budget "),
        @Query(
                name = "findByPropertyAndCharge", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.schedule.Schedule " +
                        "WHERE property == :property && charge == :charge"),
        @Query(
                name = "findByPropertyChargeAndDates", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.schedule.Schedule " +
                        "WHERE property == :property && charge == :charge && startDate == :startDate && endDate == :endDate")
})
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = Schedules.class)
public class Schedule extends EstatioDomainObject<Schedule> implements WithIntervalMutable<Schedule>, WithApplicationTenancyProperty {

    public Schedule() {
        super("property, startDate, endDate");
    }

    public Schedule(final LocalDate startDate, final LocalDate endDate) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //region > identificatiom
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", "Schedule for ".concat(getProperty().getName())
                .concat(" - period: ")
                .concat(getEffectiveInterval().toString())
        );
    }
    //endregion

    private Property property;

    @javax.jdo.annotations.Column(name="propertyId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    private LocalDate endDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate date) {
        return LocalDateInterval.including(this.getStartDate(), this.getEndDate()).contains(date);
    }

    // //////////////////////////////////////

    private Helper<Schedule> changeDates = new Helper<Schedule>(this);

    Helper<Schedule> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Schedule changeDates(
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End date") @Parameter(optionality =  Optionality.OPTIONAL) LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
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

    //region > charge (property)
    private Charge charge;

    @Column(allowsNull = "false", name = "chargeId")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(final Charge charge) {
        this.charge = charge;
    }
    //endregion

    //region > status (property)
    private Status status;

    @Column(allowsNull = "false")
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }
    //endregion

    //region > budget (property)
    private Budget budget;

    @Column(allowsNull = "false", name = "budgetId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    public Budget getBudget() {
        return budget;
    }

    public void setBudget(final Budget budget) {
        this.budget = budget;
    }
    //endregion



    private SortedSet<ScheduleItem> scheduleItems = new TreeSet<ScheduleItem>();

    @CollectionLayout(render= RenderType.EAGERLY)
    @Persistent(mappedBy = "schedule", dependentElement = "true")
    public SortedSet<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(final SortedSet<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }


    public Budget deleteSchedule(final boolean confirmDelete) {

        for (ScheduleItem scheduleItem : getScheduleItems()) {
            removeIfNotAlready(scheduleItem);
        }

        removeIfNotAlready(this);
        return this.getBudget();
    }

    public String validateDeleteSchedule(boolean confirmDelete){
        return confirmDelete? null:"Please confirm";
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Schedule closeSchedule() {
        setStatus(Status.CLOSED);
        return this;
    }

    public boolean hideCloseSchedule() {
        return getStatus()==Status.OPEN ? false : true;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Schedule openSchedule() {
        setStatus(Status.OPEN);
        return this;
    }

    public boolean hideOpenSchedule() {
        return getStatus()==Status.CLOSED ? false : true;
    }

    @MemberOrder(sequence = "4")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Override public ApplicationTenancy getApplicationTenancy() {
        return getProperty().getApplicationTenancy();
    }

    // //////////////////////////////////////

    public enum Status {
        OPEN,
        CLOSED;
    }

}
