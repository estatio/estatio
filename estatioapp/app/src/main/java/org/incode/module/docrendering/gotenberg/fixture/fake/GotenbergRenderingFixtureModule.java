package org.incode.module.docrendering.gotenberg.fixture.fake;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.incode.module.document.DocumentModule;

@XmlRootElement(name = "module")
public class GotenbergRenderingFixtureModule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new DocumentModule()
        );
    }

}
