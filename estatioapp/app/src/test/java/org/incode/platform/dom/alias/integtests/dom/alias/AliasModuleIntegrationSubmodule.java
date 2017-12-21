package org.incode.platform.dom.alias.integtests.dom.alias;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.alias.dom.AliasModule;
import org.incode.platform.dom.alias.integtests.demo.AliasModuleDemoDomSubmodule;
import org.incode.platform.dom.alias.integtests.dom.alias.dom.AliasForDemoObject;

@XmlRootElement(name = "module")
public class AliasModuleIntegrationSubmodule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new AliasModuleDemoDomSubmodule(),
                new AliasModule()
        );
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                deleteFrom(AliasForDemoObject.class);
            }
        };
    }
}
