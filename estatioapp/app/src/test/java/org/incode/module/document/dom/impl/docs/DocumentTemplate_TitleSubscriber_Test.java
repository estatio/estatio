package org.incode.module.document.dom.impl.docs;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.document.dom.impl.docs.Document;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DocumentTemplate_TitleSubscriber_Test {

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