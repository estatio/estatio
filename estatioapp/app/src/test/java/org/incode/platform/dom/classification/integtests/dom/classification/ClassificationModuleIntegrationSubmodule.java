package org.incode.platform.dom.classification.integtests.dom.classification;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.platform.dom.classification.integtests.demo.ClassificationModuleDemoDomSubmodule;
import org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.demowithatpath.ClassificationForDemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.otherwithatpath.ClassificationForOtherObjectWithAtPath;

@XmlRootElement(name = "module")
public class ClassificationModuleIntegrationSubmodule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new ClassificationModuleDemoDomSubmodule(),
                new ClassificationModule()
        );
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {

            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                deleteFrom(ClassificationForOtherObjectWithAtPath.class);
                deleteFrom(ClassificationForDemoObjectWithAtPath.class);
            }
        };
    }
}
