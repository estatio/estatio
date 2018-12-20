package org.incode.platform.dom.alias.integtests;

import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.ModuleAbstract;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.platform.dom.alias.integtests.dom.alias.AliasModuleIntegrationSubmodule;
import org.incode.platform.dom.alias.integtests.dom.alias.dom.AliasForDemoObject;

import org.estatio.module.base.integtests.BaseModuleIntegTestAbstract;

public abstract class AliasModuleIntegTestAbstract extends BaseModuleIntegTestAbstract {

    @XmlRootElement(name = "module")
    public static class MyModule extends AliasModuleIntegrationSubmodule {
        @Override
        public Set<org.apache.isis.applib.Module> getDependencies() {
            final Set<org.apache.isis.applib.Module> dependencies = super.getDependencies();
            dependencies.addAll(Sets.newHashSet(
                    new FakeDataModule()
            ));
            return dependencies;
            // TODO: reinstate if we ever bring in alias.  For now, having to comment out this subscriber because it is causing the 'isis.reflector.validator.checkModuleExtent' check to fail.
            // .withAdditionalServices(T_addAlias_IntegTest.DomainEventIntegTest.Subscriber.class)
        }
    }

    public static ModuleAbstract module() {
        return new MyModule();
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
