package org.estatio.dom.asset;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.module.country.dom.EstatioApplicationTenancyRepositoryForCountry;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepositoryForProperty {


    @Programmatic
    public String pathFor(final Property property) {
        return estatioApplicationTenancyRepositoryForCountry.pathFor(property.getCountry()).concat(String.format("/%s", property.getReference()));
    }

    public ApplicationTenancy findOrCreateTenancyFor(final Property property) {
        ApplicationTenancy propertyTenancy = applicationTenancies.findByPath(pathFor(property));
        if (propertyTenancy != null){
            return propertyTenancy;
        }
        final ApplicationTenancy countryApplicationTenancy = estatioApplicationTenancyRepositoryForCountry.findOrCreateTenancyFor(property.getCountry());
        final String tenancyName = String.format("%s/%s ", countryApplicationTenancy.getPath(), property.getReference());
        return applicationTenancies.newTenancy(tenancyName, pathFor(property), countryApplicationTenancy);
    }


    public List<ApplicationTenancy> propertyTenanciesUnder(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isPropertyTenancyUnder(tenancy)));
    }


    public List<ApplicationTenancy> localTenanciesFor(final Property property) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isLocalOf(property)));
    }

    public ApplicationTenancy findOrCreateLocalDefaultTenancy(final ApplicationTenancy propertyTenancy) {
        return findOrCreateLocalNamedTenancy(propertyTenancy, "_", "Default");
    }

    public ApplicationTenancy findOrCreateLocalNamedTenancy(final ApplicationTenancy propertyTenancy, final String child, final String suffix) {
        ApplicationTenancyLevel propertyLevel = ApplicationTenancyLevel.of(propertyTenancy);

        ApplicationTenancyLevel localDefaultLevel = propertyLevel.child(child);
        ApplicationTenancy childTenancy = applicationTenancies.findByPath(localDefaultLevel.getPath());
        if (childTenancy == null) {
            childTenancy = applicationTenancies.newTenancy(propertyTenancy.getName() + " " + suffix, localDefaultLevel.getPath(), propertyTenancy);
        }
        return childTenancy;
    }



    private List<ApplicationTenancy> allTenancies() {
        return applicationTenancies.allTenancies();
    }



    public static class Predicates {
        private Predicates() {
        }

        public static Predicate<? super ApplicationTenancy> isLocalOf(final Property property) {
            final String propertyPath = property.getApplicationTenancyPath();
            final ApplicationTenancyLevel propertyLevel = ApplicationTenancyLevel.of(propertyPath);
            return candidate -> {
                ApplicationTenancyLevel candidateLevel = ApplicationTenancyLevel.of(candidate);
                return candidateLevel.childOf(propertyLevel);
            };
        }


        public static Predicate<? super ApplicationTenancy> isPropertyTenancyUnder(final ApplicationTenancy tenancy) {
            return com.google.common.base.Predicates.and(
                    isProperty(), org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository.Predicates.isSelfOrChildOf(tenancy));
        }

        public static Predicate<ApplicationTenancy> isProperty() {
            return candidate -> ApplicationTenancyLevel.of(candidate).isProperty();
        }

        public static Predicate<ApplicationTenancy> isPropertyOf(final Country country) {
            final String countryPath = "/" + country.getReference();
            final ApplicationTenancyLevel countryLevel = ApplicationTenancyLevel.of(countryPath);
            return candidate -> {
                ApplicationTenancyLevel candidateLevel = ApplicationTenancyLevel.of(candidate);
                return candidateLevel.isProperty() && candidateLevel.childOf(countryLevel);
            };
        }



    }

    @Inject
    private MeService meService;

    @Inject
    ApplicationTenancyRepository applicationTenancies;

    /**
     * for testing
     */
    public void setApplicationTenancies(final ApplicationTenancyRepository applicationTenancies) {
        this.applicationTenancies = applicationTenancies;
    }

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepositoryForCountry;

    /**
     * for testing
     */
    public void setEstatioApplicationTenancyRepositoryForCountry(final EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepositoryForCountry) {
        this.estatioApplicationTenancyRepositoryForCountry = estatioApplicationTenancyRepositoryForCountry;
    }
}
