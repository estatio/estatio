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
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.schedule.Schedule;

@DomainService(repositoryFor = ScheduleItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class ScheduleItems extends UdoDomainRepositoryAndFactory<ScheduleItem> {

    public ScheduleItems() {
        super(ScheduleItems.class, ScheduleItem.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public ScheduleItem newScheduleItem(
            final Schedule schedule,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {

        ScheduleItem scheduleItem = newTransientInstance(ScheduleItem.class);
        scheduleItem.setSchedule(schedule);
        scheduleItem.setKeyTable(keyTable);
        scheduleItem.setBudgetItem(budgetItem);
        scheduleItem.setPercentage(percentage);

        persistIfNotAlready(scheduleItem);

        return scheduleItem;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout()
    public List<ScheduleItem> allScheduleItems() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<ScheduleItem> findBySchedule(final Schedule schedule) {
        return allMatches("findBySchedule", "schedule", schedule);
    }

    @Programmatic
    public List<ScheduleItem> findByBudgetItem(final BudgetItem budgetItem) {
        return allMatches("findByBudgetItem", "budgetItem", budgetItem);
    }

    @Programmatic
    public List<ScheduleItem> findByKeyTable(final KeyTable keyTable) {
        return allMatches("findByKeyTable", "keyTable", keyTable);
    }

    @Programmatic
    public ScheduleItem findByScheduleAndBudgetItemAndKeyTable(final Schedule schedule, final BudgetItem budgetItem, final KeyTable keyTable) {
        return uniqueMatch("findByScheduleAndBudgetItemAndKeyTable", "schedule", schedule, "budgetItem", budgetItem, "keyTable", keyTable);
    }
}
