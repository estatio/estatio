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
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.fixtures.builders.TurnoverReportingConfigBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum TurnoverReportingConfig_enum
        implements PersonaWithBuilderScript<TurnoverReportingConfig, TurnoverReportingConfigBuilder>, PersonaWithFinder<TurnoverReportingConfig> {


    OxfTopModel001GbPrelim(
            Lease_enum.OxfTopModel001Gb, Lease_enum.OxfTopModel001Gb.getManager_d(), Type.PRELIMINARY, ld(2014, 1, 2), Frequency.MONTHLY, Currency_enum.EUR),
    OxfTopModel001GbAudit(
            Lease_enum.OxfTopModel001Gb, Lease_enum.OxfTopModel001Gb.getManager_d(), Type.AUDITED, ld(2014, 1, 2), Frequency.YEARLY, Currency_enum.EUR),
    BudPoison001NlPrelim(
            Lease_enum.BudPoison001Nl, null, Type.PRELIMINARY, ld(2014, 1, 1), Frequency.MONTHLY, Currency_enum.EUR),
    BudPoison001NlAudit(
            Lease_enum.BudPoison001Nl, null, Type.AUDITED, ld(2014, 1, 1), Frequency.YEARLY, Currency_enum.EUR),
    BudMiracle002NlPrelim(
            Lease_enum.BudMiracle002Nl, null, Type.PRELIMINARY, ld(2014, 1, 1), Frequency.MONTHLY, Currency_enum.EUR),
    BudMiracle002NlAudit(
            Lease_enum.BudMiracle002Nl, null, Type.AUDITED, ld(2014, 1, 1), Frequency.YEARLY, Currency_enum.EUR),
    BudDago004NlPrelim(
            Lease_enum.BudDago004Nl, null, Type.PRELIMINARY, ld(2014, 1, 1), Frequency.MONTHLY, Currency_enum.EUR),
    BudDago004NlAudit(
            Lease_enum.BudDago004Nl, null, Type.AUDITED, ld(2014, 1, 1), Frequency.YEARLY, Currency_enum.EUR),

    OxfMiracl005GbPrelim(
            Lease_enum.OxfMiracl005Gb, Person_enum.JohnTurnover, Type.PRELIMINARY, ld(2014, 1, 1), Frequency.MONTHLY, Currency_enum.EUR),
    OxfMiracl005GbAudit(
            Lease_enum.OxfMiracl005Gb, Person_enum.JohnTurnover, Type.AUDITED, ld(2014, 1, 1), Frequency.YEARLY, Currency_enum.EUR),
    OxfMediaX002GbPrelim(
            Lease_enum.OxfMediaX002Gb, Person_enum.JohnTurnover, Type.PRELIMINARY, ld(2014, 1, 1), Frequency.MONTHLY, Currency_enum.EUR),
    OxfMediaX002GbAudit(
            Lease_enum.OxfMediaX002Gb, Person_enum.JohnTurnover, Type.AUDITED, ld(2014, 1, 1), Frequency.YEARLY, Currency_enum.EUR),
    OxfPoison003GbPrelim(
            Lease_enum.OxfPoison003Gb, Person_enum.JohnTurnover, Type.PRELIMINARY, ld(2014, 1, 1), Frequency.MONTHLY, Currency_enum.EUR),
    OxfPoison003GbAudit(
            Lease_enum.OxfPoison003Gb, Person_enum.JohnTurnover, Type.AUDITED, ld(2014, 1, 1), Frequency.YEARLY, Currency_enum.EUR),
    ;

    private final Lease_enum lease_d;
    private final Person_enum person_d;
    private final Type type;
    private final LocalDate startDate;
    private final Frequency frequency;
    private final Currency_enum currency_d;

    @Override
    public TurnoverReportingConfigBuilder builder() {
        return new TurnoverReportingConfigBuilder()
                .setPrereq((f,ec) -> f.setOccupancy(f.objectFor(lease_d, ec).getOccupancies().first()))
                .setPrereq((f,ec) -> f.setReporter(f.objectFor(person_d, ec)))
                .setTypeEnum(type)
                .setStartDate(startDate)
                .setFrequency(frequency)
                .setPrereq((f, ec) -> f.setCurrency(f.objectFor(currency_d, ec)));
    }

    @Override
    public TurnoverReportingConfig findUsing(final ServiceRegistry2 serviceRegistry) {
        final TurnoverReportingConfigRepository turnoverReportingConfigRepository = serviceRegistry.lookupService(TurnoverReportingConfigRepository.class);
        final OccupancyRepository occupancyRepository = serviceRegistry.lookupService(OccupancyRepository.class);
        final Occupancy occupancy = occupancyRepository.findByLease(lease_d.findUsing(serviceRegistry)).stream().findFirst().orElse(null);
        return turnoverReportingConfigRepository.findUnique(occupancy, type);
    }

}
