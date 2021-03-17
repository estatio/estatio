package org.incode.module.docfragment.dom.impl;

import java.util.List;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.assertj.core.api.Assertions.assertThat;

public class DocFragmentRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    ServiceRegistry2 mockServiceRegistry;
    
    @Mock
    RepositoryService mockRepositoryService;

    DocFragmentRepository docfragmentRepository;

    @Before
    public void setUp() throws Exception {
        docfragmentRepository = new DocFragmentRepository();
        docfragmentRepository.repositoryService = mockRepositoryService;
        docfragmentRepository.serviceRegistry = mockServiceRegistry;
    }

    public static class Create extends DocFragmentRepository_Test {

        @Test
        public void happyCase() throws Exception {

            // given
            final Sequence seq = context.sequence("create");
            context.checking(new Expectations() {
                {
                    oneOf(mockServiceRegistry).injectServicesInto(with(any(DocFragment.class)));
                    inSequence(seq);

                    oneOf(mockRepositoryService).persist(with(any(DocFragment.class)));
                    inSequence(seq);
                }

            });

            // when
            final DocFragment obj = docfragmentRepository.create("invoice.Invoice", "due", "/ITA", "The invoice should be paid by ${dueDate}");

            // then
            assertThat(obj).isNotNull();
            assertThat(obj.getObjectType()).isEqualTo("invoice.Invoice");
            assertThat(obj.getName()).isEqualTo("due");
            assertThat(obj.getAtPath()).isEqualTo("/ITA");
            assertThat(obj.getTemplateText()).isEqualTo("The invoice should be paid by ${dueDate}");
        }
    }

    public static class ListAll extends DocFragmentRepository_Test {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<DocFragment> all = Lists.newArrayList();

            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).allInstances(DocFragment.class);
                    will(returnValue(all));
                }
            });

            // when
            final List<DocFragment> list = docfragmentRepository.listAll();

            // then
            assertThat(list).isEqualTo(all);
        }
    }
}
