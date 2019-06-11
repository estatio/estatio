package org.incode.module.country.dom.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Country.class)
public class CountryRepository  {

    static final Pattern PATTERN_COUNTRY_CODE_FROM_ATPATH = Pattern.compile("/(?<reference>[^/]{3}).*");

    public List<Country> newCountry(
            final String reference,
            final String alpha2Code,
            final String name) {
        createCountry(reference, alpha2Code, name);
        return allCountries();
    }


    public List<Country> allCountries() {
        return repositoryService.allInstances(Country.class);
    }


    public Country createCountry(
            final String reference,
            final String alpha2Code,
            final String name) {
        final Country country = new Country(reference, alpha2Code, name);
        repositoryService.persistAndFlush(country);
        return country;
    }

    public Country findOrCreateCountry(
            final String reference,
            final String alpha2Code,
            final String name) {
        Country country = findCountry(reference);
        return country == null ? createCountry(reference, alpha2Code, name) : country;
    }

    public Country findCountry(
            final String reference) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                    Country.class,
                    "findByReference",
                    "reference", reference));
    }

    public Country findCountryByAlpha2Code(
            final String alpha2Code) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Country.class,
                        "findCountryByAlpha2Code",
                        "alpha2Code", alpha2Code));
    }

    public Country findCountryByAtPath(final String atPath) {
        if(atPath == null) {
            return null;
        }
        Matcher matcher = PATTERN_COUNTRY_CODE_FROM_ATPATH.matcher(atPath);
        if(!matcher.matches()) {
            return null;
        }
        final String reference = matcher.group("reference");
        return findCountry(reference);
    }

    public List<Country> countriesFor(final Iterable<String> countryCodes) {
        List<Country> available = Lists.newArrayList();
        final ImmutableMap<String, Country> countryByCode = Maps.uniqueIndex(allCountries(), input -> input.getName());
        for (String countryCodeForUser : countryCodes) {
            available.add(countryByCode.get(countryCodeForUser));
        }
        return available;
    }

    @Programmatic
    public List<Country> findCountries(final String regexReference) {
        return repositoryService.allMatches(
                new QueryDefault<>(Country.class,
                        "findLikeReference",
                        "reference", regexReference));
    }

    @Inject
    RepositoryService repositoryService;

}
