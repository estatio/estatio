package org.estatio.module.lease.app;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.countryapptenancy.dom.CountryServiceForCurrentUser;
import org.estatio.module.lease.dom.LeaseItemType;

public class InvoiceServiceMenuTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);
    @Mock
    CountryRepository countryRepository;
    @Mock
    CountryServiceForCurrentUser countryServiceForCurrentUser;

    @Test
    public void doDefault2CalculateInvoicesForProperty_works_for_ita() {

        // given
        InvoiceServiceMenu menu = new InvoiceServiceMenu();
        menu.countryRepository = countryRepository;
        menu.countryServiceForCurrentUser = countryServiceForCurrentUser;
        Country italy = new Country("ITA", "Italy", "IT");

        // expect
        context.checking(new Expectations(){{
            oneOf(countryServiceForCurrentUser).countriesForCurrentUser();
            will(returnValue(Arrays.asList(italy)));
            oneOf(countryRepository).findCountry("ITA");
            will(returnValue(italy));
        }});

        // when
        List<LeaseItemType> defaultTypes = menu.default2CalculateInvoicesForProperty();
        // then
        Assertions.assertThat(defaultTypes).hasSize(2);

    }

    @Test
    public void doDefault2CalculateInvoicesForProperty_works_for_fra() {

        // given
        InvoiceServiceMenu menu = new InvoiceServiceMenu();
        menu.countryRepository = countryRepository;
        menu.countryServiceForCurrentUser = countryServiceForCurrentUser;
        Country france = new Country("FRA", "France", "FR");
        Country italy = new Country("ITA", "Italy", "IT");

        // expect
        context.checking(new Expectations(){{
            oneOf(countryServiceForCurrentUser).countriesForCurrentUser();
            will(returnValue(Arrays.asList(france)));
            oneOf(countryRepository).findCountry("ITA");
            will(returnValue(italy));
        }});

        // when
        List<LeaseItemType> defaultTypes = menu.default2CalculateInvoicesForProperty();
        // then
        Assertions.assertThat(defaultTypes).hasSize(11);

    }

    @Test
    public void doDefault2CalculateInvoicesForProperty_works_when_user_has_no_countries() {

        // given
        InvoiceServiceMenu menu = new InvoiceServiceMenu();
        menu.countryRepository = countryRepository;
        menu.countryServiceForCurrentUser = countryServiceForCurrentUser;
        Country italy = new Country("ITA", "Italy", "IT");

        // expect
        context.checking(new Expectations(){{
            oneOf(countryServiceForCurrentUser).countriesForCurrentUser();
            will(returnValue(Arrays.asList()));
            oneOf(countryRepository).findCountry("ITA");
            will(returnValue(italy));
        }});

        // when
        List<LeaseItemType> defaultTypes = menu.default2CalculateInvoicesForProperty();
        // then
        Assertions.assertThat(defaultTypes).hasSize(11);

    }

}