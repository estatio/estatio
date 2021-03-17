package org.incode.module.country.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;

@Programmatic
public class StatesRefData extends FixtureScript {

    public static final String GBR = "GBR";
    public static final String NLD = "NLD";
    public static final String ITA = "ITA";
    public static final String FRA = "FRA";
    public static final String SWE = "SWE";

    public static final String GB = "GB";
    public static final String IT = "IT";
    public static final String NL = "NL";
    public static final String SE = "SE";
    public static final String FR = "FR";

    @Override
    protected void execute(ExecutionContext executionContext) {

        Country countryGBR = countryRepository.findCountry(GBR);
        Country countryNED = countryRepository.findCountry(NLD);
        Country countryITA = countryRepository.findCountry(ITA);
        Country countryFRA = countryRepository.findCountry(FRA);
        Country countrySWE = countryRepository.findCountry(SWE);
        
        createState(countryNED, "-DRN", "Drenthe", executionContext);
        createState(countryNED, "-FLE", "Flevoland", executionContext);
        createState(countryNED, "-FRI", "Friesland", executionContext);
        createState(countryNED, "-GEL", "Gelderland", executionContext);
        createState(countryNED, "-GRO", "Groningen", executionContext);
        createState(countryNED, "-LIM", "Limburg", executionContext);
        createState(countryNED, "-NBT", "Noord-Brabant", executionContext);
        createState(countryNED, "-NOH", "Noord-Holland", executionContext);
        createState(countryNED, "-OIJ", "Overijssel", executionContext);
        createState(countryNED, "-UTR", "Utrecht", executionContext);
        createState(countryNED, "-ZEL", "Zeeland", executionContext);
        createState(countryNED, "-ZUH", "Zuid-Holland", executionContext);

        createState(countryGBR, "-BED", "Bedfordshire", executionContext);
        createState(countryGBR, "-BEK", "Berkshire", executionContext);
        createState(countryGBR, "-BUK", "Buckinghamshire", executionContext);
        createState(countryGBR, "-CMB", "Cambridgeshire", executionContext);
        createState(countryGBR, "-CHE", "Cheshire", executionContext);
        createState(countryGBR, "-COR", "Cornwall", executionContext);
        createState(countryGBR, "-DBY", "Derbyshire", executionContext);
        createState(countryGBR, "-DEV", "Devon", executionContext);
        createState(countryGBR, "-DOR", "Dorset", executionContext);
        createState(countryGBR, "-DUR", "Durham", executionContext);
        createState(countryGBR, "-ESX", "Essex", executionContext);
        createState(countryGBR, "-GLO", "Gloucestershire", executionContext);
        createState(countryGBR, "-HAN", "Hampshire", executionContext);
        createState(countryGBR, "-KNT", "Kent", executionContext);
        createState(countryGBR, "-LAN", "Lancashire", executionContext);
        createState(countryGBR, "-LEI", "Leicerstershire", executionContext);
        createState(countryGBR, "-LIN", "Lincolnshire", executionContext);
        createState(countryGBR, "-NFK", "Norfolk", executionContext);
        createState(countryGBR, "-NTP", "Northamptonshire", executionContext);
        createState(countryGBR, "-NTB", "Northumberland", executionContext);
        createState(countryGBR, "-OXF", "Oxfordshire", executionContext);
        createState(countryGBR, "-RUT", "Rutland", executionContext);
        createState(countryGBR, "-SHR", "Shropshire", executionContext);
        createState(countryGBR, "-SOM", "Somerset", executionContext);
        createState(countryGBR, "-STA", "Staffordshire", executionContext);
        createState(countryGBR, "-SUF", "Suffolk", executionContext);
        createState(countryGBR, "-WAR", "Warwickshire", executionContext);
        createState(countryGBR, "-WIL", "Wiltshire", executionContext);
        createState(countryGBR, "-WOR", "Worcerstershire", executionContext);
    }

    private State createState(Country country, final String referenceSuffix, String name, ExecutionContext executionContext) {
        final String reference = country.getAlpha2Code() + referenceSuffix;
        final State state = stateRepository.newState(reference, name, country);
        return executionContext.addResult(this, state.getReference(), state);
    }

    // //////////////////////////////////////

    @Inject
    private StateRepository stateRepository;

    @Inject
    private CountryRepository countryRepository;

}
