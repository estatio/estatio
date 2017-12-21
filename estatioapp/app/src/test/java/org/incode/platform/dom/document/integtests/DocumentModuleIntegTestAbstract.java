package org.incode.platform.dom.document.integtests;

import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract3;

import org.isisaddons.module.command.CommandModule;
import org.isisaddons.module.fakedata.FakeDataModule;

import org.incode.module.docrendering.freemarker.dom.FreemarkerDocRenderingModule;
import org.incode.module.docrendering.stringinterpolator.dom.StringInterpolatorDocRenderingModule;
import org.incode.module.docrendering.xdocreport.dom.XDocReportDocRenderingModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.Document_delete;
import org.incode.platform.dom.document.integtests.app.DocumentAppModule;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;
import org.incode.platform.dom.document.integtests.demo.dom.other.OtherObject;
import org.incode.platform.dom.document.integtests.dom.document.DocumentModuleIntegrationSubmodule;
import org.incode.platform.dom.document.integtests.dom.document.dom.paperclips.demowithurl.PaperclipForDemoObjectWithUrl;
import org.incode.platform.dom.document.integtests.dom.document.dom.paperclips.other.PaperclipForOtherObject;

public abstract class DocumentModuleIntegTestAbstract extends IntegrationTestAbstract3 {

    public static ModuleAbstract module() {
        return new DocumentModuleIntegrationSubmodule() {
                    @Override
                    public Set<Module> getDependencies() {
                        return Sets.newHashSet(
                                new FreemarkerDocRenderingModule(),
                                new StringInterpolatorDocRenderingModule(),
                                new XDocReportDocRenderingModule()
                        );
                    }
                }
                .withAdditionalModules(
                        DocumentAppModule.class,
                        CommandModule.class,
                        DocumentModuleIntegTestAbstract.class,
                        FakeDataModule.class);
    }

    protected DocumentModuleIntegTestAbstract() {
        super(module());
    }

    protected Document_delete _delete(final Document document) {
        return mixin(Document_delete.class, document);
    }

    protected PaperclipForDemoObjectWithUrl._preview _preview(final DemoObjectWithUrl domainObject) {
        return mixin(PaperclipForDemoObjectWithUrl._preview.class, domainObject);
    }

    protected PaperclipForDemoObjectWithUrl._preview _preview(final OtherObject domainObject) {
        return mixin(PaperclipForDemoObjectWithUrl._preview.class, domainObject);
    }

    protected PaperclipForDemoObjectWithUrl._createAndAttachDocumentAndRender _createAndAttachDocumentAndRender(final DemoObjectWithUrl demoObject) {
        return mixin(PaperclipForDemoObjectWithUrl._createAndAttachDocumentAndRender.class, demoObject);
    }

    protected PaperclipForOtherObject._createAndAttachDocumentAndRender _createAndAttachDocumentAndRender(final OtherObject otherObject) {
        return mixin(PaperclipForOtherObject._createAndAttachDocumentAndRender.class, otherObject);
    }

    protected PaperclipForDemoObjectWithUrl._createAndAttachDocumentAndScheduleRender _createAndAttachDocumentAndScheduleRender(final DemoObjectWithUrl domainObject) {
        return mixin(PaperclipForDemoObjectWithUrl._createAndAttachDocumentAndScheduleRender.class, domainObject);
    }

    protected PaperclipForOtherObject._createAndAttachDocumentAndScheduleRender _createAndAttachDocumentAndScheduleRender(final OtherObject domainObject) {
        return mixin(PaperclipForOtherObject._createAndAttachDocumentAndScheduleRender.class, domainObject);
    }

    protected PaperclipForDemoObjectWithUrl._documents _documents(final DemoObjectWithUrl domainObject) {
        return mixin(PaperclipForDemoObjectWithUrl._documents.class, domainObject);
    }

    protected PaperclipForOtherObject._documents _documents(final OtherObject domainObject) {
        return mixin(PaperclipForOtherObject._documents.class, domainObject);
    }



}
