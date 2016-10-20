package org.estatio.dom.apptenancy;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

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


    public static class Predicates {
        private Predicates() {
        }

        public static Predicate<? super ApplicationTenancy> isSelf(final ApplicationTenancy tenancy) {
            return candidate -> tenancy == candidate;
        }

        public static Predicate<? super ApplicationTenancy> isChildOf(final ApplicationTenancy tenancy) {
            final ApplicationTenancyLevel tenancyLevel = ApplicationTenancyLevel.of(tenancy);
            return candidate -> {
                final ApplicationTenancyLevel candidateLevel = ApplicationTenancyLevel.of(candidate);
                return candidateLevel.childOf(tenancyLevel);
            };
        }

        public static Predicate<? super ApplicationTenancy> isSelfOrChildOf(final ApplicationTenancy tenancy) {
            return com.google.common.base.Predicates.or(isSelf(tenancy), isChildOf(tenancy));
        }


    }



    @Inject
    ApplicationTenancyRepository applicationTenancies;


}
