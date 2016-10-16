package org.estatio.dom.country;

import java.util.List;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.Country;

import static org.assertj.core.api.Assertions.assertThat;

public class EstatioApplicationTenancyRepositoryForCountryTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ApplicationTenancyRepository mockApplicationTenancies;

    private ApplicationTenancy global;
    private ApplicationTenancy globalOther;
    private ApplicationTenancy france;
    private ApplicationTenancy franceOther;
    private ApplicationTenancy viv;
    private ApplicationTenancy vivDefault;
    private ApplicationTenancy vivTa;
    private ApplicationTenancy piq;
    private ApplicationTenancy piqDefault;
    private ApplicationTenancy piqTa;
    private ApplicationTenancy italy;
    private ApplicationTenancy italyOther;
    private ApplicationTenancy grande;
    private ApplicationTenancy grandeDefault;
    private ApplicationTenancy grandeTa;

    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {{
            allowing(mockApplicationTenancies).allTenancies();
            will(returnValue(someTenancies()));

            allowing(mockApplicationTenancies).findByPath("/");
            will(returnValue(global));

            allowing(mockApplicationTenancies).findByPath("/GBR");
            will(returnValue(null));

            allowing(mockApplicationTenancies).findByPath("/ITA");
            will(returnValue(italy));

            allowing(mockApplicationTenancies).findByPath("/ITA/GRA");
            will(returnValue(grande));

            allowing(mockApplicationTenancies).findByPath("/ITA/GRA/_");
            will(returnValue(grandeDefault));

            allowing(mockApplicationTenancies).findByPath("/ITA/GRA/ta");
            will(returnValue(grandeTa));
        }});

        estatioApplicationTenancyRepository = new EstatioApplicationTenancyRepositoryForCountry();
        estatioApplicationTenancyRepository.applicationTenancies = mockApplicationTenancies;
    }

    private List<ApplicationTenancy> someTenancies() {
        global = tenancy("/", "Global");
        globalOther = tenancy("/_", "Global Other");
        france = tenancy("/FRA", "France");
        franceOther = tenancy("/FRA/_", "France Other");
        viv = tenancy("/FRA/VIV", "Vive (France)");
        vivDefault = tenancy("/FRA/VIV/_", "Vive (France) Other");
        vivTa = tenancy("/FRA/VIV/ta", "Vive (France) TA");
        piq = tenancy("/FRA/PIQ", "Piquant (France)");
        piqDefault = tenancy("/FRA/PIQ/_", "Piquant (France) Other");
        piqTa = tenancy("/FRA/PIQ/ta", "Piquant (France) TA");
        italy = tenancy("/ITA", "Italy");
        italyOther = tenancy("/ITA/_", "Italy Other");
        grande = tenancy("/ITA/GRA", "Grande (Italy)");
        grandeDefault = tenancy("/ITA/GRA/_", "Grande (Italy) Other");
        grandeTa = tenancy("/ITA/GRA/ta", "Grande (Italy) TA");

        return Lists.newArrayList(
                global,
                globalOther,
                france,
                franceOther,
                viv,
                vivDefault,
                vivTa,
                piq,
                piqDefault,
                piqTa,
                italy,
                italyOther,
                grande,
                grandeDefault,
                grandeTa
        );
    }

    private static ApplicationTenancy tenancy(final String path, final String name) {
        ApplicationTenancy applicationTenancy = new ApplicationTenancy();
        applicationTenancy.setPath(path);
        applicationTenancy.setName(name);
        return applicationTenancy;
    }

    private static Country country(final String reference) {
        Country country = new Country();
        country.setReference(reference);
        return country;
    }


    @Test
    public void testFindOrCreateCountryTenancy_whenExists() throws Exception {
        // given
        Country country = new Country();
        country.setReference("ITA");

        // when
        ApplicationTenancy countryTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);

        // then
        assertThat(countryTenancy).isEqualTo(italy);
    }

    @Test
    public void testFindOrCreateCountryTenancy_whenDoesNotExist() throws Exception {
        // given
        final Country country = new Country("GBR", "UK", "United Kingdom");

        // expect
        final ApplicationTenancy newlyCreatedTenancy = new ApplicationTenancy();
        context.checking(new Expectations() {{
            oneOf(mockApplicationTenancies).newTenancy("GBR", "/GBR", global);
            will(returnValue(newlyCreatedTenancy));
        }});

        // when
        ApplicationTenancy countryTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);

        // then
        assertThat(countryTenancy).isEqualTo(newlyCreatedTenancy);
    }

    @Test
    public void testAllCountryTenancies() throws Exception {
        List<ApplicationTenancy> applicationTenancies = estatioApplicationTenancyRepository.allCountryTenancies();

        Assertions.assertThat(applicationTenancies).containsExactly(france, italy);
    }

    @Test
    public void testPathForCountry() throws Exception {
        //given
        Country country = new Country("ITA", "IT", "ITALY");
        //then
        assertThat(estatioApplicationTenancyRepository.pathFor(country)).isEqualTo("/ITA");
    }


    @Test
    public void testAllCountryTenanciesFor() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.countryTenanciesFor(france);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(france);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.countryTenanciesFor(global);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(france, italy);

    }

    @Test
    public void testAllCountryTenanciesIncludeGlobalIfUserIsGlobalFor() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.countryTenanciesIncludeGlobalIfTenancyIsGlobalFor(france);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(france);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.countryTenanciesIncludeGlobalIfTenancyIsGlobalFor(global);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(global, france, italy);

    }

    @Test
    public void testFindCountryTenancyFor() {

        // given
        ApplicationTenancy atGlobalLevel = new ApplicationTenancy();
        atGlobalLevel.setPath("/");
        ApplicationTenancy atGlobalLevel_ = new ApplicationTenancy();
        atGlobalLevel_.setPath("/_");

        ApplicationTenancy atCountryLevel = new ApplicationTenancy();
        atCountryLevel.setPath("/ABC");
        ApplicationTenancy atCountryLevel_ = new ApplicationTenancy();
        atCountryLevel_.setPath("/ABC/_");

        ApplicationTenancy atPropertyLevel = new ApplicationTenancy();
        atPropertyLevel.setPath("/ABC/DEF");
        atPropertyLevel.setParent(atCountryLevel);

        ApplicationTenancy atLandLordLevel = new ApplicationTenancy();
        atLandLordLevel.setPath("/ABC/DEF/GHI");
        atLandLordLevel.setParent(atPropertyLevel);

        // when, then
        assertThat(estatioApplicationTenancyRepository.findCountryTenancyFor(atGlobalLevel).getPath()).isEqualTo("/");
        assertThat(estatioApplicationTenancyRepository.findCountryTenancyFor(atGlobalLevel_).getPath()).isEqualTo("/_");
        assertThat(estatioApplicationTenancyRepository.findCountryTenancyFor(atCountryLevel).getPath()).isEqualTo("/ABC");
        assertThat(estatioApplicationTenancyRepository.findCountryTenancyFor(atCountryLevel_).getPath()).isEqualTo("/ABC/_");
        assertThat(estatioApplicationTenancyRepository.findCountryTenancyFor(atPropertyLevel).getPath()).isEqualTo("/ABC");
        assertThat(estatioApplicationTenancyRepository.findCountryTenancyFor(atLandLordLevel).getPath()).isEqualTo("/ABC");

    }





}