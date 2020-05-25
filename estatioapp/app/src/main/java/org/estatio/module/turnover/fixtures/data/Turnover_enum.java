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
package org.estatio.module.turnover.fixtures.data;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.fixtures.builders.TurnoverBuilder;
import org.estatio.module.turnover.fixtures.builders.TurnoverReportingConfigBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Turnover_enum
        implements PersonaWithBuilderScript<Turnover, TurnoverBuilder>, PersonaWithFinder<Turnover> {


    OxfTopModel001Gb202001(
            TurnoverReportingConfig_enum.OxfTopModel001GbPrelim, new LocalDate(2020,1,1), new BigDecimal("1234.45"), new BigDecimal("1000.01"), BigInteger.valueOf(123), false),
    OxfTopModel001Gb201901(
            TurnoverReportingConfig_enum.OxfTopModel001GbPrelim, new LocalDate(2019,1,1), new BigDecimal("1244.45"), null, BigInteger.valueOf(124), false)

    ;

    private final TurnoverReportingConfig_enum config_d;
    private final LocalDate turnoverDate;
    private final BigDecimal grossAmount;
    private final BigDecimal netAmount;
    private final BigInteger purchaseCount;
    private final Boolean nonComparable;

    @Override
    public TurnoverBuilder builder() {
        return new TurnoverBuilder()
                .setPrereq((f,ec) -> f.setConfig(f.objectFor(config_d, ec))
                .setTurnoverDate(turnoverDate)
                .setTurnoverGrossAmount(grossAmount)
                .setTurnoverNetAmount(netAmount)
                .setPurchaseCount(purchaseCount)
                .setNonComparable(nonComparable)
                );
    }

    @Override
    public Turnover findUsing(final ServiceRegistry2 serviceRegistry) {
        final TurnoverRepository turnoverRepository = serviceRegistry.lookupService(TurnoverRepository.class);
        final TurnoverReportingConfig config = config_d.findUsing(serviceRegistry);
        return turnoverRepository.findUnique(config, turnoverDate, config.getType());
    }

}
