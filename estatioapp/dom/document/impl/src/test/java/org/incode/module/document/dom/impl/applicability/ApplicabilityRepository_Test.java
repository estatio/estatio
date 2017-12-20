package org.incode.module.document.dom.impl.applicability;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;

public class ApplicabilityRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DocumentTemplate mockDocumentTemplate;

    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    ApplicabilityRepository applicabilityRepository;

    @Before
    public void setUp() throws Exception {
        applicabilityRepository.repositoryService = mockRepositoryService;
    }

    public static class Create_Test extends ApplicabilityRepository_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }

    public static class Delete_Test extends ApplicabilityRepository_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }


}