package org.incode.module.communications.integtests;

import org.junit.BeforeClass;

import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.incode.module.communications.CommunicationsModuleDomManifest;

public abstract class CommunicationsModuleIntegTestAbstract extends IntegrationTestAbstract2 {

    @BeforeClass
    public static void initSystem() {
        bootstrapUsing(CommunicationsModuleDomManifest.BUILDER
                            //.withAdditionalServices(ModuleFixtureScriptsSpecificationProvider.class)
                );
    }

}
