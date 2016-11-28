package org.estatio.dom.asset;

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

import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;

import static org.assertj.core.api.Assertions.assertThat;

public class EstatioApplicationTenancyRepositoryForProperty_UnitTest {

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

    private EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepositoryForProperty;
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepositoryForCountry;

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

        estatioApplicationTenancyRepositoryForProperty = new EstatioApplicationTenancyRepositoryForProperty();
        estatioApplicationTenancyRepositoryForCountry = new EstatioApplicationTenancyRepositoryForCountry();
        estatioApplicationTenancyRepositoryForProperty.applicationTenancies = mockApplicationTenancies;
        estatioApplicationTenancyRepositoryForProperty.estatioApplicationTenancyRepositoryForCountry = estatioApplicationTenancyRepositoryForCountry;
        estatioApplicationTenancyRepositoryForCountry.setApplicationTenancies(mockApplicationTenancies);
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

    @Test
    public void testPathForProperty() throws Exception {
        //given
        final Property p = propertyWith("ITA", "GRA");
        //then
        assertThat(estatioApplicationTenancyRepositoryForProperty.pathFor(p)).isEqualTo("/ITA/GRA");
    }


    @Test
    public void testFindOrCreatePropertyTenancy_whenCountryAndPropertyExists() throws Exception {
        // given
        final Property property = new Property();
        property.setReference("GRA");
        property.setCountry(new Country("ITA", "ITA", "Italy"));

        // when
        ApplicationTenancy propertyTenancy = estatioApplicationTenancyRepositoryForProperty.findOrCreateTenancyFor(property);

        // then
        assertThat(propertyTenancy).isEqualTo(grande);
    }

    @Test
    public void testAllPropertyTenanciesFor() throws Exception {
        List<ApplicationTenancy> applicationTenancies;

        // when
        applicationTenancies = estatioApplicationTenancyRepositoryForProperty.propertyTenanciesUnder(france);

        // then
        assertThat(applicationTenancies).containsExactly(viv, piq);

        // when
        applicationTenancies = estatioApplicationTenancyRepositoryForProperty.propertyTenanciesUnder(italy);

        // then
        assertThat(applicationTenancies).containsExactly(grande);

        // when
        applicationTenancies = estatioApplicationTenancyRepositoryForProperty.propertyTenanciesUnder(global);

        // then
        assertThat(applicationTenancies).containsExactly(viv, piq, grande);

    }

    @Test
    public void testChoicesLocalTenanciesFor() throws Exception {

        // given
        Property property = new Property();
        property.setApplicationTenancyPath("/ITA/GRA");

        // when
        List<ApplicationTenancy> localTenancies = estatioApplicationTenancyRepositoryForProperty.localTenanciesFor(property);

        // then
        assertThat(localTenancies).containsExactly(grandeDefault, grandeTa);
    }


    @Test
    public void testFindOrCreateLocalNamedTenancy_whenExists() throws Exception {
        ApplicationTenancy localTenancy = estatioApplicationTenancyRepositoryForProperty
                .findOrCreateLocalNamedTenancy(grande, "_", "Default");
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

        ApplicationTenancy localTenancy = estatioApplicationTenancyRepositoryForProperty
                .findOrCreateLocalNamedTenancy(grande, "abc", "ABC");
        assertThat(localTenancy).isEqualTo(newApplicationTenancy);
    }


    private Property propertyWith(String countryCode, String reference){
        Property property = new Property();
        property.setReference(reference);
        property.setCountry(new Country(countryCode, countryCode, countryCode));
        return property;
    }


}