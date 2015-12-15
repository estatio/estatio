package org.estatio.dom.apptenancy;

import java.util.List;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.asset.Property;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class EstatioApplicationTenancyRepositoryTest {

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

    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

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

        estatioApplicationTenancyRepository = new EstatioApplicationTenancyRepository();
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
    public void testAllCountryTenancies() throws Exception {
        List<ApplicationTenancy> applicationTenancies = estatioApplicationTenancyRepository.allCountryTenancies();

        assertThat(applicationTenancies).containsExactly(france, italy);
    }

    @Test
    public void testAllPropertyTenancies() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesFor(country("FRA"));

        // then
        assertThat(applicationTenancies).containsExactly(viv, piq);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesFor(country("ITA"));

        // then
        assertThat(applicationTenancies).containsExactly(grande);
    }

    @Test
    public void testChildrenOf() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.childrenOf(france);

        // then
        assertThat(applicationTenancies).containsExactly(franceOther, viv, vivDefault, vivTa, piq, piqDefault, piqTa);

    }

    @Test
    public void testSelfOrChildrenOf() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.selfOrChildrenOf(france);

        // then
        assertThat(applicationTenancies).containsExactly(france, franceOther, viv, vivDefault, vivTa, piq, piqDefault, piqTa);

    }

    @Test
    public void testAllCountryTenanciesFor() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.countryTenanciesFor(france);

        // then
        assertThat(applicationTenancies).containsExactly(france);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.countryTenanciesFor(global);

        // then
        assertThat(applicationTenancies).containsExactly(france, italy);

    }

    @Test
    public void testAllPropertyTenanciesFor() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesUnder(france);

        // then
        assertThat(applicationTenancies).containsExactly(viv, piq);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesUnder(italy);

        // then
        assertThat(applicationTenancies).containsExactly(grande);

        // when
        applicationTenancies = estatioApplicationTenancyRepository.propertyTenanciesUnder(global);

        // then
        assertThat(applicationTenancies).containsExactly(viv, piq, grande);

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
            oneOf(mockApplicationTenancies).newTenancy("United Kingdom", "/GBR", global);
            will(returnValue(newlyCreatedTenancy));
        }});

        // when
        ApplicationTenancy countryTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);

        // then
        assertThat(countryTenancy).isEqualTo(newlyCreatedTenancy);
    }

    @Test
    public void testFindOrCreatePropertyTenancy_whenCountryAndPropertyExists() throws Exception {
        // given
        final Property property = new Property();
        property.setReference("GRA");
        property.setCountry(new Country("ITA", "ITA", "Italy"));

        // when
        ApplicationTenancy propertyTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(property);

        // then
        assertThat(propertyTenancy).isEqualTo(grande);
    }


    @Test
    public void testChoicesLocalTenanciesFor() throws Exception {

        // given
        Property property = new Property();
        property.setApplicationTenancyPath("/ITA/GRA");

        // when
        List<ApplicationTenancy> localTenancies = estatioApplicationTenancyRepository.localTenanciesFor(property);

        // then
        assertThat(localTenancies).containsExactly(grandeDefault, grandeTa);
    }

    @Test
    public void testFindOrCreateLocalNamedTenancy_whenExists() throws Exception {
        ApplicationTenancy localTenancy = estatioApplicationTenancyRepository.findOrCreateLocalNamedTenancy(grande, "_", "Default");
        assertThat(localTenancy).isEqualTo(grandeDefault);
    }

    @Test
    public void testFindOrCreateLocalNamedTenancy_whenDoesNotExist() throws Exception {

        final ApplicationTenancy newApplicationTenancy = new ApplicationTenancy();
        context.checking(new Expectations() {{
            oneOf(mockApplicationTenancies).findByPath("/ITA/GRA/abc");
            will(returnValue(null));

            oneOf(mockApplicationTenancies).newTenancy("Grande (Italy) ABC", "/ITA/GRA/abc", grande);
            will(returnValue(newApplicationTenancy));
        }});

        ApplicationTenancy localTenancy = estatioApplicationTenancyRepository.findOrCreateLocalNamedTenancy(grande, "abc", "ABC");
        assertThat(localTenancy).isEqualTo(newApplicationTenancy);
    }

    @Test
    public void testPathForCountry() throws Exception {
        //given
        Country country = new Country("ITA", "IT", "ITALY");
        //then
        assertThat(estatioApplicationTenancyRepository.pathFor(country)).isEqualTo("/ITA");
    }

    @Test
    public void testPathForProperty() throws Exception {
        //given
        final Property p = propertyWith("ITA", "GRA");
        //then
        assertThat(estatioApplicationTenancyRepository.pathFor(p)).isEqualTo("/ITA/GRA");
    }

    @Test
    public void testPathForPartyProperty() throws Exception {
        //given
        final Property p = propertyWith("ITA", "GRA");
        final Party pa = partyWith("HELLO");
        //then
        assertThat(estatioApplicationTenancyRepository.pathFor(p,pa)).isEqualTo("/ITA/GRA/HELLO");
    }

    private Party partyWith(final String hello) {
        Party pa = new Organisation();
        pa.setReference(hello);
        return pa;
    }

    private Property propertyWith(String countryCode, String reference){
        Property property = new Property();
        property.setReference(reference);
        property.setCountry(new Country(countryCode, countryCode, countryCode));
        return property;
    }


}