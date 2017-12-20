package org.incode.module.document.dom.impl.applicability;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.i18n.TranslationService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.types.DocumentType;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class Applicability_TitleSubscriber_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    FakeDataService faker = new FakeDataService() {{ init(); }};

    Applicability.TitleSubscriber titleSubscriber;

    @Mock
    DocumentTemplate mockDocumentTemplate;

    @Mock
    DocumentType mockDocumentType;

    @Mock
    Applicability mockApplicability;

    @Mock
    private TranslationService mockTranslationService;

    @Before
    public void setUp() throws Exception {
        titleSubscriber = new Applicability.TitleSubscriber();
    }

    @Ignore
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

            allowing(mockDocumentTemplate).getType();
            will(returnValue(mockDocumentType));

            allowing(mockDocumentType).getReference();
            will(returnValue("XYZ123"));
        }});

        // when
        titleSubscriber.on(ev);
        
        // then
        final TranslatableString translatableTitle = ev.getTranslatableTitle();
        Assertions.assertThat(translatableTitle.translate(mockTranslationService, "")).isEqualTo("SomeDomainObject XYZ123");
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