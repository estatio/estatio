
package org.incode.module.document.dom.impl.types;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentTypeRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    DocumentTypeRepository documentTypeRepository;

    @Before
    public void setUp() throws Exception {
        documentTypeRepository.repositoryService = mockRepositoryService;
    }

    public static class Create extends DocumentTypeRepository_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }

    public static class FindByReference_Test extends DocumentTypeRepository_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }


}