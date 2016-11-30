package org.estatio.dom.lease;

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

import java.util.regex.Pattern;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.objectvalue.regex.RegExFacet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class Lease_ReferenceType_RegexValidation_Test {

    private final Mockery context = new JUnit4Mockery();

    private RegExFacet regExFacetOnTypeAnnotation;
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
    public void testLeaseReference() {
        tester(Lease.ReferenceType.Meta.REGEX, "AAA-A0A-A1&+=_-", true);
        tester(Lease.ReferenceType.Meta.REGEX, "AAA-A0A0-/A/1&+", true);
        tester(Lease.ReferenceType.Meta.REGEX, "AAA-A0A0A-=_-//", true);
        tester(Lease.ReferenceType.Meta.REGEX, "AAA-A0A0A0-A1&+", true);
        tester(Lease.ReferenceType.Meta.REGEX, "AAA-A0A0A0A-=_-", true);
        tester(Lease.ReferenceType.Meta.REGEX, "AA-A0AA1&+=_-", true);
        tester(Lease.ReferenceType.Meta.REGEX, "AA-A0A0A0A-=_-", true);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(Lease.ReferenceType.Meta.REGEX, "AAA-A0A0A0A-=-", true);

        tester(Lease.ReferenceType.Meta.REGEX, "XZ--A0A0A0A-=-", true);

        tester(Lease.ReferenceType.Meta.REGEX, "X-AAA-A0A-A1&+=_-", true);
        tester(Lease.ReferenceType.Meta.REGEX, "Z-AAA-A0A0-/A1&/+", true);
        tester(Lease.ReferenceType.Meta.REGEX, "X-AAA-A0A0A-=_-//", true);
        tester(Lease.ReferenceType.Meta.REGEX, "Z-AAA-A0A0A0-A1&+", true);
        tester(Lease.ReferenceType.Meta.REGEX, "X-AAA-A0A0A0A-=_-", true);
        tester(Lease.ReferenceType.Meta.REGEX, "Z--AA--A0A0A0A-=-", false);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(Lease.ReferenceType.Meta.REGEX, "X-AAA-A0A0A0A-=-", true);

        tester(Lease.ReferenceType.Meta.REGEX, "XZ-AAA-A0A0A0A-=-", false);

        tester(Lease.ReferenceType.Meta.REGEX, "POR-MOROGLI-016", true);
        tester(Lease.ReferenceType.Meta.REGEX, "POR-MOROGLI2-16", true);
        tester(Lease.ReferenceType.Meta.REGEX, "POR-MOROGLI2-1", true);
        tester(Lease.ReferenceType.Meta.REGEX, "X-POR-MOROGLI2-1", true);

        // Reopened EST-521 as a 16 character lease was being accepted
        tester(Lease.ReferenceType.Meta.REGEX, "CAR-GUESS2-1112N", false);
        tester(Lease.ReferenceType.Meta.REGEX, "CAR-GUES2-1112N", true);

        tester(Lease.ReferenceType.Meta.REGEX, "X-CAR-GUESS2-1112N", false);
        tester(Lease.ReferenceType.Meta.REGEX, "Z-CAR-GUES2-1112N", true);

        tester(Lease.ReferenceType.Meta.REGEX, "X-CAR-GUESS2-1112N", false);
        tester(Lease.ReferenceType.Meta.REGEX, "Z-CAR-GUES2-1112N", true);

        // ECP-130
        tester(Lease.ReferenceType.Meta.REGEX, "CH-CELIO", true);
        tester(Lease.ReferenceType.Meta.REGEX, "CH-CE LIO", true);

        // ECP-130
        tester(Lease.ReferenceType.Meta.REGEX, "CH-KRYS", true);
        tester(Lease.ReferenceType.Meta.REGEX, "CH-CE LIO", true);
    }

    private void tester(String regex, String pattern, boolean expected) {
        assertThat( Pattern.compile(regex, 0).matcher(pattern).matches(), equalTo(expected));
    }
}
