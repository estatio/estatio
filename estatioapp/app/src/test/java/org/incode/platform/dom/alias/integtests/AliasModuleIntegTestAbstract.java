package org.incode.platform.dom.alias.integtests;

import javax.inject.Inject;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract3;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.platform.dom.alias.integtests.dom.alias.AliasModuleIntegrationSubmodule;
import org.incode.platform.dom.alias.integtests.dom.alias.dom.AliasForDemoObject;
import org.incode.platform.dom.alias.integtests.tests.alias.T_addAlias_IntegTest;

public abstract class AliasModuleIntegTestAbstract extends IntegrationTestAbstract3 {

    public static ModuleAbstract module() {
        return new AliasModuleIntegrationSubmodule()
                    .withAdditionalServices(T_addAlias_IntegTest.DomainEventIntegTest.Subscriber.class)
                    .withAdditionalModules(FakeDataModule.class);
    }

    protected AliasModuleIntegTestAbstract() {
        super(module());
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
