package org.incode.platform.dom.classification.integtests.tests.category;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPathMenu;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryRepository_findByParent_IntegTest extends ClassificationModuleIntegTestAbstract {

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

    @Test
    public void happy_case() {
        // given
        Category large = categoryRepository.findByReference("LGE");

        // when
        List<Category> results = categoryRepository.findByParent(large);

        // then
        assertThat(results).hasSize(3);
        assertThat(results).allMatch(c -> c.getParent().equals(large));
        assertThat(results).extracting(Category::getReference)
                .containsOnly("L", "XL", "XXL");
    }

    @Test
    public void when_none() {
        // given
        Category frenchRed = categoryRepository.findByReference("FRRED");

        // when
        List<Category> results = categoryRepository.findByParent(frenchRed);

        // then
        assertThat(results).isEmpty();
    }

}