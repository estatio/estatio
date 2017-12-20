package org.incode.platform.dom.docfragment.integtests;

import org.junit.BeforeClass;

import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.platform.dom.docfragment.integtests.app.DocFragmentAppAppManifest;
import org.incode.platform.dom.docfragment.integtests.demo.ExampleDomDemoDomSubmodule;

public abstract class DocFragmentModuleIntegTestAbstract extends IntegrationTestAbstract2 {

    @BeforeClass
    public static void initSystem() {
        bootstrapUsing(DocFragmentAppAppManifest.BUILDER
                .withAdditionalModules(ExampleDomDemoDomSubmodule.class)
                .withConfigurationProperty(FreeMarkerService.JODA_SUPPORT_KEY, "true")
                .withAdditionalServices(FakeDataService.class)
        );
    }

}
