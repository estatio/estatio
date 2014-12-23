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

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.regex.annotation.RegExFacetOnTypeAnnotation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
        tester(RegexValidation.Unit.REFERENCE, "ABC-123", true);
        tester(RegexValidation.Unit.REFERENCE, "ABC- 123", false);
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

    private void tester(String regex, String pattern, boolean expected) {
        regExFacetOnTypeAnnotation = new RegExFacetOnTypeAnnotation(regex, "", expected, facetHolder);
        assertThat(regExFacetOnTypeAnnotation.doesNotMatch(pattern), equalTo(!expected));
    }
}
