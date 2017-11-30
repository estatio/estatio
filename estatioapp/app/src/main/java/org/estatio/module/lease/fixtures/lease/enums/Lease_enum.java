package org.estatio.module.lease.fixtures.lease.enums;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.EnumWithBuilderScript;
import org.apache.isis.applib.fixturescripts.EnumWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.AddressesCreationPolicy;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.InvoiceAddressCreationPolicy;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.ManagerRoleCreationPolicy;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.OccupancyCreationPolicy;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum Lease_enum implements EnumWithFinder<Lease> , EnumWithBuilderScript<Lease, LeaseBuilder> {

    KalPoison001Nl  (
            "KAL-POISON-001", "Poison Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.KalNl, "001",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.PoisonNl,
            ld(2011, 1, 1), ld(2020, 12, 31), null,
            OccupancyCreationPolicy.CREATE,
            "Poison", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "HEALT&BEAUTY", "PERFUMERIE",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfMediaX002Gb  (
            "OXF-MEDIAX-002", "Mediax Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb, "002",
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.MediaXGb,
            ld(2008, 1, 1), ld(2017, 12, 31), null,
            OccupancyCreationPolicy.CREATE,
            "Mediax", BrandCoverage.NATIONAL, Country_enum.GBR, "ELECTRIC", "ELECTRIC",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    OxfMiracl005Gb  (
            "OXF-MIRACL-005", "Miracle lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb, "005",
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.MiracleGb,
            ld(2013, 11, 7), ld(2023, 11, 6), null,
            OccupancyCreationPolicy.CREATE,
            "Miracle", BrandCoverage.NATIONAL, Country_enum.GBR, "FASHION", "ALL",
            ManagerRoleCreationPolicy.DONT_CREATE, Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPoison003Gb  (
            "OXF-POISON-003", "Poison Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb, "003",
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.PoisonGb,
            ld(2011, 1, 1), ld(2020, 12, 31), null,
            OccupancyCreationPolicy.CREATE,
            "Poison", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "HEALT&BEAUTY", "PERFUMERIE",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPret004Gb    (
            "OXF-PRET-004", "Pret-a-Partir lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb, "004",
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.PretGb,
            ld(2011, 7, 1), ld(2014, 6, 30), null,
            OccupancyCreationPolicy.DONT_CREATE,
            "Pret-a-Partir", BrandCoverage.REGIONAL, Country_enum.FRA,
            "FASHION", "ALL",
            ManagerRoleCreationPolicy.DONT_CREATE, Person_enum.GinoVannelliGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfTopModel001Gb(
            "OXF-TOPMODEL-001", "Topmodel Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb, "001",
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.TopModelGb,
            ld(2010, 7, 15), ld(2022, 7, 14), null,
            OccupancyCreationPolicy.CREATE,
            "Topmodel", BrandCoverage.NATIONAL, Country_enum.GBR, "FASHION", "WOMEN",
            ManagerRoleCreationPolicy.CREATE, Person_enum.GinoVannelliGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    BudPoison001Nl(
            "BUD-POISON-001", "Poison Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl, "001",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.PoisonNl,
            ld(2011, 1, 1), ld(2020, 12, 31), null,
            OccupancyCreationPolicy.CREATE,
            "Poison", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "HEALT&BEAUTY", "PERFUMERIE",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudMiracle002Nl(
            "BUD-MIRACLE-002", "Miracle Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl, "002",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.MiracleNl,
            ld(2011, 1, 1), ld(2015, 6, 30), ld(2015, 6, 30),
            OccupancyCreationPolicy.CREATE,
            "Miracle", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "HEALT&BEAUTY", "PERFUMERIE",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHello003Nl(
            "BUD-HELLO-003", "Hello Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl, "003",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.HelloWorldNl,
            ld(2015, 4, 1), ld(2020, 12, 31), null,
            OccupancyCreationPolicy.CREATE,
            "Hello", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "IT", "TELECOM",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudDago004Nl(
            "BUD-DAGO-004", "Dago Bank Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl, "004",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.DagoBankNl,
            ld(2011, 1, 1), ld(2015, 6, 30), ld(2015, 6, 30),
            OccupancyCreationPolicy.CREATE,
            "Dago Bank", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "BANK", "LOANS",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudNlBank004Nl(
            "BUD-NLBANK-004", "NL Bank Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl, "004",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.NlBankNl,
            ld(2015, 10, 1), ld(2020, 6, 30), null,
            OccupancyCreationPolicy.CREATE,
            "Nl Bank", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "BANK", "LOANS",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHyper005Nl(
            "BUD-HYPER-005", "Hypermarkt Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl, "005",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.HyperNl,
            ld(2015, 4, 1), ld(2015, 6, 30), ld(2015, 6, 30),
            OccupancyCreationPolicy.CREATE,
            "Nl Hypermarkt", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "SUPERMARKET", "RETAIL",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHello006Nl(
            "BUD-HELLO-006", "Hello Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl, "006",
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.DagoBankNl, // tenant looks wrong here...
            ld(2011, 1, 1), ld(2014, 12, 31), ld(2014, 12, 31),
            OccupancyCreationPolicy.CREATE,
            "Dago Bank", BrandCoverage.INTERNATIONAL, Country_enum.NLD, "BANK", "LOANS",
            ManagerRoleCreationPolicy.CREATE, Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    ;


    private final String ref;
    private final String name;
    private final PropertyAndUnitsAndOwnerAndManager_enum propertyAndUnits_d;
    private final String unitReferenceSuffix;
    private final OrganisationAndComms_enum landlord_d;
    private final OrganisationAndComms_enum tenant_d;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate occupancyEndDate;

    private final LeaseBuilder.OccupancyCreationPolicy occupancyCreationPolicy;
    private final String brand;
    private final BrandCoverage brandCoverage;
    private final Country_enum countryOfOrigin_d;
    private final String sector;
    private final String activity;

    private final LeaseBuilder.ManagerRoleCreationPolicy managerRoleCreationPolicy;
    private final Person_enum manager_d;

    private final InvoiceAddressCreationPolicy invoiceAddressCreationPolicy;
    private final AddressesCreationPolicy addressesCreationPolicy;

    public String getUnitReference() {
        return propertyAndUnits_d.getProperty_d().unitRef(unitReferenceSuffix);
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
                    final Property property = f.objectFor(propertyAndUnits_d, ec);
                    f.setUnit(property.findUnitByReference(getUnitReference()));
                })
                .setPrereq((f,ec) -> f.setLandlord(f.objectFor(landlord_d, ec)))
                .setPrereq((f,ec) -> f.setTenant(f.objectFor(tenant_d, ec)))
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setOccupancyEndDate(occupancyEndDate)

                .setOccupancyCreationPolicy(occupancyCreationPolicy)
                .setBrand(brand)
                .setBrandCoverage(brandCoverage)
                .setPrereq((f,ec) -> f.setCountryOfOrigin(f.objectFor(countryOfOrigin_d, ec)))
                .setSector(sector)
                .setActivity(activity)

                .setManagerRoleCreationPolicy(managerRoleCreationPolicy)
                .setPrereq((f,ec) -> f.setManager(f.objectFor(manager_d, ec)))

                .setInvoiceAddressCreationPolicy(invoiceAddressCreationPolicy)
                .setAddressesCreationPolicy(addressesCreationPolicy);
    }


}
