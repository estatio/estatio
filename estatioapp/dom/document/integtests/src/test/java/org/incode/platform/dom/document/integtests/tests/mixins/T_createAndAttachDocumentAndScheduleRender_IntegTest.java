package org.incode.platform.dom.document.integtests.tests.mixins;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.background.BackgroundCommandService;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.platform.dom.document.integtests.DocumentModuleIntegTestAbstract;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;
import org.incode.platform.dom.document.integtests.demo.fixture.setup.DemoObjectWithUrl_createUpTo5_fakeData;
import org.incode.platform.dom.document.integtests.dom.document.fixture.DemoModule_and_DocTypesAndTemplates_tearDown;
import org.incode.platform.dom.document.integtests.dom.document.fixture.seed.DocumentTypeAndTemplatesApplicableForDemoObjectFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assume.assumeThat;

public class T_createAndAttachDocumentAndScheduleRender_IntegTest extends DocumentModuleIntegTestAbstract {

    DemoObjectWithUrl demoObject;


    DocumentTypeAndTemplatesApplicableForDemoObjectFixture templateFs;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoModule_and_DocTypesAndTemplates_tearDown(), null);

        // types + templates
        templateFs = new DocumentTypeAndTemplatesApplicableForDemoObjectFixture();
        fixtureScripts.runFixtureScript(templateFs, null);

        // demo objects
        final DemoObjectWithUrl_createUpTo5_fakeData demoObjectWithUrlFixture = new DemoObjectWithUrl_createUpTo5_fakeData();
        fixtureScripts.runFixtureScript(demoObjectWithUrlFixture, null);
        demoObject = demoObjectWithUrlFixture.getDemoObjects().get(0);

        transactionService.flushTransaction();
    }


    @Inject
    BackgroundCommandService backgroundCommandService;

    public static class Disabled_IntegTest extends T_createAndAttachDocumentAndScheduleRender_IntegTest {

        @Test
        public void if_no_background_service() throws Exception {

            // given
            assumeThat(backgroundCommandService, is(nullValue()));

            // when
            final TranslatableString reason = _createAndAttachDocumentAndScheduleRender(demoObject).disable$$();

            // then
            assertThat(reason).isNotNull();

            // expect
            expectedExceptions.expect(DisabledException.class);

            // when
            final DocumentTemplate anyTemplate = templateFs.getFmkTemplate();
            wrap(_createAndAttachDocumentAndScheduleRender(demoObject)).$$(anyTemplate);
        }
    }


}