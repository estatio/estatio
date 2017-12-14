package org.estatio.module.lease.fixtures.lease.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.country.fixtures.enums.Country_enum.GBR;
import static org.estatio.module.country.fixtures.enums.Country_enum.NLD;
import static org.estatio.module.lease.dom.occupancy.tags.BrandCoverage.INTERNATIONAL;
import static org.estatio.module.lease.dom.occupancy.tags.BrandCoverage.NATIONAL;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.AddressesCreationPolicy;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.InvoiceAddressCreationPolicy;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum Lease_enum implements PersonaWithFinder<Lease>, PersonaWithBuilderScript<Lease, LeaseBuilder> {

    KalPoison001Nl  (
            "KAL-POISON-001", "Poison Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.KalNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("001", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null)
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfMediaX002Gb  (
            "OXF-MEDIAX-002", "Mediax Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.MediaXGb,
            ld(2008, 1, 1), ld(2017, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("002", "Mediax", NATIONAL, GBR, "ELECTRIC", "ELECTRIC", ld(2008, 1, 1), null)
            },
            Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    OxfMiracl005Gb  (
            "OXF-MIRACL-005", "Miracle lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.MiracleGb,
            ld(2013, 11, 7), ld(2023, 11, 6),
            new OccupancySpec[] {
                new OccupancySpec("005", "Miracle", NATIONAL, GBR, "FASHION", "ALL", ld(2013, 11, 7), null)
            },
            null, // no manager
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPoison003Gb  (
            "OXF-POISON-003", "Poison Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.PoisonGb,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("003", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null)
            },
            Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPret004Gb    (
            "OXF-PRET-004", "Pret-a-Partir lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.PretGb,
            ld(2011, 7, 1), ld(2014, 6, 30),
            new OccupancySpec[] {
                // although these values were provided, adding an occupancy was disabled.  So equiv to passing in no info
                // new OccupancySpec("004", "Pret-a-Partir", BrandCoverage.REGIONAL, Country_enum.FRA, "FASHION", "ALL", ...)
            },
            Person_enum.GinoVannelliGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfTopModel001Gb(
            "OXF-TOPMODEL-001", "Topmodel Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.TopModelGb,
            ld(2010, 7, 15), ld(2022, 7, 14),
            new OccupancySpec[] {
                new OccupancySpec("001", "Topmodel", NATIONAL, GBR, "FASHION", "WOMEN", ld(2010, 7, 15), null)
            },
            Person_enum.GinoVannelliGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    BudPoison001Nl(
            "BUD-POISON-001", "Poison Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("001", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null)
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudMiracle002Nl(
            "BUD-MIRACLE-002", "Miracle Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.MiracleNl,
            ld(2011, 1, 1), ld(2015, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("002", "Miracle", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), ld(2015, 6, 30))
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHello003Nl(
            "BUD-HELLO-003", "Hello Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.HelloWorldNl,
            ld(2015, 4, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("003", "Hello", INTERNATIONAL, NLD, "IT", "TELECOM", ld(2015, 4, 1), null)
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudDago004Nl(
            "BUD-DAGO-004", "Dago Bank Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.DagoBankNl,
            ld(2011, 1, 1), ld(2015, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("004", "Dago Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2011, 1, 1), ld(2015, 6, 30)),
                new OccupancySpec("007", "Dago Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2011, 1, 1), ld(2015, 6, 30))
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudNlBank004Nl(
            "BUD-NLBANK-004", "NL Bank Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.NlBankNl,
            ld(2015, 10, 1), ld(2020, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("004", "Nl Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2015, 10, 1), null)
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHyper005Nl(
            "BUD-HYPER-005", "Hypermarkt Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.HyperNl,
            ld(2015, 4, 1), ld(2015, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("005", "Nl Hypermarkt", INTERNATIONAL, NLD, "SUPERMARKET", "RETAIL", ld(2015, 4, 1), ld(2015, 6, 30))
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHello006Nl(
            "BUD-HELLO-006", "Hello Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.DagoBankNl, // tenant looks wrong here...
            ld(2011, 1, 1), ld(2014, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec( "006", "Dago Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2011, 1, 1), ld(2014, 12, 31))
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    ;

    @AllArgsConstructor
    @Data
    public static class OccupancySpec {
        String unitReferenceSuffix;
        String brand;
        BrandCoverage brandCoverage;
        Country_enum countryOfOrigin_d;
        String sector;
        String activity;
        LocalDate startDate;
        LocalDate endDate;
    }

    private final String ref;
    private final String name;
    private final PropertyAndUnitsAndOwnerAndManager_enum propertyAndUnits_d;
    private final OrganisationAndComms_enum landlord_d;
    private final Organisation_enum tenant_d;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private final OccupancySpec[] occupancySpecs;

    private final Person_enum manager_d;

    private final InvoiceAddressCreationPolicy invoiceAddressCreationPolicy;
    private final AddressesCreationPolicy addressesCreationPolicy;

    public String unitReferenceFor(String unitReferenceSuffix) {
        return propertyAndUnits_d.getProperty_d().unitRef(unitReferenceSuffix);
    }

    @Override
    public Lease findUsing(final ServiceRegistry2 serviceRegistry) {
        final LeaseRepository repository = serviceRegistry.lookupService(LeaseRepository.class);
        return repository.findLeaseByReference(ref);
    }

    @Override
    public LeaseBuilder builder() {
        return new LeaseBuilder()
                .setPrereq((f,ec) -> f.setProperty(f.objectFor(propertyAndUnits_d.getProperty_d(), ec)))
                .setReference(ref)
                .setName(name)
                .setPrereq((f,ec) -> f.setLandlord(f.objectFor(landlord_d, ec)))
                .setPrereq((f,ec) -> {
                    // if there are corresponding comms, then also create
                    Arrays.stream(OrganisationAndComms_enum.values())
                            .filter(x -> x.getOrganisation_d() == tenant_d)
                            .forEach(x -> f.objectFor(x, ec));

                    f.setTenant(f.objectFor(tenant_d, ec));
                })
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setPrereq((f, ec) -> {
                    final Property property = f.objectFor(propertyAndUnits_d, ec);
                    f.setOccupancySpecs(
                            Arrays.stream(Lease_enum.this.getOccupancySpecs())
                                    .map(x -> new LeaseBuilder.OccupancySpec(
                                            property.findUnitByReference(unitReferenceFor(x.unitReferenceSuffix)),
                                            x.brand, x.brandCoverage,
                                            f.objectFor(x.getCountryOfOrigin_d(), ec),
                                            x.sector, x.activity, x.startDate, x.endDate)
                                    )
                                    .collect(Collectors.toList()));
                })
                .setPrereq((f,ec) -> f.setManager(f.objectFor(manager_d, ec)))

                .setInvoiceAddressCreationPolicy(invoiceAddressCreationPolicy)
                .setAddressesCreationPolicy(addressesCreationPolicy);
    }


}
