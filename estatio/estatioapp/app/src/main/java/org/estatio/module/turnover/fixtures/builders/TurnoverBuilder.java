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

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"config", "turnoverDate"}, callSuper = false)
@ToString(of={"config", "turnoverDate"})
@Accessors(chain = true)
public class TurnoverBuilder extends BuilderScriptAbstract<Turnover, TurnoverBuilder> {

    @Getter @Setter
    TurnoverReportingConfig config;

    @Getter @Setter
    LocalDate turnoverDate;

    @Getter @Setter
    BigDecimal turnoverGrossAmount;

    @Getter @Setter
    BigDecimal turnoverNetAmount;

    @Getter @Setter
    BigInteger purchaseCount;

    @Getter @Setter
    Boolean nonComparable;

    @Getter
    Turnover object;


    @Override
    protected void execute(ExecutionContext ec) {

        checkParam("config", ec, TurnoverReportingConfig.class);
        final Turnover turnover = turnoverRepository
                .createNewEmpty(config, turnoverDate, config.getType(), config.getFrequency(), config.getCurrency());
        turnover.setGrossAmount(getTurnoverGrossAmount());
        turnover.setNetAmount(getTurnoverNetAmount());
        turnover.setPurchaseCount(getPurchaseCount());
        turnover.setNonComparable(nonComparable);
        turnover.setStatus(Status.APPROVED);
        ec.addResult(this, object);

        object = turnover;

    }

    // //////////////////////////////////////

    @Inject
    TurnoverRepository turnoverRepository;

}
