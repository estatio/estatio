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
package org.estatio.module.registration.integtests.landregister;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.estatio.module.asset.dom.registration.FixedAssetRegistrationRepository;
import org.estatio.module.registration.dom.LandRegister;
import org.estatio.module.registration.dom.LandRegisters;
import org.estatio.module.registration.fixtures.personas.LandRegisterForOxfordUnit001;
import org.estatio.module.registration.integtests.RegistrationModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LandRegisters_IntegTest extends RegistrationModuleIntegTestAbstract {

    public static class NewRegistration extends LandRegisters_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new LandRegisterForOxfordUnit001());
                }
            });
        }

        @Inject
        private LandRegisters landRegisters;

        @Inject
        FixedAssetRegistrationRepository fixedAssetRegistrationRepository;

        @Inject
        IsisJdoSupport isisJdoSupport;

        @Test
        public void current() {

            LandRegister landregister = (LandRegister) fixedAssetRegistrationRepository.allRegistrations().get(0);
            assertThat(landregister.getComuneAmministrativo(), is("comuneAmministrativo"));
            assertThat(landregister.getComuneCatastale(), is("comuneCatastale"));
            assertThat(landregister.getCodiceComuneCatastale(), is("codiceComuneCatastale"));
            assertThat(landregister.getRendita(), is(new BigDecimal("123.45")));
            assertThat(landregister.getFoglio(), is("foglio"));
            assertThat(landregister.getParticella(), is("particella"));
            assertThat(landregister.getSubalterno(), is("subalterno"));
            assertThat(landregister.getCategoria(), is("categoria"));
            assertThat(landregister.getClasse(), is("classe"));
            assertThat(landregister.getConsistenza(), is("consistenza"));

            LandRegister nextLandregister = landRegisters.newRegistration(
                    landregister.getSubject(),
                    landregister,
                    landregister.getComuneAmministrativo(),
                    landregister.getComuneCatastale(),
                    landregister.getCodiceComuneCatastale(),
                    landregister.getRendita(),
                    landregister.getFoglio(),
                    landregister.getParticella(),
                    landregister.getSubalterno(),
                    landregister.getCategoria(),
                    landregister.getClasse(),
                    landregister.getConsistenza(),
                    new LocalDate(2014, 1, 1),
                    "Change description");

            transactionService.flushTransaction();
            isisJdoSupport.refresh(nextLandregister);
            isisJdoSupport.refresh(landregister);

            assertThat(nextLandregister.getPrevious(), is(landregister));
            assertThat(nextLandregister.getDescription(), is("Change description"));
            assertThat(landregister.getNext(), is(nextLandregister));
            assertThat(landregister.getEndDate(), is(new LocalDate(2013, 12, 31)));

        }
    }
}