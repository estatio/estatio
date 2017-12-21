package org.incode.platform.dom.communications.integtests;

import javax.inject.Inject;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract3;

import org.isisaddons.module.command.CommandModule;
import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.FreeMarkerModule;
import org.isisaddons.module.pdfbox.dom.PdfBoxModule;

import org.incode.platform.dom.communications.integtests.app.services.DemoAppApplicationModuleServicesSubmodule;
import org.incode.platform.dom.communications.integtests.app.services.fakesched.FakeScheduler;
import org.incode.platform.dom.communications.integtests.dom.communications.CommunicationsModuleIntegrationSubmodule;
import org.incode.platform.dom.communications.integtests.dom.document.ClassificationsModuleDocumentDomModule;

public abstract class CommunicationsModuleIntegTestAbstract extends IntegrationTestAbstract3 {

    public static ModuleAbstract module() {
        return new CommunicationsModuleIntegrationSubmodule()
                    .withAdditionalModules(
                            DemoAppApplicationModuleServicesSubmodule.class,
                            ClassificationsModuleDocumentDomModule.class,
                            PdfBoxModule.class,
                            CommandModule.class,
                            FreeMarkerModule.class,
                            FakeDataModule.class);
    }

    protected CommunicationsModuleIntegTestAbstract() {
        super(module());
    }

    @Inject
    protected FakeScheduler fakeScheduler;

}
