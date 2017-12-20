package org.incode.platform.dom.alias.integtests;

import javax.inject.Inject;

import org.junit.BeforeClass;

import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.platform.dom.alias.integtests.app.AliasModuleAppManifest;
import org.incode.platform.dom.alias.integtests.demo.ExampleDomDemoDomSubmodule;
import org.incode.platform.dom.alias.integtests.dom.alias.dom.AliasForDemoObject;

public abstract class AliasModuleIntegTestAbstract extends IntegrationTestAbstract2 {

    @BeforeClass
    public static void initClass() {
        bootstrapUsing(
                AliasModuleAppManifest.BUILDER.
                        withAdditionalModules(
                                ExampleDomDemoDomSubmodule.class,
                                AliasModuleIntegTestAbstract.class,
                                FakeDataModule.class
                        )
                        .build());
    }

    @Inject
    protected FakeDataService fakeData;

    protected AliasForDemoObject._addAlias mixinAddAlias(final Object aliased) {
        return mixin(AliasForDemoObject._addAlias.class, aliased);
    }
    protected AliasForDemoObject._removeAlias mixinRemoveAlias(final Object aliased) {
        return mixin(AliasForDemoObject._removeAlias.class, aliased);
    }

    protected AliasForDemoObject._aliases mixinAliases(final Object aliased) {
        return mixin(AliasForDemoObject._aliases.class, aliased);
    }


}
