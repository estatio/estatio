/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.impl.applicability;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class Applicability_TitleSubscriber_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    FakeDataService faker = new FakeDataService() {{ init(); }};

    Applicability.TitleSubscriber titleSubscriber;

    @Mock
    DocumentTemplate mockDocumentTemplate;

    @Mock
    Applicability mockApplicability;

    @Before
    public void setUp() throws Exception {
        titleSubscriber = new Applicability.TitleSubscriber();
    }

    @Test
    public void happy_case() throws Exception {
        // given
        final Applicability.TitleUiEvent ev = new Applicability.TitleUiEvent();
        ev.setSource(mockApplicability);

        // expect
        context.checking(new Expectations() {{
            allowing(mockApplicability).getDomainClassName();
            will(returnValue("com.mycompany.SomeDomainObject"));

            allowing(mockApplicability).getDocumentTemplate();
            will(returnValue(mockDocumentTemplate));

            allowing(mockDocumentTemplate).getName();
            will(returnValue("XYZ123"));
        }});

        // when
        titleSubscriber.on(ev);
        
        // then
        Assertions.assertThat(ev.getTitle()).isEqualTo("SomeDomainObject XYZ123");
    }

    @Test
    public void title_already_set() throws Exception {
        // given
        final Applicability.TitleUiEvent ev = new Applicability.TitleUiEvent();
        final String someTitle = faker.strings().upper(20);
        ev.setTitle(someTitle);

        // when
        titleSubscriber.on(ev);

        // then
        assertThat(ev.getTitle()).isEqualTo(someTitle);
    }


}