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
package org.estatio.module.asset.integtests.count;

import java.math.BigInteger;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.counts.Count;
import org.estatio.module.asset.dom.counts.CountRepository;
import org.estatio.module.asset.dom.counts.Type;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.imports.CountImport;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;

public class CountImport_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Property_enum.OxfGb);
            }
        });
    }

    @Test
    public void import_works() throws Exception {

        // given
        final Property oxf = Property_enum.OxfGb.findUsing(serviceRegistry2);
        final LocalDate date = new LocalDate(2019, 1, 1);
        final BigInteger pCount = BigInteger.valueOf(345);
        final BigInteger cCount = BigInteger.valueOf(123);

        CountImport p1 = new CountImport("OXF", "PEDESTRIAL", date, pCount);
        CountImport c1 = new CountImport("OXF", "CAR", date, cCount);
        serviceRegistry2.injectServicesInto(p1);
        serviceRegistry2.injectServicesInto(c1);

        // when
        p1.importData();
        c1.importData();

        // then
        Assertions.assertThat(countRepository.listAll()).hasSize(2);
        Count pcnt1 = countRepository.findByPropertyAndType(oxf, Type.PEDESTRIAL).get(0);
        Assertions.assertThat(pcnt1.getProperty()).isEqualTo(oxf);
        Assertions.assertThat(pcnt1.getDate()).isEqualTo(date);
        Assertions.assertThat(pcnt1.getValue()).isEqualTo(pCount);
        Count ccnt1 = countRepository.findByPropertyAndType(oxf, Type.CAR).get(0);
        Assertions.assertThat(ccnt1.getProperty()).isEqualTo(oxf);
        Assertions.assertThat(ccnt1.getDate()).isEqualTo(date);
        Assertions.assertThat(ccnt1.getValue()).isEqualTo(cCount);

    }

    @Inject CountRepository countRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}