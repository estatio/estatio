package org.estatio.module.base.integtests;

import java.util.Arrays;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import lombok.Getter;

// TODO: it ought to be possible write instead :
//   module.withAdditionalDependency(new GotenbergRenderingFakeModule()) etc, but
//  there's a bug in the framework that prevents this
//  as a workaround, register services directly.
@XmlRootElement(name = "module")
public class ModuleForTesting extends ModuleAbstract {

    @Getter
    private final Set<Module> dependencies;

    public ModuleForTesting(final Module... dependencies) {
        this.dependencies = Sets.newLinkedHashSet(Arrays.asList(dependencies));
    }

}
