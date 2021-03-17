package org.incode.platform.dom.classification.integtests.tests.demo;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

public class DemoObjectMenu_IntegTest extends ClassificationModuleIntegTestAbstract {

    DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3 fs;

    @Before
    public void setUpData() throws Exception {
        fs = new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3();
        fixtureScripts.runFixtureScript(fs, null);
    }

    @Ignore // TODO: to fix...
    @Test
    public void listAll() throws Exception {

        // given
        int numDemoObjects = fs.getDemoObjects().size();

        // when
        final List<DemoObjectWithAtPath> all = wrap(demoObjectMenu).listAllDemoObjectsWithAtPath();

        // then
        Assertions.assertThat(all.size()).isEqualTo(numDemoObjects);
    }
    
    @Test
    public void create() throws Exception {

        // given
        final List<DemoObjectWithAtPath> before = wrap(demoObjectMenu).listAllDemoObjectsWithAtPath();
        int numBefore = before.size();

        // when
        wrap(demoObjectMenu).createDemoObjectWithAtPath("Faz", "/");

        // then
        final List<DemoObjectWithAtPath> after = wrap(demoObjectMenu).listAllDemoObjectsWithAtPath();
        Assertions.assertThat(after.size()).isEqualTo(numBefore+1);
    }

    @Inject
    DemoObjectWithAtPathMenu demoObjectMenu;


}