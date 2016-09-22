package org.estatio.dom.apptenancy;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepository {

    public List<ApplicationTenancy> allTenancies() {
        return applicationTenancies.allTenancies();
    }

    public List<ApplicationTenancy> selfOrChildrenOf(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isSelfOrChildOf(tenancy)));
    }

    public List<ApplicationTenancy> childrenOf(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isChildOf(tenancy)));
    }




    @Inject
    ApplicationTenancyRepository applicationTenancies;


}
