package org.incode.module.document.dom.impl.paperclips;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class PaperclipRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    PaperclipRepository paperclipRepository;

    @Before
    public void setUp() throws Exception {
        paperclipRepository.repositoryService = mockRepositoryService;
    }

    public static class FindByDocument_Test extends PaperclipRepository_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }

    public static class FindByAttachedTo_Test extends PaperclipRepository_Test {

        @Ignore
        @Test
        public void if_attachedTo_is_null() throws Exception {

        }

        @Ignore
        @Test
        public void if_bookmark_is_null() throws Exception {

        }

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }

    public static class FindByAttachedToAndRoleName_Test extends PaperclipRepository_Test {

        @Ignore
        @Test
        public void if_attachedTo_is_null() throws Exception {

        }

        @Ignore
        @Test
        public void if_roleName_is_null() throws Exception {

        }

        @Ignore
        @Test
        public void if_bookmark_is_null() throws Exception {

        }

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }

    public static class Attach_Test extends PaperclipRepository_Test {


        @Ignore
        @Test
        public void when_no_subclass_of_Paperclip_for_object_to_attach_to() throws Exception {

        }

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }


}