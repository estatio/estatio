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
package org.estatio.module.turnover.dom;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.Person;

@DomainService(repositoryFor = TurnoverReportingConfig.class, nature = NatureOfService.DOMAIN)
public class TurnoverReportingConfigRepository extends UdoDomainRepositoryAndFactory<TurnoverReportingConfig> {

    public TurnoverReportingConfigRepository() {
        super(TurnoverReportingConfigRepository.class, TurnoverReportingConfig.class);
    }

    public TurnoverReportingConfig findOrCreate(
            final Occupancy occupancy,
            final Person reporter,
            final LocalDate startDate,
            final Frequency prelimFrequency,
            final Frequency auditedFrequency) {
        TurnoverReportingConfig config = findUnique(occupancy);
        if (config==null){
            config = create(occupancy, reporter, startDate, prelimFrequency, auditedFrequency);
        }
        return config;
    }

    public TurnoverReportingConfig create(
            final Occupancy occupancy,
            final Person reporter,
            final LocalDate startDate,
            final Frequency prelimFrequency,
            final Frequency auditedFrequency) {
        TurnoverReportingConfig config = new TurnoverReportingConfig(occupancy, reporter, startDate, prelimFrequency, auditedFrequency);
        serviceRegistry2.injectServicesInto(config);
        repositoryService.persistAndFlush(config);
        return config;
    }

    public TurnoverReportingConfig findUnique(final Occupancy occupancy) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        TurnoverReportingConfig.class,
                        "findUnique",
                        "occupancy", occupancy));
    }

    public List<TurnoverReportingConfig> listAll() {
        return allInstances();
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    RepositoryService repositoryService;

}
