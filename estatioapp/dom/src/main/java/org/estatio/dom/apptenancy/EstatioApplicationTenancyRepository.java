package org.estatio.dom.apptenancy;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.dom.asset.Property;
import org.estatio.dom.geography.Country;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepository {

    //region > getId
    public String getId() {
        return "EstatioApplicationTenancyRepository";
    }
    //endregion

    public List<ApplicationTenancy> allTenancies() {
        return applicationTenancies.allTenancies();
    }

    public List<ApplicationTenancy> allCountryTenancies() {
        return Lists.newArrayList(Iterables.filter(allTenancies(), Predicates.isCountry()));
    }

    public List<ApplicationTenancy> propertyTenanciesFor(final Country country) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isPropertyOf(country)));
    }

    public List<ApplicationTenancy> selfOrChildrenOf(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isSelfOrChildOf(tenancy)));
    }

    public List<ApplicationTenancy> childrenOf(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isChildOf(tenancy)));
    }

    public List<ApplicationTenancy> countryTenanciesFor(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isCountryTenancyFor(tenancy)));
    }

    public List<ApplicationTenancy> globalOrCountryTenanciesFor(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isGlobalOrCountryTenancyFor(tenancy)));
    }

    public List<ApplicationTenancy> countryTenanciesForCurrentUser() {
        final ApplicationUser currentUser = meService.me();
        return countryTenanciesFor(currentUser.getTenancy());
    }

    public List<ApplicationTenancy> globalOrCountryTenanciesForCurrentUser() {
        final ApplicationUser currentUser = meService.me();
        return globalOrCountryTenanciesFor(currentUser.getTenancy());
    }

    public List<ApplicationTenancy> propertyTenanciesUnder(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isPropertyTenancyUnder(tenancy)));
    }

    public List<ApplicationTenancy> propertyTenanciesForCurrentUser() {
        final ApplicationUser currentUser = meService.me();
        return propertyTenanciesUnder(currentUser.getTenancy());
    }

    // //////////////////////////////////////

    public ApplicationTenancy findOrCreateCountryTenancy(final Country country) {

        final String countryPath = String.format("/%s", country.getReference());

        for (final ApplicationTenancy countryTenancy : allCountryTenancies()) {
            if (countryTenancy.getPath().equals(countryPath)) {
                return countryTenancy;
            }
        }

        final ApplicationTenancy rootTenancy = applicationTenancies.findTenancyByPath("/");
        return applicationTenancies.newTenancy(country.getName(), countryPath, rootTenancy);
    }

    public ApplicationTenancy findOrCreatePropertyTenancy(final Property property) {
        return findOrCreatePropertyTenancy(findOrCreateCountryTenancy(property.getCountry()), property.getReference());
    }

    public ApplicationTenancy findOrCreateLeaseTenancy(final Lease lease) {
        final ApplicationTenancy propertyTenancy = findOrCreatePropertyTenancy(lease.getProperty());
        return findOrCreateLocalDefaultTenancy(propertyTenancy);
    }

    public ApplicationTenancy findOrCreatePropertyTenancy(final ApplicationTenancy countryApplicationTenancy, final String propertyReference) {

        final ApplicationTenancyLevel countryAppTenancyLevel = ApplicationTenancyLevel.of(countryApplicationTenancy);
        if (!countryAppTenancyLevel.isCountry()) {
            throw new IllegalArgumentException(String.format("Application tenancy '%s' is not a country-level", countryApplicationTenancy));
        }

        final ApplicationTenancyLevel propertyAppTenancyLevel = countryAppTenancyLevel.child(propertyReference);
        final ApplicationTenancy propertyApplicationTenancy = applicationTenancies.findTenancyByPath(propertyAppTenancyLevel.getPath());
        if (propertyApplicationTenancy != null) {
            return propertyApplicationTenancy;
        }

        final String tenancyName = String.format("%s (%s)", propertyReference, countryApplicationTenancy.getName());
        return applicationTenancies.newTenancy(tenancyName, propertyAppTenancyLevel.getPath(), countryApplicationTenancy);
    }

    public ApplicationTenancy findOrCreateLocalDefaultTenancy(final ApplicationTenancy propertyTenancy) {
        return findOrCreateLocalNamedTenancy(propertyTenancy, "_", "Default");
    }

    public ApplicationTenancy findOrCreateLocalTaTenancy(final ApplicationTenancy propertyTenancy) {
        return findOrCreateLocalNamedTenancy(propertyTenancy, "ta", "TA");
    }

    public ApplicationTenancy findOrCreateLocalNamedTenancy(final ApplicationTenancy propertyTenancy, final String child, final String suffix) {
        ApplicationTenancyLevel propertyLevel = ApplicationTenancyLevel.of(propertyTenancy);

        ApplicationTenancyLevel localDefaultLevel = propertyLevel.child(child);
        ApplicationTenancy childTenancy = applicationTenancies.findTenancyByPath(localDefaultLevel.getPath());
        if (childTenancy == null) {
            childTenancy = applicationTenancies.newTenancy(propertyTenancy.getName() + " " + suffix, localDefaultLevel.getPath(), propertyTenancy);
        }
        return childTenancy;
    }

    public List<ApplicationTenancy> localTenanciesFor(final Property property) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isLocalOf(property)));
    }

    // //////////////////////////////////////

    public static class Predicates {
        private Predicates() {
        }

        public static Predicate<ApplicationTenancy> isCountry() {
            return candidate -> ApplicationTenancyLevel.of(candidate).isCountry();
        }

        public static Predicate<ApplicationTenancy> isProperty() {
            return candidate -> ApplicationTenancyLevel.of(candidate).isProperty();
        }

        public static Predicate<ApplicationTenancy> isPropertyOf(final Country country) {
            final String countryPath = "/" + country.getAlpha2Code().toLowerCase();
            final ApplicationTenancyLevel countryLevel = ApplicationTenancyLevel.of(countryPath);
            return candidate -> {
                ApplicationTenancyLevel candidateLevel = ApplicationTenancyLevel.of(candidate);
                return candidateLevel.isProperty() && candidateLevel.childOf(countryLevel);
            };
        }

        public static Predicate<? super ApplicationTenancy> isLocalOf(final Property property) {
            final String propertyPath = property.getApplicationTenancyPath();
            final ApplicationTenancyLevel propertyLevel = ApplicationTenancyLevel.of(propertyPath);
            return candidate -> {
                ApplicationTenancyLevel candidateLevel = ApplicationTenancyLevel.of(candidate);
                return candidateLevel.childOf(propertyLevel);
            };
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

        public static Predicate<? super ApplicationTenancy> isCountryTenancyFor(final ApplicationTenancy tenancy) {
            return com.google.common.base.Predicates.and(
                    isCountry(), isSelfOrChildOf(tenancy));
        }

        public static Predicate<? super ApplicationTenancy> isGlobalOrCountryTenancyFor(final ApplicationTenancy tenancy) {
            return com.google.common.base.Predicates.or(
                    isGlobal(), isCountryTenancyFor(tenancy));
        }

        public static Predicate<? super ApplicationTenancy> isGlobal() {
            return input -> ApplicationTenancyLevel.of(input).isRoot();
        }

        public static Predicate<? super ApplicationTenancy> isPropertyTenancyUnder(final ApplicationTenancy tenancy) {
            return com.google.common.base.Predicates.and(
                    isProperty(), isSelfOrChildOf(tenancy));
        }
    }

    // //////////////////////////////////////

    @Inject
    ApplicationTenancies applicationTenancies;

    @Inject
    private MeService meService;

}
