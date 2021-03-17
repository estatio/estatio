package org.incode.platform.dom.classification.integtests.tests.applicability;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.ClassificationModuleIntegTestAbstract;
import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3;
import org.incode.platform.dom.classification.integtests.dom.classification.fixture.DemoObjectWithAtPath_and_OtherObjectWithAtPath_tearDown;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicabilityRepository_findByDomainTypeAndUnderAtPath_IntegTest extends
        ClassificationModuleIntegTestAbstract {

    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ApplicabilityRepository applicabilityRepository;

    @Inject
    ApplicationTenancyService applicationTenancyService;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_tearDown(), null);
        fixtureScripts.runFixtureScript(new DemoObjectWithAtPath_and_OtherObjectWithAtPath_recreate3(), null);
    }

    @Test
    public void exact_match_on_application_tenancy() {

        // /ITA matches /ITA and /
        assertThat(applicabilityRepository.findByDomainTypeAndUnderAtPath(DemoObjectWithAtPath.class, "/ITA").size()).isEqualTo(2);
    }

    @Test
    public void matches_on_sub_application_tenancy() {

        final List<Applicability> byDomainTypeAndUnderAtPath = applicabilityRepository.findByDomainTypeAndUnderAtPath(DemoObjectWithAtPath.class, "/ITA/XYZ");
        assertThat(byDomainTypeAndUnderAtPath.size()).isEqualTo(2);
        assertThat(byDomainTypeAndUnderAtPath).extracting(Applicability::getAtPath).containsOnly("/ITA", "/");
        // eg set up for "/ITA", search for app tenancy "/ITA/MIL"

    }

    @Test
    public void does_not_match_on_super_application_tenancy() {
        assertThat(applicabilityRepository.findByDomainTypeAndUnderAtPath(DemoObjectWithAtPath.class, "/").size()).isEqualTo(1);
    }

}