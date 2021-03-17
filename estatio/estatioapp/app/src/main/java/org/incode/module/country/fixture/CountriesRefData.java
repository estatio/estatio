package org.incode.module.country.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;

@Programmatic
public class CountriesRefData extends FixtureScript {

    public static final String GBR = "GBR";
    public static final String NLD = "NLD";
    public static final String ITA = "ITA";
    public static final String FRA = "FRA";
    public static final String SWE = "SWE";

    public static final String GBR_2 = "GB";
    public static final String ITA_2 = "IT";
    public static final String NLD_2 = "NL";
    public static final String SWE_2 = "SE";
    public static final String FRA_2 = "FR";

    @Override
    protected void execute(ExecutionContext executionContext) {

        createCountry(GBR, GBR_2, "United Kingdom", executionContext);
        createCountry(NLD, NLD_2, "Netherlands", executionContext);
        createCountry(ITA, ITA_2, "Italy", executionContext);
        createCountry(FRA, FRA_2, "France", executionContext);
        createCountry(SWE, SWE_2, "Sweden", executionContext);
    }

    private Country createCountry(final String reference, String alpha2Code, String name, ExecutionContext executionContext) {
        final Country country = countryRepository.createCountry(reference, alpha2Code, name);
        return executionContext.addResult(this, country.getAlpha2Code(), country);
    }

    // //////////////////////////////////////

    @Inject
    private StateRepository stateRepository;

    @Inject
    private CountryRepository countryRepository;

}
