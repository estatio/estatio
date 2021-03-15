package org.incode.module.docrendering.gotenberg.dom;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.incode.module.document.DocumentModule;

@XmlRootElement(name = "module")
public class GotenbergRenderingDomModule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new DocumentModule()
        );
    }

}
