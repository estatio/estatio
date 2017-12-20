package org.incode.platform.dom.alias.integtests.tests.demo;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.incode.platform.dom.alias.integtests.AliasModuleIntegTestAbstract;
import org.incode.platform.dom.alias.integtests.demo.dom.demo.DemoObject;
import org.incode.platform.dom.alias.integtests.demo.dom.demo.DemoObjectMenu;
import org.incode.platform.dom.alias.integtests.demo.fixture.data.DemoObjectData;
import org.incode.platform.dom.alias.integtests.dom.alias.fixture.DemoObject_withAliases_recreate2;

public class DemoObjectMenu_IntegTest extends AliasModuleIntegTestAbstract {

    @Inject
    DemoObjectMenu demoObjectMenu;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObject_withAliases_recreate2(), null);
    }

    @Test
    public void listAll() throws Exception {

        final List<DemoObject> all = wrap(demoObjectMenu).listAllDemoObjects();
        Assertions.assertThat(all.size()).isEqualTo(DemoObjectData.values().length);
        
        DemoObject demoObject = wrap(all.get(0));
        Assertions.assertThat(demoObject.getName()).isEqualTo("Foo");
    }
    
    @Test
    public void create() throws Exception {

        wrap(demoObjectMenu).createDemoObject("Faz");
        
        final List<DemoObject> all = wrap(demoObjectMenu).listAllDemoObjects();
        Assertions.assertThat(all.size()).isEqualTo(DemoObjectData.values().length+1);
    }

}