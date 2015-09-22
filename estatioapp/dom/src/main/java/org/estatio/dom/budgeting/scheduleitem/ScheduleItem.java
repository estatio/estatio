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
package org.estatio.dom.budgeting.scheduleitem;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.schedule.Schedule;

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
                name = "findBySchedule", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.scheduleitem.ScheduleItem " +
                        "WHERE schedule == :schedule "),
        @Query(
                name = "findByBudgetItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.scheduleitem.ScheduleItem " +
                        "WHERE budgetItem == :budgetItem "),
        @Query(
                name = "findByKeyTable", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.scheduleitem.ScheduleItem " +
                        "WHERE keyTable == :keyTable "),
        @Query(
                name = "findByScheduleAndBudgetItemAndKeyTable", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.scheduleitem.ScheduleItem " +
                        "WHERE schedule == :schedule && budgetItem == :budgetItem && keyTable == :keyTable ")
})
@Unique(name = "ScheduleItem_schedule_budgetItem_keyTable_UNQ", members = {"schedule", "budgetItem", "keyTable"})
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = ScheduleItems.class)
public class ScheduleItem extends EstatioDomainObject<ScheduleItem> implements WithApplicationTenancyProperty {

    public ScheduleItem() {
        super("schedule, budgetItem, keyTable");
    }

    //region > identificatiom
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", "Schedule item for ".concat(getSchedule().getProperty().getName()));
    }
    //endregion

    //region > schedule (property)
    private Schedule schedule;

    @Column(allowsNull = "false", name = "scheduleId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(final Schedule schedule) {
        this.schedule = schedule;
    }
    //endregion

    // //////////////////////////////////////

    private KeyTable keyTable;

    @Column(name="keyTableId", allowsNull = "false")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    public KeyTable getKeyTable() {
        return keyTable;
    }

    public void setKeyTable(KeyTable keyTable) {
        this.keyTable = keyTable;
    }

    public ScheduleItem changeKeyTable(final @ParameterLayout(named = "KeyTable") KeyTable keyTable) {
        setKeyTable(keyTable);
        return this;
    }

    public KeyTable default0ChangeKeyTable(final KeyTable keyTable) {
        return getKeyTable();
    }

    public String validateChangeKeyTable(final KeyTable keyTable) {
        if (keyTable.equals(null)) {
            return "KeyTable can't be empty";
        }
        return null;
    }

    // //////////////////////////////////////


    //region > budgetItem (property)
    private BudgetItem budgetItem;

    @Column(allowsNull = "false", name = "budgetItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    public BudgetItem getBudgetItem() {
        return budgetItem;
    }

    public void setBudgetItem(final BudgetItem budgetItem) {
        this.budgetItem = budgetItem;
    }
    //endregion

    //region > percentage (property)
    private BigDecimal percentage;

    @Column(allowsNull = "false")
    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(final BigDecimal percentage) {
        this.percentage = percentage;
    }

    public ScheduleItem updatePercentage(final BigDecimal percentage) {
        setPercentage(percentage);
        return this;
    }

    public BigDecimal default0UpdatePercentage(final BigDecimal percentage) {
        return getPercentage();
    }

    public String validateUpdatePercentage(final BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal(100)) > 0) {
            return "percentage should be in range 0 - 100";
        }
        return null;
    }

    //endregion



    @Override
    @MemberOrder(sequence = "7")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getSchedule().getApplicationTenancy();
    }

}