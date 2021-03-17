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
package org.estatio.module.turnover.fixtures.builders;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"occupancy", "typeEnum"}, callSuper = false)
@ToString(of={"occupancy", "typeEnum"})
@Accessors(chain = true)
public class TurnoverReportingConfigBuilder extends BuilderScriptAbstract<TurnoverReportingConfig, TurnoverReportingConfigBuilder> {

    @Getter @Setter
    Occupancy occupancy;
    @Getter @Setter
    Type typeEnum;
    @Getter @Setter
    Person reporter;
    @Getter @Setter
    LocalDate startDate;
    @Getter @Setter
    Frequency frequency;
    @Getter @Setter
    Frequency auditedFrequency;
    @Getter @Setter
    Currency currency;

    @Getter
    TurnoverReportingConfig object;


    @Override
    protected void execute(ExecutionContext ec) {

        checkParam("occupancy", ec, Occupancy.class);
        checkParam("typeEnum", ec, Type.class);
        checkParam("startDate", ec, LocalDate.class);
        checkParam("frequency", ec, Frequency.class);
        checkParam("currency", ec, Currency.class);


        final TurnoverReportingConfig config =
                turnoverReportingConfigRepository.findOrCreate(
                        occupancy,
                        typeEnum,
                        reporter,
                        startDate,
                        frequency,
                        currency);

        ec.addResult(this, config);

        object = config;

    }

    // //////////////////////////////////////

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;

}
