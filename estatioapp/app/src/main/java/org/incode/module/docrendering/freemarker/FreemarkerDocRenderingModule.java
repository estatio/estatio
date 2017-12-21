package org.incode.module.docrendering.freemarker;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.isisaddons.module.freemarker.IncodeLibFreeMarkerModule;

import org.incode.module.document.DocumentModule;

@XmlRootElement(name = "module")
public class FreemarkerDocRenderingModule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new DocumentModule(),
                new IncodeLibFreeMarkerModule()
        );
    }

}
