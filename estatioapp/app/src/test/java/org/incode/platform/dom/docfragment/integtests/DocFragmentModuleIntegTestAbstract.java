package org.incode.platform.dom.docfragment.integtests;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract3;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.platform.dom.docfragment.integtests.dom.docfragment.DocFragmentModuleIntegrationSubmodule;

public abstract class DocFragmentModuleIntegTestAbstract extends IntegrationTestAbstract3 {

    public static ModuleAbstract module() {
        return new DocFragmentModuleIntegrationSubmodule()
                    .withAdditionalModules(FakeDataModule.class)
                    .withConfigurationProperty(FreeMarkerService.JODA_SUPPORT_KEY, "true");
    }

    protected DocFragmentModuleIntegTestAbstract() {
        super(module());
    }

}
