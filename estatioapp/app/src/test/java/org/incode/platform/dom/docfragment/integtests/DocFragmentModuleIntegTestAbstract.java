package org.incode.platform.dom.docfragment.integtests;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.ModuleAbstract;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.platform.dom.docfragment.integtests.dom.docfragment.DocFragmentModuleIntegrationSubmodule;

import org.estatio.module.base.integtests.BaseModuleIntegTestAbstract;

public abstract class DocFragmentModuleIntegTestAbstract extends BaseModuleIntegTestAbstract {

    @XmlRootElement(name = "module")
    public static class MyModule extends DocFragmentModuleIntegrationSubmodule {
        @Override
        public Set<org.apache.isis.applib.Module> getDependencies() {
            final Set<org.apache.isis.applib.Module> dependencies = super.getDependencies();
            dependencies.addAll(Sets.newHashSet(
                    new FakeDataModule()
            ));
            return dependencies;
        }
    }

    public static ModuleAbstract module() {
        return new MyModule()
                .withConfigurationProperty(FreeMarkerService.JODA_SUPPORT_KEY, "true");
    }

    protected DocFragmentModuleIntegTestAbstract() {
        super(module());
    }

}
