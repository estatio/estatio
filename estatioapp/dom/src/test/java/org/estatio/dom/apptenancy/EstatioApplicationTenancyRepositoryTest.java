package org.estatio.dom.apptenancy;

import java.util.List;
import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.estatio.dom.asset.Property;
import org.estatio.dom.geography.Country;

public class EstatioApplicationTenancyRepositoryTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ApplicationTenancies mockApplicationTenancies;

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

    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {{
            allowing(mockApplicationTenancies).allTenancies();
            will(returnValue(someTenancies()));

            allowing(mockApplicationTenancies).findTenancyByPath("/");
            will(returnValue(global));

            allowing(mockApplicationTenancies).findTenancyByPath("/it");
            will(returnValue(italy));

            allowing(mockApplicationTenancies).findTenancyByPath("/it/GRA");
            will(returnValue(grande));

            allowing(mockApplicationTenancies).findTenancyByPath("/it/GRA/_");
            will(returnValue(grandeDefault));

            allowing(mockApplicationTenancies).findTenancyByPath("/it/GRA/ta");
            will(returnValue(grandeTa));
        }});

        estatioApplicationTenancyRepository = new EstatioApplicationTenancyRepository();
        estatioApplicationTenancyRepository.applicationTenancies = mockApplicationTenancies;
    }

    private List<ApplicationTenancy> someTenancies() {
        global = tenancy("/", "Global");
        globalOther = tenancy("/_", "Global Other");
        france = tenancy("/fr", "France");
        franceOther = tenancy("/fr/_", "France Other");
        viv = tenancy("/fr/VIV", "Vive (France)");
        vivDefault = tenancy("/fr/VIV/_", "Vive (France) Other");
        vivTa = tenancy("/fr/VIV/ta", "Vive (France) TA");
        piq = tenancy("/fr/PIQ", "Piquant (France)");
        piqDefault = tenancy("/fr/PIQ/_", "Piquant (France) Other");
        piqTa = tenancy("/fr/PIQ/ta", "Piquant (France) TA");
        italy = tenancy("/it", "Italy");
        italyOther = tenancy("/it/_", "Italy Other");
        grande = tenancy("/it/GRA", "Grande (Italy)");
        grandeDefault = tenancy("/it/GRA/_", "Grande (Italy) Other");
        grandeTa = tenancy("/it/GRA/ta", "Grande (Italy) TA");

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

    private static Country country(final String alpha2Code) {
        Country country = new Country();
        country.setAlpha2Code(alpha2Code);
        return country;
    }




    @Test
    public void testAllCountryTenancies() throws Exception {
        List<ApplicationTenancy> applicationTenancies = estatioApplicationTenancyRepository.allCountryTenancies();

        Assertions.assertThat(applicationTenancies).containsExactly(france, italy);
    }

    @Test
    public void testAllPropertyTenancies() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesFor(country("fr"));

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(viv, piq);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesFor(country("it"));

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(grande);
    }

    @Test
    public void testSelfOrChildrenOf() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.selfOrChildrenOf(france);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(france, franceOther, viv, vivDefault, vivTa, piq, piqDefault, piqTa);

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
    public void testAllPropertyTenanciesFor() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesUnder(france);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(viv, piq);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesUnder(italy);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(grande);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesUnder(global);

        // then
        Assertions.assertThat(applicationTenancies).containsExactly(viv, piq, grande);

    }


    @Test
    public void testFindOrCreateCountryTenancy_whenExists() throws Exception {
        // given
        Country country = new Country();
        country.setReference("it");

        // when
        ApplicationTenancy countryTenancy = estatioApplicationTenancyRepository.findOrCreateCountryTenancy(country);

        // then
        Assertions.assertThat(countryTenancy).isEqualTo(italy);
    }

    @Test
    public void testFindOrCreateCountryTenancy_whenDoesNotExist() throws Exception {
        // given
        final Country country = new Country();
        country.setReference("GBR");
        country.setAlpha2Code("uk");
        country.setName("United Kingdom");

        // expect
        final ApplicationTenancy newlyCreatedTenancy = new ApplicationTenancy();
        context.checking(new Expectations() {{
            oneOf(mockApplicationTenancies).newTenancy("United Kingdom", "/GBR", global);
            will(returnValue(newlyCreatedTenancy));
        }});

        // when
        ApplicationTenancy countryTenancy = estatioApplicationTenancyRepository.findOrCreateCountryTenancy(country);

        // then
        Assertions.assertThat(countryTenancy).isEqualTo(newlyCreatedTenancy);
    }

    @Test
    public void testFindOrCreatePropertyTenancy_whenCountryAndPropertyExists() throws Exception {
        // given
        final ApplicationTenancy countryApplicationTenancy = new ApplicationTenancy();
        countryApplicationTenancy.setPath("/it");

        // when
        ApplicationTenancy propertyTenancy = estatioApplicationTenancyRepository.findOrCreatePropertyTenancy(countryApplicationTenancy, "GRA");

        // then
        Assertions.assertThat(propertyTenancy).isEqualTo(grande);
    }

    @Test
    public void testFindOrCreatePropertyTenancy_whenCountryExistsButPropertyDoesNot() throws Exception {

        // expect
        final ApplicationTenancy newApplicationTenancy = new ApplicationTenancy();
        context.checking(new Expectations() {{

            allowing(mockApplicationTenancies).findTenancyByPath("/it/XXX");
            will(returnValue(null));

            oneOf(mockApplicationTenancies).newTenancy("XXX (Italy)", "/it/XXX", italy);
            will(returnValue(newApplicationTenancy));
        }});

        // when
        ApplicationTenancy propertyTenancy = estatioApplicationTenancyRepository.findOrCreatePropertyTenancy(italy, "XXX");

        // then
        Assertions.assertThat(propertyTenancy).isEqualTo(newApplicationTenancy);
    }

    @Test
    public void testChoicesLocalTenanciesFor() throws Exception {

        // given
        Property property = new Property();
        property.setApplicationTenancyPath("/it/GRA");

        // when
        List<ApplicationTenancy> localTenancies = estatioApplicationTenancyRepository.localTenanciesFor(property);

        // then
        Assertions.assertThat(localTenancies).containsExactly(grandeDefault, grandeTa);
    }


    @Test
    public void testFindOrCreateLocalNamedTenancy_whenExists() throws Exception {
        ApplicationTenancy localTenancy = estatioApplicationTenancyRepository.findOrCreateLocalNamedTenancy(grande, "_", "Default");
        Assertions.assertThat(localTenancy).isEqualTo(grandeDefault);
    }

    @Test
    public void testFindOrCreateLocalNamedTenancy_whenDoesNotExist() throws Exception {

        final ApplicationTenancy newApplicationTenancy = new ApplicationTenancy();
        context.checking(new Expectations() {{
            oneOf(mockApplicationTenancies).findTenancyByPath("/it/GRA/abc");
            will(returnValue(null));

            oneOf(mockApplicationTenancies).newTenancy("Grande (Italy) ABC", "/it/GRA/abc", grande);
            will(returnValue(newApplicationTenancy));
        }});

        ApplicationTenancy localTenancy = estatioApplicationTenancyRepository.findOrCreateLocalNamedTenancy(grande, "abc", "ABC");
        Assertions.assertThat(localTenancy).isEqualTo(newApplicationTenancy);
    }

}