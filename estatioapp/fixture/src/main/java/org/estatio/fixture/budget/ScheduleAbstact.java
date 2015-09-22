/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.fixture.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.Budgets;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItems;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.fixture.EstatioFixtureScript;

/**
 * Created by jodo on 22/04/15.
 */
public abstract class ScheduleAbstact extends EstatioFixtureScript {


    protected Schedule createSchedule(
            final Property property,
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge charge,
            final Schedule.Status status,
            final ExecutionContext fixtureResults){
        Schedule schedule = schedulesRepo.newSchedule(property,budget,startDate,endDate,charge, status);

        return fixtureResults.addResult(this, schedule);
    }

    protected void createScheduleItem(
            final Schedule schedule,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage
    ){
        scheduleItemRepo.newScheduleItem(schedule,keyTable,budgetItem, percentage);
    }

    @Inject
    protected Schedules schedulesRepo;

    @Inject
    protected ScheduleItems scheduleItemRepo;

    @Inject
    protected PropertyRepository propertyRepository;

    @Inject
    protected Budgets budgetRepository;

    @Inject
    protected Charges chargesRepository;

    @Inject
    protected KeyTables keyTableRepository;

}
