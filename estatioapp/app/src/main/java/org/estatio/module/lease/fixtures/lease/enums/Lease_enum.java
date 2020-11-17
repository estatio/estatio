package org.estatio.module.lease.fixtures.lease.enums;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentTemplate;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder;
import org.estatio.module.lease.fixtures.numerators.enums.PropertyOwnerNumerator_enum;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.lease.dom.occupancy.tags.BrandCoverage.INTERNATIONAL;
import static org.estatio.module.lease.dom.occupancy.tags.BrandCoverage.NATIONAL;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.AddressesCreationPolicy;
import static org.estatio.module.lease.fixtures.lease.builders.LeaseBuilder.InvoiceAddressCreationPolicy;
import static org.incode.module.base.integtests.VT.ld;
import static org.incode.module.country.fixtures.enums.Country_enum.BEL;
import static org.incode.module.country.fixtures.enums.Country_enum.GBR;
import static org.incode.module.country.fixtures.enums.Country_enum.ITA;
import static org.incode.module.country.fixtures.enums.Country_enum.NLD;
import static org.incode.module.country.fixtures.enums.Country_enum.SWE;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum Lease_enum implements PersonaWithFinder<Lease>, PersonaWithBuilderScript<Lease, LeaseBuilder> {

    KalPoison001Nl  (
            "KAL-POISON-001", null, "Poison Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.KalNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("001", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfMediaX002Gb  (
            "OXF-MEDIAX-002", null, "Mediax Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.MediaXGb,
            ld(2008, 1, 1), ld(2017, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("002", "Mediax", NATIONAL, GBR, "ELECTRIC", "ELECTRIC", ld(2008, 1, 1), null, new BigDecimal("111.11"), null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    OxfMiracl005Gb  (
            "OXF-MIRACL-005", null, "Miracle lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.MiracleGb,
            ld(2013, 11, 7), ld(2023, 11, 6),
            new OccupancySpec[] {
                new OccupancySpec("005", "Miracle", NATIONAL, GBR, "FASHION", "ALL", ld(2013, 11, 7), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            null, // no manager
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPoison003Gb  (
            "OXF-POISON-003", null, "Poison Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.PoisonGb,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("003", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPoison010ADVANCEGb  (
            "OXF-POISON-010", null, "Poison Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.PoisonGb,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
            new OccupancySpec("010", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPoison011ARREARSGb  (
            "OXF-POISON-011", null, "Poison Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.PoisonGb,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                    new OccupancySpec("011", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnSmithGb,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfPret004Gb    (
            "OXF-PRET-004", null, "Pret-a-Partir lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.PretGb,
            ld(2011, 7, 1), ld(2014, 6, 30),
            new OccupancySpec[] {
                // although these values were provided, adding an occupancy was disabled.  So equiv to passing in no info
                // new OccupancySpec("004", "Pret-a-Partir", BrandCoverage.REGIONAL, Country_enum.FRA, "FASHION", "ALL", ...)
            },
            new AmendmentSpec[]{
            },
            Person_enum.GinoVannelliGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfFix006Gb    (
            "OXF-FIX-006", null, "Fixed lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.PretGb,
            ld(2011, 7, 1), ld(2019, 1, 1),
            new OccupancySpec[] {
                    new OccupancySpec("006", "Fix", BrandCoverage.REGIONAL, Country_enum.FRA, "FASHION", "ALL", ld(2011, 1, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.GinoVannelliGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    OxfTopModel001Gb(
            "OXF-TOPMODEL-001", null, "Topmodel Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.HelloWorldGb, Organisation_enum.TopModelGb,
            ld(2010, 7, 15), ld(2022, 7, 14),
            new OccupancySpec[] {
                new OccupancySpec("001", "Topmodel", NATIONAL, GBR, "FASHION", "WOMEN", ld(2010, 7, 15), null, new BigDecimal("200.25"), null, null)
            },
            new AmendmentSpec[]{
                new AmendmentSpec(LeaseAmendmentTemplate.DEMO_TYPE)
            },
            Person_enum.GinoVannelliGb,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    BudPoison001Nl(
            "BUD-POISON-001", null, "Poison Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("001", "Poison", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudMiracle002Nl(
            "BUD-MIRACLE-002", null, "Miracle Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.MiracleNl,
            ld(2011, 1, 1), ld(2015, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("002", "Miracle", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), ld(2015, 6, 30), null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHello003Nl(
            "BUD-HELLO-003", null, "Hello Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.HelloWorldNl,
            ld(2015, 4, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec("003", "Hello", INTERNATIONAL, NLD, "IT", "TELECOM", ld(2015, 4, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudDago004Nl(
            "BUD-DAGO-004", null, "Dago Bank Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.DagoBankNl,
            ld(2011, 1, 1), ld(2015, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("004", "Dago Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2011, 1, 1), ld(2015, 6, 30), null, null, null),
                new OccupancySpec("007", "Dago Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2011, 1, 1), ld(2015, 6, 30), null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudNlBank004Nl(
            "BUD-NLBANK-004", null, "NL Bank Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.NlBankNl,
            ld(2015, 10, 1), ld(2020, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("004", "Nl Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2015, 10, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHyper005Nl(
            "BUD-HYPER-005", null, "Hypermarkt Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.HyperNl,
            ld(2015, 4, 1), ld(2015, 6, 30),
            new OccupancySpec[] {
                new OccupancySpec("005", "Nl Hypermarkt", INTERNATIONAL, NLD, "SUPERMARKET", "RETAIL", ld(2015, 4, 1), ld(2015, 6, 30), null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    BudHello006Nl(
            "BUD-HELLO-006", null, "Hello Amsterdam", PropertyAndUnitsAndOwnerAndManager_enum.BudNl,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.DagoBankNl, // tenant looks wrong here...
            ld(2011, 1, 1), ld(2014, 12, 31),
            new OccupancySpec[] {
                new OccupancySpec( "006", "Dago Bank", INTERNATIONAL, NLD, "BANK", "LOANS", ld(2011, 1, 1), ld(2014, 12, 31), null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.CREATE
    ),
    HanPoison001Se  (
            "HAN-POISON-001", "4060-1001-02", "Poison Handla Center", PropertyAndUnitsAndOwnerAndManager_enum.HanSe,
            OrganisationAndComms_enum.HelloWorldSe, Organisation_enum.PoisonSe,
            ld(2011, 1, 1), ld(2020, 12, 31),
            new OccupancySpec[] {
                    new OccupancySpec("001", "Poison", INTERNATIONAL, SWE, "HEALT&BEAUTY", "PERFUMERIE", ld(2011, 1, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeSe,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    HanTopModel002Se(
            "HAN-TOPMODEL-002", "4060-0090-03", "Topmodel Handla Center", PropertyAndUnitsAndOwnerAndManager_enum.HanSe,
            OrganisationAndComms_enum.HelloWorldSe, Organisation_enum.TopModelSe,
            ld(2010, 7, 15), ld(2022, 7, 14),
            new OccupancySpec[] {
                    new OccupancySpec("002", "Topmodel", NATIONAL, SWE, "FASHION", "WOMEN", ld(2010, 7, 15), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.GinoVannelliSe,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    HanOmsHyral003Se(
            "HAN-OMSHYRA-003", "4060-0100-05", "OmsHyra Handla Center", PropertyAndUnitsAndOwnerAndManager_enum.HanSe,
            OrganisationAndComms_enum.HelloWorldSe, Organisation_enum.OmsHyraSe,
            ld(2012, 6, 1), ld(2020, 5, 31),
            new OccupancySpec[] {
                    new OccupancySpec("003", "Omshyra", NATIONAL, SWE, "RENTALS", "CARS", ld(2012, 6, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.GinoVannelliSe,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    HanDoubleCharge004Se(
            "HAN-DOUBLECHARGE-004", "4124-1007-01", "OmsHyra Handla Center", PropertyAndUnitsAndOwnerAndManager_enum.HanSe,
            OrganisationAndComms_enum.HelloWorldSe, Organisation_enum.OmsHyraSe,
            ld(2012, 6, 1), ld(2020, 5, 31),
            new OccupancySpec[] {
                    new OccupancySpec("004", "Omshyra", NATIONAL, SWE, "RENTALS", "CARS", ld(2012, 6, 1), null,null,null,null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.GinoVannelliSe,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    RonTopModel001It(
            "RON-TOPMODEL-001", null, "Topmodel Lease", PropertyAndUnitsAndOwnerAndManager_enum.RonIt,
            OrganisationAndComms_enum.HelloWorldIt, Organisation_enum.TopModelIt,
            ld(2010, 7, 15), ld(2022, 7, 14),
            new OccupancySpec[] {
                    new OccupancySpec("001", "Topmodel", NATIONAL, ITA, "FASHION", "WOMEN", ld(2010, 7, 15), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.RonRondelliIt,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
    ),
    Oxf123("OXF-123", null, "123 Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2017, 1, 1), ld(2026, 12, 31),
            new OccupancySpec[] {
                    new OccupancySpec("001", "123", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2005, 10, 28), ld(2017, 4, 16), null, null, null),
                    new OccupancySpec("002", "123", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2017, 4, 17), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    Oxfmin2("OXF-MIN2", null, "minute Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2014, 6, 1), ld(2024, 5, 31),
            new OccupancySpec[] {
                    new OccupancySpec("001", "minute", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2014, 6, 1), null, null, null, null),
                    new OccupancySpec("002", "minute", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2014, 6, 1), null, null, null, null)
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    Oxfmin3("OXF-MIN3", null, "minute Lease next", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2014, 6, 1), ld(2024, 5, 31),
            new OccupancySpec[] {
                    new OccupancySpec("001", "minute", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2019, 8, 12), null, null, null, null),
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    Oxfriu("OXF-RIU", null, "riu Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2011, 1, 16), ld(2021, 1, 15),
            new OccupancySpec[] {
                    new OccupancySpec("008", "riu", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2016, 11, 10), ld(2017,8,31), null, null, null),
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    OxfLek("OXF-LEK", null, "lek Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(1997, 9, 25), ld(2008, 9, 30),
            new OccupancySpec[] {
                    new OccupancySpec("009", "riu", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(1997, 9, 25), ld(2008,9,30), null, null, null),
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    OxfLek1("OXF-LEK1", null, "lek Lease 1", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2008, 9, 22), ld(2013, 12, 31),
            new OccupancySpec[] {
                    new OccupancySpec("009", "riu", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2008, 9, 22), ld(2013,12,31), null, null, null),
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    OxfLek2("OXF-LEK2", null, "lek Lease 2", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2014, 1, 1), ld(2021, 12, 31),
            new OccupancySpec[] {
                    new OccupancySpec("009", "riu", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2014, 1, 1), ld(2019,1,29), null, null, null),
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    OxfLif("OXF-LIF", null, "lif Lease", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2010, 8, 1), ld(2016, 4, 10),
            new OccupancySpec[] {
                    new OccupancySpec("010", "riu", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2010, 8, 1), ld(2016,4,10), null, null, null),
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    OxfLif1("OXF-LIF1", null, "lif Lease 1", PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            OrganisationAndComms_enum.AcmeNl, Organisation_enum.PoisonNl,
            ld(2016, 4, 11), ld(2022, 6, 30),
            new OccupancySpec[] {
                    new OccupancySpec("010", "riu", INTERNATIONAL, NLD, "HEALT&BEAUTY", "PERFUMERIE", ld(2016, 4, 11), null, null, null, null),
            },
            new AmendmentSpec[]{
            },
            Person_enum.JohnDoeNl,
            InvoiceAddressCreationPolicy.CREATE, AddressesCreationPolicy.DONT_CREATE),
    LuwTopModel001Be(
            "LUW-TOPMODEL-001", null, "Topmodel Lease", PropertyAndUnitsAndOwnerAndManager_enum.LuwBe,
            OrganisationAndComms_enum.HelloWorldBe, Organisation_enum.TopModelBe,
            ld(2010, 7, 15), ld(2022, 7, 14),
            new OccupancySpec[] {
                    new OccupancySpec("001", "Topmodel", NATIONAL, BEL, "FASHION", "WOMEN", ld(2010, 7, 15), null, new BigDecimal("200.25"), null, null)
            },
            new AmendmentSpec[]{
                    new AmendmentSpec(LeaseAmendmentTemplate.DEMO_TYPE)
            },
            Person_enum.GinoVannelliBe,
            InvoiceAddressCreationPolicy.DONT_CREATE, AddressesCreationPolicy.DONT_CREATE
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
        BigDecimal salesAreaNonFood;
        BigDecimal salesAreaFood;
        BigDecimal foodAndBeveragesArea;
    }

    @AllArgsConstructor
    @Data
    public static class AmendmentSpec {
        LeaseAmendmentTemplate leaseAmendmentTemplate;
    }

    private final String ref;
    private final String externalRef;
    private final String name;
    private final PropertyAndUnitsAndOwnerAndManager_enum propertyAndUnits_d;
    private final OrganisationAndComms_enum landlord_d;
    private final Organisation_enum tenant_d;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private final OccupancySpec[] occupancySpecs;
    private final AmendmentSpec[] amendmentSpecs;

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
                .setPrereq((f,ec) -> {
                    // ensure corresponding numerator exists
                    Arrays.stream(PropertyOwnerNumerator_enum.values())
                            .filter(x -> x.getPropertyAndUnitsAndOwnerAndManager_d().getProperty_d() == propertyAndUnits_d.getProperty_d())
                            .forEach(x -> f.objectFor(x, ec));

                    f.setProperty(f.objectFor(propertyAndUnits_d.getProperty_d(), ec));
                })
                .setExternalRef(externalRef)
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
                                            x.sector, x.activity, x.startDate, x.endDate,
                                            x.salesAreaNonFood, x.salesAreaFood, x.foodAndBeveragesArea)
                                    )
                                    .collect(Collectors.toList()));
                })
                .setPrereq((f, ec) -> {
                    f.setAmendmentSpecs(
                            Arrays.stream(Lease_enum.this.getAmendmentSpecs())
                                    .map(x -> new LeaseBuilder.AmendmentSpec(
                                            x.leaseAmendmentTemplate)
                                    )
                                    .collect(Collectors.toList()));
                })
                .setPrereq((f,ec) -> f.setManager(f.objectFor(manager_d, ec)))

                .setInvoiceAddressCreationPolicy(invoiceAddressCreationPolicy)
                .setAddressesCreationPolicy(addressesCreationPolicy);
    }


}
