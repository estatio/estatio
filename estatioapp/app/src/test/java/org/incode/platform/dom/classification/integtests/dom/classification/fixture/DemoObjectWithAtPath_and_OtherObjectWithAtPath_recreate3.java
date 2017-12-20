package org.incode.platform.dom.classification.integtests.dom.classification.fixture;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPath;

import lombok.Getter;

public class DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3 extends
        FixtureScript {

    @Getter
    private List<DemoObjectWithAtPath> demoObjects = Lists.newArrayList();

    @Getter
    private List<OtherObjectWithAtPath> otherObjects = Lists.newArrayList();

    @Override
    protected void execute(final ExecutionContext executionContext) {
        // prereqs
        executionContext.executeChild(this,
                new DemoObjectWithAtPath_and_OtherObjectWithAtPath_tearDown());
        executionContext.executeChild(this,
                new DemoObjectWithAtPath_and_OtherObjectWithAtPath_create3());

    }


}
