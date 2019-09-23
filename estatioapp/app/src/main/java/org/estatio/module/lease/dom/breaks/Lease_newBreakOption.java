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
package org.estatio.module.lease.dom.breaks;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;
import org.joda.time.LocalDate;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.lease.dom.Lease;

@Mixin(method = "act")
public class Lease_newBreakOption extends UdoDomainService<Lease_newBreakOption> {

    private final Lease lease;

    public Lease_newBreakOption(Lease lease) {
        super(Lease_newBreakOption.class);
        this.lease = lease;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "breakOptions", sequence = "1")
    public Lease act(
            final LocalDate breakDate,
            final @ParameterLayout(describedAs = "Notification period in a text format. Example 6y5m2d") String notificationPeriod,
            final BreakType breakType,
            final BreakExerciseType breakExerciseType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description
    ) {
        return ccoBreakOptionContributions.newBreakOption(this.lease, breakDate, notificationPeriod, breakType, breakExerciseType, description);
    }

    public String validateAct(
            final LocalDate breakDate,
            final String notificationPeriodStr,
            final BreakType breakType,
            final BreakExerciseType breakExerciseType,
            final String description) {

        return ccoBreakOptionContributions.validateNewBreakOption(this.lease, breakDate, notificationPeriodStr, breakType, breakExerciseType, description);
    }

    public LocalDate default0Act() {
        // REVIEW: this is just a guess as to a reasonable default
        return ccoBreakOptionContributions.default1NewBreakOption();
    }
    
    public BreakType default2Act() {
        return ccoBreakOptionContributions.default3NewBreakOption();
    }

    public BreakExerciseType default3Act() {
        return ccoBreakOptionContributions.default4NewBreakOption();
    }

    @Inject
    LeaseService ccoBreakOptionContributions;
}
