package org.incode.platform.dom.classification.integtests.tests.classification;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

public class Classification_UiSubscriber_IntegTest extends ClassificationModuleIntegTestAbstract {

    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ApplicabilityRepository applicabilityRepository;

    @Inject
    DemoObjectWithAtPathMenu demoObjectMenu;
    @Inject
    ApplicationTenancyService applicationTenancyService;

    @Before
    public void setUpData() throws Exception {
         fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
    }


    @Ignore
    public void override_title_subscriber() {

        // given a service subscribing on Classification.TitleUiEvent

        // then the title of an classification should be...

    }

    @Ignore
    public void override_icon_subscriber() {

        // given a service subscribing on Classification.IconUiEvent

        // then the icon of an classification should be...

    }

    @Ignore
    public void override_cssClass_subscriber() {

        // given a service subscribing on Classification.CssClassUiEvent

        // then the CSS class of an classification should be...

    }


}