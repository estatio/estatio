package org.estatio.module.countryapptenancy.dom;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

public class CountryServiceForCurrentUser_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock MeService mockMeService;

    @Mock CountryRepository mockCountryRepository;

    @Test
    public void countriesForCurrentUser_works() throws Exception {

        // given
        CountryServiceForCurrentUser service = new CountryServiceForCurrentUser();
        service.meService = mockMeService;
        service.countryRepository = mockCountryRepository;
        ApplicationUser user = new ApplicationUser();
        user.setAtPath("/FRA;/BEL");
        Country france = new Country();
        Country belgium = new Country();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockMeService).me();
            will(returnValue(user));
            oneOf(mockCountryRepository).findCountries("FRA");
            will(returnValue(Arrays.asList(france)));
            oneOf(mockCountryRepository).findCountries("BEL");
            will(returnValue(Arrays.asList(belgium)));
        }});

        // when
        List<Country> result = service.countriesForCurrentUser();

        //
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).contains(france);
        Assertions.assertThat(result).contains(belgium);
    }

}