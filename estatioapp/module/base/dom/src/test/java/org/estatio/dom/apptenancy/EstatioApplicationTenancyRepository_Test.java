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

import static org.assertj.core.api.Assertions.assertThat;

public class EstatioApplicationTenancyRepository_Test {

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

    ApplicationTenancy applicationTenancyCountry;

}