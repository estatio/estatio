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

import javax.inject.Inject;

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
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTable;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.currency.Currencies;

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
            final BudgetKeyTable budgetKeyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {

        ScheduleItem scheduleItem = newTransientInstance(ScheduleItem.class);
        scheduleItem.setSchedule(schedule);
        scheduleItem.setBudgetKeyTable(budgetKeyTable);
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
    public List<ScheduleItem> findScheduleItemBySchedule(final Schedule schedule) {
        return allMatches("findScheduleItemBySchedule", "schedule", schedule);
    }

    @Inject
    Currencies currencies;
}
