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
package org.incode.module.documents.dom.impl.docs;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class Document_TitleSubscriber_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    FakeDataService faker = new FakeDataService() {{ init(); }};

    Document.TitleSubscriber titleSubscriber;

    @Mock
    Document mockDocument;

    @Before
    public void setUp() throws Exception {
        titleSubscriber = new Document.TitleSubscriber();
    }

    @Ignore
    @Test
    public void happy_case() throws Exception {

        // given
        final Document.TitleUiEvent ev = new Document.TitleUiEvent();
        ev.setSource(mockDocument);

        // expect
        context.checking(new Expectations() {{
        }});

        // when
        titleSubscriber.on(ev);
        
        // then
        Assertions.assertThat(ev.getTitle()).isEqualTo("****");
    }

    @Test
    public void title_already_set() throws Exception {
        // given
        final Document.TitleUiEvent ev = new Document.TitleUiEvent();
        ev.setSource(mockDocument);

        final String someTitle = faker.strings().upper(20);
        ev.setTitle(someTitle);

        // when
        titleSubscriber.on(ev);

        // then
        assertThat(ev.getTitle()).isEqualTo(someTitle);
    }

}