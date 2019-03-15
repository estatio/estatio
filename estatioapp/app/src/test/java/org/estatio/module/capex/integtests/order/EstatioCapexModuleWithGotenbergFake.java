package org.estatio.module.capex.integtests.order;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.incode.module.docrendering.gotenberg.fixture.fake.GotenbergRenderingFakeModule;

import org.estatio.module.capex.EstatioCapexModule;

@XmlRootElement(name = "module")
public class EstatioCapexModuleWithGotenbergFake extends ModuleAbstract {
    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new EstatioCapexModule(),
                new GotenbergRenderingFakeModule());
    }
}
