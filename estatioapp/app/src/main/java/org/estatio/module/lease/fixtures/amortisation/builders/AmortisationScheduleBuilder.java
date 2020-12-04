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
package org.estatio.module.lease.fixtures.amortisation.builders;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"property"}, callSuper = false)
@ToString(of={"property"})
@Accessors(chain = true)
public final class AmortisationScheduleBuilder
        extends BuilderScriptAbstract<AmortisationSchedule, AmortisationScheduleBuilder> {

    @Getter @Setter
    private Lease lease;

    @Getter @Setter
    private Charge charge;

    @Getter @Setter
    private BigDecimal scheduledValue;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private BigInteger sequence;

    @Getter
    private AmortisationSchedule object;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("lease", ec, Lease.class);
        checkParam("charge", ec, Charge.class);

        this.object = amortisationScheduleRepository.findOrCreate(
                lease,
                charge,
                scheduledValue,
                Frequency.MONTHLY,
                startDate,
                startDate.plusYears(3).minusDays(1),
                sequence);

        object.createAndDistributeEntries();

        String key = object.toString();
        ec.addResult(this, key, object);
    }



    @Inject AmortisationScheduleRepository amortisationScheduleRepository;

}
