package org.estatio.dom.apptenancy;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepositoryForCharge {

    public List<ApplicationTenancy> allCountryTenancies() {
        return Lists.newArrayList(Iterables.filter(allTenancies(), Predicates.isCountry()));
    }


    private List<ApplicationTenancy> allTenancies() {
        return applicationTenancies.allTenancies();
    }

    @Inject
    ApplicationTenancyRepository applicationTenancies;

}
