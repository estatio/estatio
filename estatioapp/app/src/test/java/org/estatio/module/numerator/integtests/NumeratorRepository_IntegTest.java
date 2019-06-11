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
package org.estatio.module.numerator.integtests;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.numerator.integtests.dom.NumeratorExampleObject;
import org.estatio.module.numerator.integtests.dom.NumeratorExampleObject_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorRepository_IntegTest extends NumeratorModuleIntegTestAbstract {

    @Inject
    NumeratorRepository numeratorRepository;

    NumeratorExampleObject exampleObjectOxf;
    NumeratorExampleObject exampleObjectKal;

    Country countryGbr;
    Country countryNld;

    ApplicationTenancy applicationTenancyGlobal;
    ApplicationTenancy applicationTenancyGb;
    ApplicationTenancy applicationTenancyNl;

    static final String NAME = "ABC";
    static final String NAME2 = "DEF";

    static final String FORMAT = "ABC-%04d";
    static final String FORMAT2 = "DEF-%06d";

    @Before
    public void setUp() throws Exception {
        runFixtureScript(new PersonaEnumPersistAll<>(NumeratorExampleObject_enum.class));
        runFixtureScript(new PersonaEnumPersistAll<>(Country_enum.class));
        runFixtureScript(new PersonaEnumPersistAll<>(ApplicationTenancy_enum.class));

        countryNld = Country_enum.NLD.findUsing(serviceRegistry);
        countryGbr = Country_enum.GBR.findUsing(serviceRegistry);

        applicationTenancyGlobal = ApplicationTenancy_enum.Global.findUsing(serviceRegistry);
        applicationTenancyNl = ApplicationTenancy_enum.Nl.findUsing(serviceRegistry);
        applicationTenancyGb = ApplicationTenancy_enum.Gb.findUsing(serviceRegistry);

        exampleObjectKal = NumeratorExampleObject_enum.Kal.findUsing(serviceRegistry);
        exampleObjectOxf = NumeratorExampleObject_enum.Oxf.findUsing(serviceRegistry);

        // given
        final List<Numerator> numeratorsBefore = numeratorRepository.allNumerators();
        assertThat(numeratorsBefore).isEmpty();

    }

    @Test
    public void when_null_country() throws Exception {

        // when
        numeratorRepository.findOrCreate(
                NAME, null, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGlobal);

        // then
        final List<Numerator> numeratorsAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter).hasSize(1);
        final Numerator numerator = numeratorsAfter.get(0);

        assertThat(numerator.getName()).isEqualTo(NAME);
        assertThat(numerator.getCountry()).isNull();
        assertThat(numerator.getObject()).isNull();
        assertThat(numerator.getObject2()).isNull();
        assertThat(numerator.getFormat()).isEqualTo(FORMAT);
        assertThat(numerator.getApplicationTenancy()).isSameAs(applicationTenancyGlobal);

        // and when
        numeratorRepository.findOrCreate(
                NAME, null, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGlobal);

        final List<Numerator> numeratorsAfter2 = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter2).hasSize(1);
    }

    @Test
    public void when_non_null_country_but_null_objects() throws Exception {

        // when
        numeratorRepository.findOrCreate(
                NAME, countryGbr, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        // then
        final List<Numerator> numeratorsAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter).hasSize(1);
        final Numerator numerator = numeratorsAfter.get(0);

        assertThat(numerator.getName()).isEqualTo(NAME);
        assertThat(numerator.getCountry()).isSameAs(countryGbr);
        assertThat(numerator.getObject()).isNull();
        assertThat(numerator.getObject2()).isNull();
        assertThat(numerator.getFormat()).isEqualTo(FORMAT);
        assertThat(numerator.getApplicationTenancy()).isSameAs(applicationTenancyGb);


        // and when
        numeratorRepository.findOrCreate(
                NAME, null, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        final List<Numerator> numeratorsAfter2 = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter2).hasSize(1);
    }

    @Test
    public void when_non_null_object1() throws Exception {

        // when
        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        // then
        final List<Numerator> numeratorsAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter).hasSize(1);
        final Numerator numerator = numeratorsAfter.get(0);

        assertThat(numerator.getName()).isEqualTo(NAME);
        assertThat(numerator.getCountry()).isSameAs(countryGbr);
        assertThat(numerator.getObject()).isSameAs(exampleObjectOxf);
        assertThat(numerator.getObject2()).isNull();
        assertThat(numerator.getFormat()).isEqualTo(FORMAT);
        assertThat(numerator.getApplicationTenancy()).isSameAs(applicationTenancyGb);


        // and when
        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        final List<Numerator> numeratorsAfter2 = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter2).hasSize(1);

    }

    @Test
    public void when_non_null_object1_and_non_null_object2() throws Exception {

        // when
        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, exampleObjectKal, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        // then
        final List<Numerator> numeratorsAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter).hasSize(1);
        final Numerator numerator = numeratorsAfter.get(0);

        assertThat(numerator.getName()).isEqualTo(NAME);
        assertThat(numerator.getCountry()).isSameAs(countryGbr);
        assertThat(numerator.getObject()).isSameAs(exampleObjectOxf);
        assertThat(numerator.getObject2()).isSameAs(exampleObjectKal);
        assertThat(numerator.getFormat()).isEqualTo(FORMAT);
        assertThat(numerator.getApplicationTenancy()).isSameAs(applicationTenancyGb);


        // and when
        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, exampleObjectKal, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        final List<Numerator> numeratorsAfter2 = numeratorRepository.allNumerators();
        assertThat(numeratorsAfter2).hasSize(1);
    }

    @Test
    public void when_different_scopes() throws Exception {

        // given
        numeratorRepository.findOrCreate(
                NAME, null, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGlobal);
        numeratorRepository.findOrCreate(
                NAME2, null, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGlobal);

        numeratorRepository.findOrCreate(
                NAME, countryGbr, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);
        numeratorRepository.findOrCreate(
                NAME, countryNld, null, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);
        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectKal, null, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, exampleObjectKal, FORMAT, BigInteger.ZERO, applicationTenancyGb);
        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectKal, exampleObjectOxf, FORMAT, BigInteger.ZERO, applicationTenancyGb);

        // when
        final List<Numerator> numerators = numeratorRepository.allNumerators();

        // then
        assertThat(numerators).hasSize(8);


        // when attempt to create again, with different format or lastIncrement or appTenancy
        numeratorRepository.findOrCreate(
                NAME, null, null, null, FORMAT2, BigInteger.ZERO, applicationTenancyGb);

        numeratorRepository.findOrCreate(
                NAME, countryGbr, null, null, FORMAT2, BigInteger.ZERO, applicationTenancyNl);

        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, null, FORMAT2, BigInteger.ZERO, applicationTenancyNl);

        numeratorRepository.findOrCreate(
                NAME, countryGbr, exampleObjectOxf, exampleObjectKal, FORMAT2, BigInteger.ZERO, applicationTenancyNl);

        final List<Numerator> numeratorsAfter = numeratorRepository.allNumerators();

        // then
        assertThat(numeratorsAfter).hasSize(8);
        assertThat(numeratorsAfter).containsExactlyElementsOf(numerators);

    }


}