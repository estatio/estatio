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
package org.estatio.module.party.integtests.app;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.app.NumeratorForOrganisationMenu;
import org.estatio.module.party.integtests.PartyModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorForOrganisationMenu_IntegTest extends PartyModuleIntegTestAbstract {

    private final String format = "GBFO-%04d";
    private final BigInteger lastIncrement = BigInteger.ZERO;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {
                ec.executeChild(this, ApplicationTenancy_enum.Gb);
            }
        });
    }

    ApplicationTenancy applicationTenancy;

    @Inject
    NumeratorForOrganisationMenu numeratorForOrganisationMenu;
    @Inject
    NumeratorRepository numeratorRepository;

    @Before
    public void setUp() throws Exception {

        applicationTenancy = ApplicationTenancy_enum.Gb.findUsing(serviceRegistry);
        assertThat(applicationTenancy).isNotNull();

        List<Numerator> numerators = numeratorRepository.allNumerators();
        assertThat(numerators).isEmpty();
    }

    @Test
    public void create_and_find() throws Exception {

        // when
        final Numerator numeratorBefore = numeratorForOrganisationMenu
                .findOrganisationReferenceNumerator(applicationTenancy);

        // then
        assertThat(numeratorBefore).isNull();

        // when
        final Numerator numerator = numeratorForOrganisationMenu
                .createOrganisationReferenceNumerator(format, lastIncrement, applicationTenancy);

        // then
        assertThat(numerator).isNotNull();
        assertThat(numerator.getApplicationTenancy()).isSameAs(applicationTenancy);
        assertThat(numerator.getFormat()).isEqualTo(format);
        assertThat(numerator.getLastIncrement()).isEqualTo(lastIncrement);

        // ... the organisation isn't persisted though... it's merely used
        assertThat(numerator.getObjectType()).isNull();
        assertThat(numerator.getObjectIdentifier()).isNull();

        // when
        final Numerator numeratorAfter = numeratorForOrganisationMenu
                .findOrganisationReferenceNumerator(applicationTenancy);

        // then
        assertThat(numeratorAfter).isSameAs(numerator);
    }

}