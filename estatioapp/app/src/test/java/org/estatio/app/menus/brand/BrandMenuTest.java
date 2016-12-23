package org.estatio.app.menus.brand;

import java.util.Collections;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.BrandRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class BrandMenuTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    MeService mockMeService;

    @Mock
    EstatioApplicationTenancyRepositoryForCountry mockEstatioApplicationTenancyRepository;

    @Mock
    BrandRepository mockBrandRepository;

    BrandMenu brandMenu;

    public static class ValidateNewBrand extends BrandMenuTest {

        ApplicationUser currentUser;

        @Before
        public void setUp() throws Exception {
            brandMenu = new BrandMenu();
            brandMenu.meService = mockMeService;
            brandMenu.estatioApplicationTenancyRepository = mockEstatioApplicationTenancyRepository;

            brandMenu.brandRepository = mockBrandRepository;

            currentUser = new ApplicationUser();
            context.checking(new Expectations() {{
                allowing(mockMeService).me();
                will(returnValue(currentUser));
            }});
        }

        Country countryOfBrand;

        @Test
        public void user_has_null_at_path() throws Exception {

            // given
            currentUser.setAtPath(null);
            countryOfBrand = null;

            // when
            final String reason = brandMenu.validateNewBrand(null, null, null, null, countryOfBrand);
            
            // then
            assertThat(reason).isEqualTo("You may only create country-specific brands");
        }

        @Test
        public void user_at_country_level_trying_to_create_a_global_brand() throws Exception {

            // given
            currentUser.setAtPath("/ITA");
            countryOfBrand = null;

            // when
            final String reason = brandMenu.validateNewBrand(null, null, null, null, countryOfBrand);

            // then
            assertThat(reason).isEqualTo("You may only create country-specific brands");

        }

        @Test
        public void user_at_country_level_can_create_a_country_brand() throws Exception {

            // given
            currentUser.setAtPath("/ITA");
            countryOfBrand = new Country();

            context.checking(new Expectations() {{
                ignoring(mockEstatioApplicationTenancyRepository);
                ignoring(mockBrandRepository);
            }});

            // when
            final String reason = brandMenu.validateNewBrand(null, null, null, null, countryOfBrand);

            // then
            assertThat(reason).isNull();

        }

        @Test
        public void user_at_global_level_can_create_a_global_brand() throws Exception {

            // given
            currentUser.setAtPath("/");
            countryOfBrand = null;

            context.checking(new Expectations() {{
                ignoring(mockEstatioApplicationTenancyRepository);
                ignoring(mockBrandRepository);
            }});

            // when
            final String reason = brandMenu.validateNewBrand(null, null, null, null, countryOfBrand);

            // then
            assertThat(reason).isNull();
        }

        @Test
        public void user_at_global_level_can_create_a_country_brand() throws Exception {

            // given
            currentUser.setAtPath("/");
            countryOfBrand = new Country();

            context.checking(new Expectations() {{
                ignoring(mockEstatioApplicationTenancyRepository);
                ignoring(mockBrandRepository);
            }});


            // when
            final String reason = brandMenu.validateNewBrand(null, null, null, null, countryOfBrand);

            // then
            assertThat(reason).isNull();
        }

        @Test
        public void brand_already_in_use() throws Exception {

            // given
            currentUser.setAtPath("/");
            countryOfBrand = new Country();
            String name = "someName";

            final ApplicationTenancy appTenancyOfCountryOfBrand = new ApplicationTenancy();

            context.checking(new Expectations() {{
                allowing(mockEstatioApplicationTenancyRepository).findOrCreateTenancyFor(countryOfBrand);
                will(returnValue(appTenancyOfCountryOfBrand));

                oneOf(mockBrandRepository).findByNameLowerCaseAndAppTenancy(name, appTenancyOfCountryOfBrand);
                will(returnValue(Lists.newArrayList(new Brand())));
            }});

            // when
            final String reason = brandMenu.validateNewBrand(name, null, null, null, countryOfBrand);

            // then
            assertThat(reason).isEqualTo("Brand with name someName exists already for null");

        }

        @Test
        public void happy_case() throws Exception {

            // given
            currentUser.setAtPath("/");
            countryOfBrand = new Country();
            String name = "someName";

            final ApplicationTenancy appTenancyOfCountryOfBrand = new ApplicationTenancy();

            context.checking(new Expectations() {{
                allowing(mockEstatioApplicationTenancyRepository).findOrCreateTenancyFor(countryOfBrand);
                will(returnValue(appTenancyOfCountryOfBrand));

                oneOf(mockBrandRepository).findByNameLowerCaseAndAppTenancy(name, appTenancyOfCountryOfBrand);
                will(returnValue(Collections.emptyList()));
            }});

            // when
            final String reason = brandMenu.validateNewBrand(name, null, null, null, countryOfBrand);

            // then
            assertThat(reason).isNull();

        }
    }
}