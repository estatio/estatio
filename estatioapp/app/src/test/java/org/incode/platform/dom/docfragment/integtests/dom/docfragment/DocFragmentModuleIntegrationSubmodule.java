package org.incode.platform.dom.docfragment.integtests.dom.docfragment;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.incode.module.docfragment.dom.DocFragmentModule;
import org.incode.platform.dom.docfragment.integtests.demo.DocFragmentModuleDemoDomSubmodule;

@XmlRootElement(name = "module")
public class DocFragmentModuleIntegrationSubmodule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new DocFragmentModule(),
                new DocFragmentModuleDemoDomSubmodule()
        );
    }


}
