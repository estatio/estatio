package org.estatio.dom.asset;

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

public class Unit_ReferenceType_RegexValidation_UnitTest {

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
    public void testUnitReference() {
        tester(Unit.ReferenceType.Meta.REGEX, "AAA-XXXXXXXXXXX", true);
        tester(Unit.ReferenceType.Meta.REGEX, "AAA-A0A0A0A0A0A", true);
        tester(Unit.ReferenceType.Meta.REGEX, "AAA-A0A0A0A0", true);
        tester(Unit.ReferenceType.Meta.REGEX, "AAA-A+23A2-1", true);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(Unit.ReferenceType.Meta.REGEX, "AAA-A0", true);

        tester(Unit.ReferenceType.Meta.REGEX, "A-AAA-A0A0A0A0A0A", true);
        tester(Unit.ReferenceType.Meta.REGEX, "A-AAA-A0A0A0A0", true);
        tester(Unit.ReferenceType.Meta.REGEX, "A-AAA-A+23A2-1", true);

        // Used to be false. According to new constraints this should pass the
        // test
        tester(Unit.ReferenceType.Meta.REGEX, "A-AAA-A0", true);

        tester(Unit.ReferenceType.Meta.REGEX, "A-AA-A0A0A0A0", true);

        tester(Unit.ReferenceType.Meta.REGEX, "POR-016", true);
        tester(Unit.ReferenceType.Meta.REGEX, "POR-16", true);
        tester(Unit.ReferenceType.Meta.REGEX, "POR-1", true);

        tester(Unit.ReferenceType.Meta.REGEX, "TOU0-1", false);
        tester(Unit.ReferenceType.Meta.REGEX, "TOU0-0001", false);
    }

    private void tester(String regex, String pattern, boolean expected) {
        assertThat( Pattern.compile(regex, 0).matcher(pattern).matches(), equalTo(expected));
    }
}
