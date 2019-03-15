package org.incode.platform.dom.communications.integtests;

import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.ModuleAbstract;

import org.isisaddons.module.command.dom.CommandDomModule;
import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.FreeMarkerModule;
import org.isisaddons.module.pdfbox.dom.PdfBoxModule;

import org.incode.platform.dom.communications.integtests.app.services.FakeCommsServiceModule;
import org.incode.platform.dom.communications.integtests.app.services.fakesched.FakeScheduler;
import org.incode.platform.dom.communications.integtests.dom.communications.CommunicationsModuleIntegrationSubmodule;
import org.incode.platform.dom.communications.integtests.dom.document.ClassificationsModuleDocumentDomModule;

import org.estatio.module.base.integtests.BaseModuleIntegTestAbstract;

public abstract class CommunicationsModuleIntegTestAbstract extends BaseModuleIntegTestAbstract {

    @XmlRootElement(name = "module")
    public static class MyModule extends CommunicationsModuleIntegrationSubmodule {
        @Override
        public Set<org.apache.isis.applib.Module> getDependencies() {
            final Set<org.apache.isis.applib.Module> dependencies = super.getDependencies();
            dependencies.addAll(Sets.newHashSet(
                    new FakeCommsServiceModule(),
                    new ClassificationsModuleDocumentDomModule(),
                    new PdfBoxModule(),
                    new CommandDomModule(),
                    new FreeMarkerModule(),
                    new FakeDataModule()
            ));
            return dependencies;
        }
    }

    public static ModuleAbstract module() {
        return new MyModule();
    }

    protected CommunicationsModuleIntegTestAbstract() {
        super(module());
    }

    @Inject
    protected FakeScheduler fakeScheduler;

}
