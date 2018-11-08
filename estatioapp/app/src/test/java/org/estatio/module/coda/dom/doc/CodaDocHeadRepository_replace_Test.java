package org.estatio.module.coda.dom.doc;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JMockActions;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHeadRepository_replace_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;

    CodaDocHeadRepository codaDocHeadRepository;
    @Before
    public void setUp() throws Exception {
        codaDocHeadRepository = new CodaDocHeadRepository();
        codaDocHeadRepository.repositoryService = mockRepositoryService;
    }

    @Test
    public void when_does_not_exist() throws Exception {

        // expecting
        context.checking(new Expectations() {{
            allowing(mockRepositoryService).uniqueMatch(with(any(QueryDefault.class)));
            will(returnValue(null));

            oneOf(mockRepositoryService).persist(with(any(CodaDocHead.class)));
            will(JMockActions.returnArgument(0));
        }});

        // when
        final CodaDocHead docHead =
                codaDocHeadRepository.replace("IT01", "FR-GEN", "123", LocalDate.now(), LocalDate.now());
    }

    @Test
    public void when_does_exist() throws Exception {

        // given
        final CodaDocHead originalDocHead =new CodaDocHead();

        // expecting
        context.checking(new Expectations() {{
            allowing(mockRepositoryService).uniqueMatch(with(any(QueryDefault.class)));
            will(returnValue(originalDocHead));

            oneOf(mockRepositoryService).removeAndFlush(with(originalDocHead));

            oneOf(mockRepositoryService).persist(with(any(CodaDocHead.class)));
            will(JMockActions.returnArgument(0));
        }});

        // when
        final CodaDocHead nextDocHead =
                codaDocHeadRepository.replace("IT01", "FR-GEN", "123", LocalDate.now(), LocalDate.now());

        // then
        assertThat(nextDocHead).isNotSameAs(originalDocHead);
    }
}