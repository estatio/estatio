package org.estatio.dom.apptenancy;

import com.google.common.base.Predicate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.asset.Property;
import org.estatio.dom.geography.Country;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;

public class Predicates {
    private Predicates() {
    }

    public static Predicate<ApplicationTenancy> isCountry() {
        return candidate -> ApplicationTenancyLevel.of(candidate).isCountry();
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
