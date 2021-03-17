package org.estatio.module.coda.dom.doc;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.core.unittestsupport.jmocking.JMockActions;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHeadRepository_replace_Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    Sequence sequence;

    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    TitleService mockTitleService;

    CodaDocHeadRepository codaDocHeadRepository;


    CodaDocHead replacementDocHead;

    @Before
    public void setUp() throws Exception {
        codaDocHeadRepository = new CodaDocHeadRepository();
        codaDocHeadRepository.repositoryService = mockRepositoryService;
        codaDocHeadRepository.titleService = mockTitleService;

        sequence = context.sequence("sequence");

        replacementDocHead = new CodaDocHead("IT01", "FR-GEN", "123", (short)1, LocalDate.now(), LocalDate.now(), "2019/1", "books", "SHA256", "");

        context.checking(new Expectations() {{
            allowing(mockTitleService).titleOf(replacementDocHead);
            will(returnValue("Replacement DocHead Title"));
        }});

    }

    @Test
    public void when_does_not_exist() throws Exception {

        // given

        // expecting
        context.checking(new Expectations() {{
            allowing(mockRepositoryService).isPersistent(with(replacementDocHead));
            inSequence(sequence);
            will(returnValue(false));

            allowing(mockRepositoryService).uniqueMatch(with(any(QueryDefault.class)));
            inSequence(sequence);
            will(returnValue(null));

            oneOf(mockRepositoryService).persistAndFlush(with(replacementDocHead));
            inSequence(sequence);
            will(JMockActions.returnArgument(0));
        }});

        // when
        final CodaDocHead docHead = codaDocHeadRepository.persistAsReplacementIfRequired(replacementDocHead);

        // then
        assertThat(docHead).isSameAs(replacementDocHead);
    }


    @Test
    public void when_does_exist() throws Exception {

        // given
        final CodaDocHead originalDocHead = new CodaDocHead();

        // expecting
        context.checking(new Expectations() {{
            allowing(mockRepositoryService).isPersistent(with(replacementDocHead));
            inSequence(sequence);
            will(returnValue(false));

            allowing(mockRepositoryService).uniqueMatch(with(any(QueryDefault.class)));
            inSequence(sequence);
            will(returnValue(originalDocHead));

            oneOf(mockRepositoryService).removeAndFlush(with(originalDocHead));
            inSequence(sequence);

            oneOf(mockRepositoryService).persistAndFlush(with(replacementDocHead));
            inSequence(sequence);
            will(JMockActions.returnArgument(0));
        }});

        // when
        final CodaDocHead nextDocHead = codaDocHeadRepository.persistAsReplacementIfRequired(replacementDocHead);

        // then
        assertThat(nextDocHead).isNotSameAs(originalDocHead);
        assertThat(nextDocHead).isSameAs(replacementDocHead);
    }

    @Test
    public void when_already_persisted() throws Exception {

        // expecting
        context.checking(new Expectations() {{
            allowing(mockRepositoryService).isPersistent(with(replacementDocHead));
            will(returnValue(true));
        }});

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("CodaDocHead 'Replacement DocHead Title' is already persistent");

        // when
        codaDocHeadRepository.persistAsReplacementIfRequired(replacementDocHead);
    }


}
