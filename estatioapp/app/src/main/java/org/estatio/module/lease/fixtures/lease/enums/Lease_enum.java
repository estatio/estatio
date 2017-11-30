package org.estatio.module.lease.fixtures.lease.enums;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.EnumWithBuilderScript;
import org.apache.isis.applib.fixturescripts.EnumWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum Lease_enum implements EnumWithFinder<Lease> , EnumWithBuilderScript<Lease, LeaseBuilder> {

    KalPoison001Nl  (
            "KAL-POISON-001", "Poison Amsterdam", Property_enum.KalNl, "001",
            "Poison", BrandCoverage.INTERNATIONAL, "HEALT&BEAUTY", "PERFUMERIE", Country_enum.NLD,
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.PoisonNl,
            ld(2011, 1, 1), ld(2020, 12, 31),
            true, true,
            Person_enum.JohnDoeNl
    ),
    OxfMediaX002Gb  (
            "OXF-MEDIAX-002", "Mediax Lease", Property_enum.OxfGb, "002",
            "Mediax", BrandCoverage.NATIONAL, "ELECTRIC", "ELECTRIC", Country_enum.GBR,
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.MediaXGb,
            ld(2008, 1, 1), ld(2017, 12, 31),
            true, true,
            Person_enum.JohnSmithGb
    ),
    OxfMiracl005Gb  (
            "OXF-MIRACL-005", "Miracle lease", Property_enum.OxfGb, "005",
            "Miracle", BrandCoverage.NATIONAL, "FASHION", "ALL", Country_enum.GBR,
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.MiracleGb,
            ld(2013, 11, 7), ld(2023, 11, 6),
            false, true,
            Person_enum.JohnSmithGb
    ),
    OxfPoison003Gb  (
            "OXF-POISON-003", "Poison Lease", Property_enum.OxfGb, "003",
            "Poison", BrandCoverage.INTERNATIONAL, "HEALT&BEAUTY", "PERFUMERIE", Country_enum.NLD,
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.PoisonGb,
            ld(2011, 1, 1), ld(2020, 12, 31),
            true, true,
            Person_enum.JohnSmithGb
    ),
    OxfPret004Gb    (
            "OXF-PRET-004", "Pret-a-Partir lease", Property_enum.OxfGb, "004",
            "Pret-a-Partir", BrandCoverage.REGIONAL, "FASHION", "ALL", Country_enum.FRA,
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.PretGb,
            ld(2011, 7, 1), ld(2014, 6, 30),
            false, false,
            Person_enum.GinoVannelliGb
    ),
    OxfTopModel001Gb(
            "OXF-TOPMODEL-001", "Topmodel Lease", Property_enum.OxfGb, "001",
            "Topmodel", BrandCoverage.NATIONAL, "FASHION", "WOMEN", Country_enum.GBR,
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.TopModelGb,
            ld(2010, 7, 15), ld(2022, 7, 14),
            true, true,
            Person_enum.GinoVannelliGb
    ),
    ;

    private final String ref;
    private final String name;
    private final Property_enum property_d;
    private final String unitReferenceSuffix;
    private final String brand;
    private final BrandCoverage brandCoverage;
    private final String sector;
    private final String activity;
    private final Country_enum countryOfOrigin_d;
    private final OrganisationAndComms_enum landlord_d;
    private final OrganisationAndComms_enum tenant_d;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean createManagerRole;
    private final boolean createLeaseUnitAndTags;
    private final Person_enum manager_d;


    public String getUnitReference() {
        return property_d.unitRef(unitReferenceSuffix);
    }

    @Override
    public Lease findUsing(final ServiceRegistry2 serviceRegistry) {
        final LeaseRepository repository = serviceRegistry.lookupService(LeaseRepository.class);
        return repository.findLeaseByReference(ref);
    }

    @Override
    public LeaseBuilder toFixtureScript() {
        return new LeaseBuilder()
                .setReference(ref)
                .setName(name)
                .setPrereq((f, ec) -> {
                    final Property property = f.objectFor(property_d, ec);
                    f.setUnit(property.findUnitByReference(getUnitReference()));
                })
                .setBrand(brand)
                .setBrandCoverage(brandCoverage)
                .setSector(sector)
                .setActivity(activity)
                .setPrereq((f,ec) -> f.setCountryOfOrigin(f.objectFor(countryOfOrigin_d, ec)))
                .setPrereq((f,ec) -> f.setLandlord(f.objectFor(landlord_d, ec)))
                .setPrereq((f,ec) -> f.setTenant(f.objectFor(tenant_d, ec)))
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setCreateManagerRole(createManagerRole)
                .setCreateLeaseUnitAndTags(createLeaseUnitAndTags)
                .setPrereq((f,ec) -> f.setManager(f.objectFor(manager_d, ec)));
    }


}
