package org.incode.platform.dom.classification.integtests.tests.category;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;

import org.apache.isis.applib.services.title.TitleService;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

import static org.assertj.core.api.Assertions.assertThat;

public class Category_UiSubscriber_IntegTest extends ClassificationModuleIntegTestAbstract {

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

    @Inject
    TitleService titleService;

    Category category;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
        category = categoryRepository.createChild(null, "Jeroen", "KOEKENBAKKER", 1);
    }

    @Ignore
    public void override_title_subscriber() {
        assertThat(titleService.titleOf(category)).isEqualTo("Holtkamp");
    }

    @Ignore
    public void override_icon_subscriber() {
        assertThat(titleService.iconNameOf(category)).isEqualTo("Jodekoek.png");
    }

}