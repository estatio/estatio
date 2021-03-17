/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.module.asset.integtests.count;

import java.math.BigInteger;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.counts.Count;
import org.estatio.module.asset.dom.counts.CountRepository;
import org.estatio.module.asset.dom.counts.Type;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;

public class CountRepository_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChildren(this, Property_enum.OxfGb);
            }
        });

    }

    @Test
    public void upsert_works() throws Exception {

        // given
        Property oxf = Property_enum.OxfGb.findUsing(serviceRegistry);
        Assertions.assertThat(countRepository.listAll()).isEmpty();

        // when
        final LocalDate date = new LocalDate(2019, 1, 1);
        Count cnt = countRepository.upsert(oxf, Type.PEDESTRIAL, date, BigInteger.valueOf(123));


        // then
        Assertions.assertThat(countRepository.listAll()).hasSize(1);
        Count cnt1 = countRepository.findUnique(oxf, Type.PEDESTRIAL, date);
        Assertions.assertThat(cnt1).isSameAs(cnt);

        // and when
        final BigInteger newPedCnt = BigInteger.valueOf(333);
        countRepository.upsert(
                oxf,
                Type.PEDESTRIAL,
                date,
                newPedCnt);
        // then
        Assertions.assertThat(countRepository.listAll()).hasSize(1);
        Assertions.assertThat(cnt.getValue()).isEqualTo(newPedCnt);
    }

    @Inject CountRepository countRepository;

}
