package org.incode.platform.dom.document.integtests.tests.mixins;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.wrapper.HiddenException;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.platform.dom.document.integtests.DocumentModuleIntegTestAbstract;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;
import org.incode.platform.dom.document.integtests.demo.dom.other.OtherObject;
import org.incode.platform.dom.document.integtests.demo.fixture.setup.DemoObjectWithUrl_createUpTo5_fakeData;
import org.incode.platform.dom.document.integtests.demo.fixture.setup.OtherObject_createUpTo5_fakeData;
import org.incode.platform.dom.document.integtests.dom.document.dom.paperclips.demowithurl.PaperclipForDemoObjectWithUrl;
import org.incode.platform.dom.document.integtests.dom.document.fixture.DemoModule_and_DocTypesAndTemplates_tearDown;
import org.incode.platform.dom.document.integtests.dom.document.fixture.seed.DocumentTypeAndTemplatesApplicableForDemoObjectFixture;

import static org.assertj.core.api.Assertions.assertThat;

public class T_createAndAttachDocumentAndRender_IntegTest extends DocumentModuleIntegTestAbstract {

    DemoObjectWithUrl demoObject;
    OtherObject otherObject;

    DocumentTypeAndTemplatesApplicableForDemoObjectFixture templateFixture;

    @Before
    public void setUpData() throws Exception {

        fixtureScripts.runFixtureScript(new DemoModule_and_DocTypesAndTemplates_tearDown(), null);

        // types + templates
        templateFixture = new DocumentTypeAndTemplatesApplicableForDemoObjectFixture();
        fixtureScripts.runFixtureScript(templateFixture, null);

        // demo objects
        final DemoObjectWithUrl_createUpTo5_fakeData demoObjectWithUrlFixture = new DemoObjectWithUrl_createUpTo5_fakeData();
        fixtureScripts.runFixtureScript(demoObjectWithUrlFixture, null);
        demoObject = demoObjectWithUrlFixture.getDemoObjects().get(0);

        // other objects
        final OtherObject_createUpTo5_fakeData otherObjectsFixture = new OtherObject_createUpTo5_fakeData();
        fixtureScripts.runFixtureScript(otherObjectsFixture, null);
        otherObject = otherObjectsFixture.getOtherObjects().get(0);

        transactionService.flushTransaction();
    }

    public static class ActionImplementation_IntegTest extends T_createAndAttachDocumentAndRender_IntegTest {

        /**
         * TODO: ignored because seem to be hitting java.lang.NoClassDefFoundError: org/apache/poi/xwpf/usermodel/IRunBody
         at org.incode.module.document.integtests.mixins.T_createAndAttachDocumentAndRender_IntegTest$ActionImplementation_IntegTest.can_create_document(T_createAndAttachDocumentAndRender_IntegTest.java:98)
         Caused by: java.lang.ClassNotFoundException: org.apache.poi.xwpf.usermodel.IRunBody
         at org.incode.module.document.integtests.mixins.T_createAndAttachDocumentAndRender_IntegTest$ActionImplementation_IntegTest.can_create_document(T_createAndAttachDocumentAndRender_IntegTest.java:98)

         *
         */
        @Ignore //
        @Test
        public void can_create_document() throws Exception {

            // given
            assertThat(wrap(_documents(demoObject)).$$()).isEmpty();

            // when
            final List<DocumentTemplate> templates = _createAndAttachDocumentAndRender(demoObject).choices0Act();

            transactionService.nextTransaction();

            // then
            assertThat(templates).hasSize(4);

            // when
            Set<Document> documents = Sets.newHashSet();
            for (DocumentTemplate template : templates) {

                final Object documentAsObj = _createAndAttachDocumentAndRender(demoObject).act(template);

                // then
                assertThat(documentAsObj).isInstanceOf(Document.class);
                Document document = (Document) documentAsObj;
                documents.add(document);

            }


            // for each
            for (Document document : documents) {

                // when
                final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);

                // then

                // (depends on the binder of the template as to how many associated)
                if (document.getType().getReference().equals("XDOCREPORT-DOC")) {
                    assertThat(paperclips).hasSize(2);

                    for (Paperclip paperclip : paperclips) {
                        final DocumentAbstract paperclipDocument = paperclip.getDocument();
                        assertThat(paperclipDocument).isSameAs(document);
                    }

                    final Object paperclipAttachedTo = paperclips.get(0).getAttachedTo();
                    assertThat(paperclipAttachedTo).isSameAs(demoObject);

                    final Object paperclipAttachedTo2 = paperclips.get(1).getAttachedTo();
                    assertThat(paperclipAttachedTo2).isSameAs(otherObject);
                }
                else {
                    assertThat(paperclips).hasSize(1);

                    final DocumentAbstract paperclipDocument = paperclips.get(0).getDocument();
                    assertThat(paperclipDocument).isSameAs(document);

                    final Object paperclipAttachedTo = paperclips.get(0).getAttachedTo();
                    assertThat(paperclipAttachedTo).isSameAs(demoObject);
                }
            }


            // then
            final PaperclipForDemoObjectWithUrl._documents wrappedMixin = wrap(_documents(demoObject));
            final List<Paperclip> clips = wrappedMixin.$$();
            assertThat(clips).hasSize(4);

            // when
            final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(demoObject);
            assertThat(paperclips).hasSize(4);

            final Set<Document> attachedDocuments = paperclips.stream().map(x -> (Document) x.getDocument()).collect(Collectors.toSet());

            // then
            assertContainSame(documents, attachedDocuments);
        }

        private void assertContainSame(final Set<Document> docSet1, final Set<Document> docSet2) {
            assertThat(docSet1).contains(docSet2.toArray(new Document[] {}));
            assertThat(docSet2).contains(docSet1.toArray(new Document[]{}));
        }

        @Inject
        QueryResultsCache queryResultsCache;
    }

    public static class Hidden_IntegTest extends T_createAndAttachDocumentAndRender_IntegTest {

        @Test
        public void if_no_applicable_templates() throws Exception {

            // when
            final List<DocumentTemplate> templates = _createAndAttachDocumentAndRender(otherObject).choices0Act();

            // then
            assertThat(templates).isEmpty();

            // expect
            expectedExceptions.expect(HiddenException.class);

            // when
            final DocumentTemplate anyTemplate = templateFixture.getFmkTemplate();
            wrap(_createAndAttachDocumentAndRender(otherObject)).act(anyTemplate);
        }


    }


    @Inject
    PaperclipRepository paperclipRepository;

}