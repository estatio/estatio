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
package org.estatio.module.turnover.integtests;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.fixtures.data.TurnoverReportingConfig_enum;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLink;
import org.estatio.module.turnover.dom.aggregation.TurnoverReportingConfigLinkRepository;
import org.estatio.module.turnoveraggregate.integtests.TurnoverAggregateModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnoverReportingConfigLinkRepository_IntegTest extends TurnoverModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Currency_enum.EUR.builder());
                executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfTopModel001GbPrelim.builder());
                executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfMediaX002GbPrelim.builder());
                executionContext.executeChild(this, TurnoverReportingConfig_enum.OxfMiracl005GbAudit.builder());
            }
        });
    }

    @Test
    public void find_or_create_works() throws Exception {

        // given
        final TurnoverReportingConfig oxfTopCfg = TurnoverReportingConfig_enum.OxfTopModel001GbPrelim
                .findUsing(serviceRegistry2);
        final TurnoverReportingConfig oxfMedCfg = TurnoverReportingConfig_enum.OxfMediaX002GbPrelim
                .findUsing(serviceRegistry2);
        assertThat(turnoverReportingConfigLinkRepository.listAll()).isEmpty();

        // when
        final TurnoverReportingConfigLink link = turnoverReportingConfigLinkRepository
                .findOrCreate(oxfTopCfg, oxfMedCfg);
        // then
        assertThat(turnoverReportingConfigLinkRepository.listAll()).hasSize(1);
        assertThat(turnoverReportingConfigLinkRepository.findUnique(oxfTopCfg, oxfMedCfg)).isEqualTo(link);

        // and when again
        final TurnoverReportingConfigLink linkSame = turnoverReportingConfigLinkRepository
                .findOrCreate(oxfTopCfg, oxfMedCfg);
        // then still
        assertThat(turnoverReportingConfigLinkRepository.listAll()).hasSize(1);
        assertThat(link).isSameAs(linkSame);
    }

    @Test
    public void findByTurnoverReportingConfig_works() throws Exception {

        // given
        final TurnoverReportingConfig oxfTopCfg = TurnoverReportingConfig_enum.OxfTopModel001GbPrelim
                .findUsing(serviceRegistry2);
        final TurnoverReportingConfig oxfMedCfg = TurnoverReportingConfig_enum.OxfMediaX002GbPrelim
                .findUsing(serviceRegistry2);
        final TurnoverReportingConfig oxfMirCfg = TurnoverReportingConfig_enum.OxfMiracl005GbAudit
                .findUsing(serviceRegistry2);
        assertThat(turnoverReportingConfigLinkRepository.listAll()).isEmpty();

        // when
        final TurnoverReportingConfigLink link1 = turnoverReportingConfigLinkRepository
                .findOrCreate(oxfTopCfg, oxfMedCfg);
        final TurnoverReportingConfigLink link2 = turnoverReportingConfigLinkRepository
                .findOrCreate(oxfTopCfg, oxfMirCfg);
        final TurnoverReportingConfigLink link3 = turnoverReportingConfigLinkRepository
                .findOrCreate(oxfMedCfg, oxfMirCfg);

        // then
        final List<TurnoverReportingConfigLink> configsFound = turnoverReportingConfigLinkRepository
                .findByTurnoverReportingConfig(oxfTopCfg);
        assertThat(configsFound).hasSize(2);
        assertThat(configsFound).contains(link1);
        assertThat(configsFound).contains(link2);
        assertThat(configsFound).doesNotContain(link3);

    }

    @Inject TurnoverReportingConfigLinkRepository turnoverReportingConfigLinkRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}