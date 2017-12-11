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
package org.estatio.module.lease.dom.breaks.prolongation;

import java.util.Set;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.utils.JodaPeriodUtils;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.breaks.BreakOption;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"   // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.breaks.prolongation.ProlongationOption "
                        + "WHERE lease == :lease")})

@javax.jdo.annotations.Discriminator("org.estatio.dom.lease.breaks.prolongation.ProlongationOption")
@DomainObject()
public class ProlongationOption
        extends BreakOption {

    private static final String CALENDAR_NAME_PROLONGATION = "Prolongation date";
    private static final String CALENDAR_NAME_PROLONGATION_EXERCISE = "Prolongation exercise";
    private static final String CALENDAR_NAME_PROLONGATION_EXERCISE_REMINDER = "Prolongation exercise reminder";

    // //////////////////////////////////////

    @Programmatic
    @Override
    public Set<String> getCalendarNames() {
        return Sets.newHashSet(
                CALENDAR_NAME_PROLONGATION,
                CALENDAR_NAME_PROLONGATION_EXERCISE,
                CALENDAR_NAME_PROLONGATION_EXERCISE_REMINDER);
    }

    public static String nameExerciseDate() {
        return "Last exercise date";
    }

    @Override
    public LocalDate getCurrentBreakDate() {
        return getBreakDate().isBefore(getClockService().now()) ? getBreakDate() : null;
    }

    @Getter @Setter
    @Column(length = 20)
    private String prolongationPeriod;


    @Override
    protected void createEvents() {
        createEvent(getBreakDate(), this, CALENDAR_NAME_PROLONGATION);
        createEvent(getExerciseDate(), this, CALENDAR_NAME_PROLONGATION_EXERCISE);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Lease prolong(){
        getLease().setEndDate(getBreakDate().plus(JodaPeriodUtils.asPeriod(getProlongationPeriod())));
        return prolongationOptionRepository.newProlongationOption(getLease(), getProlongationPeriod(), getNotificationPeriod(), getDescription());
    }

    @Inject
    ProlongationOptionRepository prolongationOptionRepository;

}
