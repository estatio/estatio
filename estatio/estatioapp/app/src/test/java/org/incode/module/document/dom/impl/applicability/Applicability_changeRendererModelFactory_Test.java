package org.incode.module.document.dom.impl.applicability;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.services.ClassService;
import org.incode.module.document.dom.spi.RendererModelFactoryClassNameService;

public class Applicability_changeRendererModelFactory_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private RendererModelFactoryClassNameService mockClassNameService;

    @Mock
    private ClassService mockClassService;

    @Mock
    private DocumentTemplate mockDocumentTemplate;

    @Mock
    Applicability mockApplicability;

    Applicability_changeRendererModelFactory mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new Applicability_changeRendererModelFactory(mockApplicability);
        mixin.classNameService = mockClassNameService;
        mixin.classService = mockClassService;
    }

    public static class DisabledTest extends Applicability_changeRendererModelFactory_Test {

        @Ignore
        @Test
        public void disabled_if_no_RendererModelFactoryClassNameService_available() throws Exception {

        }

        @Ignore
        @Test
        public void enabled_if_RendererModelFactoryClassNameService_is_available() throws Exception {

        }
    }

    public static class Choices_Test extends Applicability_changeRendererModelFactory_Test {

        @Ignore
        @Test
        public void delegates_off_to_RendererModelFactoryClassNameService() throws Exception {

        }
    }

    public static class Default_Test extends Applicability_changeRendererModelFactory_Test {

        @Ignore
        @Test
        public void creates_view_model_from_ClassService() throws Exception {

        }
    }

    public static class ActionInvocation_Test extends Applicability_changeRendererModelFactory_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }


}