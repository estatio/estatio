package org.estatio.dom.country;

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
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.country.dom.impl.Country;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;

/**
 * REVIEW: it's rather peculiar that this functionality has ended up here.  It was in udo-dom-geography (now moved out
 * into incode-module-country), but I wanted to decouple country from AppTenancy completely, so moved this out.
 *
 * It would seem that this module (party) is the only one that needs the functionality provided here...
 */
@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioApplicationTenancyRepositoryForCountry {

    public List<ApplicationTenancy> allCountryTenancies() {
        return Lists.newArrayList(Iterables.filter(allTenancies(), Predicates.isCountry()));
    }


    public ApplicationTenancy findOrCreateTenancyFor(final Country country) {
        final String countryPath = pathFor(country);
        ApplicationTenancy countryTenancy = applicationTenancies.findByPath(countryPath);
        if (countryTenancy != null){
            return countryTenancy;
        }
        final ApplicationTenancy rootTenancy = findOrCreateTenancyForGlobal();
        return applicationTenancies.newTenancy(country.getReference(), countryPath, rootTenancy);
    }


    public List<ApplicationTenancy> countryTenanciesFor(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isCountryTenancyFor(tenancy)));
    }

    public List<ApplicationTenancy> globalOrCountryTenanciesFor(final ApplicationTenancy tenancy) {
        return Lists.newArrayList(Iterables.filter(
                allTenancies(), Predicates.isGlobalOrCountryTenancyFor(tenancy)));
    }

    public List<ApplicationTenancy> countryTenanciesIncludeGlobalIfTenancyIsGlobalFor(final ApplicationTenancy tenancy) {
        return tenancy.getName() == "Global" ? globalOrCountryTenanciesFor(tenancy) : countryTenanciesFor(tenancy);
    }

    public ApplicationTenancy findCountryTenancyFor(final ApplicationTenancy applicationTenancy){
        ApplicationTenancy result = applicationTenancy;
        while (
                !
                        (
                                ApplicationTenancyLevel.of(result).isCountry()
                                        || ApplicationTenancyLevel.of(result).isCountryOther()
                                        || ApplicationTenancyLevel.of(result).isRoot()
                                        || ApplicationTenancyLevel.of(result).isRootOther()
                        )
                ) {
            result = result.getParent();
        }
        return result;
    }

    public List<ApplicationTenancy> countryTenanciesForCurrentUser() {
        final ApplicationUser currentUser = meService.me();
        return countryTenanciesFor(currentUser.getTenancy());
    }

    public List<ApplicationTenancy> globalOrCountryTenanciesForCurrentUser() {
        final ApplicationUser currentUser = meService.me();
        return globalOrCountryTenanciesFor(currentUser.getTenancy());
    }

    public List<ApplicationTenancy> countryTenanciesIncludeGlobalIfTenancyIsGlobalForCurrentUser() {
        final ApplicationUser currentUser = meService.me();
        return countryTenanciesIncludeGlobalIfTenancyIsGlobalFor(currentUser.getTenancy());
    }



    private List<ApplicationTenancy> allTenancies() {
        return applicationTenancies.allTenancies();
    }

    @Programmatic
    public String pathFor(final Country country) {
        return String.format("/%s", country.getReference());
    }

    private ApplicationTenancy findOrCreateTenancyForGlobal() {
        return applicationTenancies.findByPath("/");
    }



    public static class Predicates {
        private Predicates() {
        }

        public static Predicate<? super ApplicationTenancy> isCountryTenancyFor(final ApplicationTenancy tenancy) {
            return com.google.common.base.Predicates.and(
                    isCountry(), org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository.Predicates.isSelfOrChildOf(tenancy));
        }

        public static Predicate<? super ApplicationTenancy> isGlobalOrCountryTenancyFor(final ApplicationTenancy tenancy) {
            return com.google.common.base.Predicates.or(
                    isGlobal(), isCountryTenancyFor(tenancy));
        }

        public static Predicate<? super ApplicationTenancy> isGlobal() {
            return input -> ApplicationTenancyLevel.of(input).isRoot();
        }

        public static Predicate<ApplicationTenancy> isCountry() {
            return candidate -> ApplicationTenancyLevel.of(candidate).isCountry();
        }


    }

    @Inject
    ApplicationTenancyRepository applicationTenancies;

    /**
     * For testing
     */
    public void setApplicationTenancies(final ApplicationTenancyRepository applicationTenancies) {
        this.applicationTenancies = applicationTenancies;
    }

    @Inject
    private MeService meService;


}
