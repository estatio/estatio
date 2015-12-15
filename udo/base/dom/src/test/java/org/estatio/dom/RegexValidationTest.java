package org.estatio.dom;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.regex.annotation.RegExFacetOnTypeAnnotation;

public class RegexValidationTest {

    private final Mockery context = new JUnit4Mockery();

    private RegExFacetOnTypeAnnotation regExFacetOnTypeAnnotation;
    private FacetHolder facetHolder;

    @Before
    public void setUp() throws Exception {
        facetHolder = context.mock(FacetHolder.class);
    }

    @After
    public void tearDown() throws Exception {
        facetHolder = null;
        regExFacetOnTypeAnnotation = null;
    }

    @Test
    public void testUnitReference() {
        tester(RegexValidation.Unit.REFERENCE, "AAA-XXXXXXXXXXX", true);
        tester(RegexValidation.Unit.REFERENCE, "AAA-A0A0A0A0A0A", true);
        tester(RegexValidation.Unit.REFERENCE, "AAA-A0A0A0A0", true);
        tester(RegexValidation.Unit.REFERENCE, "AAA-A+23A2-1", true);
        tester(RegexValidation.Unit.REFERENCE, "AA-A0A0A0A0", false);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(RegexValidation.Unit.REFERENCE, "AAA-A0", true);

        tester(RegexValidation.Unit.REFERENCE, "A-AAA-A0A0A0A0A0A", true);
        tester(RegexValidation.Unit.REFERENCE, "A-AAA-A0A0A0A0", true);
        tester(RegexValidation.Unit.REFERENCE, "A-AAA-A+23A2-1", true);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(RegexValidation.Unit.REFERENCE, "A-AAA-A0", true);

        tester(RegexValidation.Unit.REFERENCE, "A-AA-A0A0A0A0", false);

        tester(RegexValidation.Unit.REFERENCE, "POR-016", true);
        tester(RegexValidation.Unit.REFERENCE, "POR-16", true);
        tester(RegexValidation.Unit.REFERENCE, "POR-1", true);
    }

    @Test
    public void testPhoneNumer() {
        tester(RegexValidation.CommunicationChannel.PHONENUMBER, "+31 20 12344-12", true);
        tester(RegexValidation.CommunicationChannel.PHONENUMBER, "00316-57201234", true);
        tester(RegexValidation.CommunicationChannel.PHONENUMBER, "asd", false);
    }

    @Test
    public void testEmailAddress() {
        tester(RegexValidation.CommunicationChannel.EMAIL, "asd@@asd.com", false);
        tester(RegexValidation.CommunicationChannel.EMAIL, "asd@asd.com", true);
        tester(RegexValidation.CommunicationChannel.EMAIL, "a sd@asd.com", false);
        tester(RegexValidation.CommunicationChannel.EMAIL, "asd@asd", false);
        tester(RegexValidation.CommunicationChannel.EMAIL, "asd", false);
    }

    @Test
    public void testLeaseReference() {
        tester(RegexValidation.Lease.REFERENCE, "AAA-A0A-A1&+=_-", true);
        tester(RegexValidation.Lease.REFERENCE, "AAA-A0A0-/A/1&+", true);
        tester(RegexValidation.Lease.REFERENCE, "AAA-A0A0A-=_-//", true);
        tester(RegexValidation.Lease.REFERENCE, "AAA-A0A0A0-A1&+", true);
        tester(RegexValidation.Lease.REFERENCE, "AAA-A0A0A0A-=_-", true);
        tester(RegexValidation.Lease.REFERENCE, "AA-A0A0A0A-=_-", false);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(RegexValidation.Lease.REFERENCE, "AAA-A0A0A0A-=-", true);

        tester(RegexValidation.Lease.REFERENCE, "XZ--A0A0A0A-=-", false);

        tester(RegexValidation.Lease.REFERENCE, "X-AAA-A0A-A1&+=_-", true);
        tester(RegexValidation.Lease.REFERENCE, "Z-AAA-A0A0-/A1&/+", true);
        tester(RegexValidation.Lease.REFERENCE, "X-AAA-A0A0A-=_-//", true);
        tester(RegexValidation.Lease.REFERENCE, "Z-AAA-A0A0A0-A1&+", true);
        tester(RegexValidation.Lease.REFERENCE, "X-AAA-A0A0A0A-=_-", true);
        tester(RegexValidation.Lease.REFERENCE, "Z--AA--A0A0A0A-=-", false);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(RegexValidation.Lease.REFERENCE, "X-AAA-A0A0A0A-=-", true);

        tester(RegexValidation.Lease.REFERENCE, "XZ-AAA-A0A0A0A-=-", false);

        tester(RegexValidation.Lease.REFERENCE, "POR-MOROGLI-016", true);
        tester(RegexValidation.Lease.REFERENCE, "POR-MOROGLI2-16", true);
        tester(RegexValidation.Lease.REFERENCE, "POR-MOROGLI2-1", true);
        tester(RegexValidation.Lease.REFERENCE, "X-POR-MOROGLI2-1", true);

        // Reopened EST-521 as a 16 character lease was being accepted
        tester(RegexValidation.Lease.REFERENCE, "CAR-GUESS2-1112N", false);
        tester(RegexValidation.Lease.REFERENCE, "CAR-GUES2-1112N", true);

        tester(RegexValidation.Lease.REFERENCE, "X-CAR-GUESS2-1112N", false);
        tester(RegexValidation.Lease.REFERENCE, "Z-CAR-GUES2-1112N", true);

        tester(RegexValidation.Lease.REFERENCE, "X-CAR-GUESS2-1112N", false);
        tester(RegexValidation.Lease.REFERENCE, "Z-CAR-GUES2-1112N", true);
    }

    private void tester(String regex, String pattern, boolean expected) {
        regExFacetOnTypeAnnotation = new RegExFacetOnTypeAnnotation(regex, "", expected, facetHolder);
        assertThat(regExFacetOnTypeAnnotation.doesNotMatch(pattern), equalTo(!expected));
    }
}
