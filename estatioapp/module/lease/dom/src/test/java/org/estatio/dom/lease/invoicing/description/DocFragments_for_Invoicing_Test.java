package org.estatio.dom.lease.invoicing.description;

import java.util.Arrays;
import java.util.SortedSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import org.assertj.core.api.Assertions;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.unittestsupport.dom.reflect.ReflectUtils;

import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.invoicing.InvoiceForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.lease.fixture.seed.DocFragmentData;

public class DocFragments_for_Invoicing_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);


    FakeDataService fake;

    FreeMarkerService freeMarkerService;
    ClockService clockService;

    @JUnitRuleMockery2.Ignoring
    @Mock
    ConfigurationService mockConfigurationService;

    String templateName;
    String templateText;

    @Before
    public void setUp() throws Exception {

        clockService = new ClockService();
        fake = new FakeDataService();
        ReflectUtils.inject(fake, clockService);
        fake.init();

        freeMarkerService = new FreeMarkerService();
        ReflectUtils.inject(freeMarkerService, "configurationService",  mockConfigurationService);
        final ImmutableMap<String, String> props = ImmutableMap.of();
        freeMarkerService.init(props);


        templateName = "Template name";
    }

    public static class InvoiceItem_Test extends DocFragments_for_Invoicing_Test {

        InvoiceItemForLease item;

        Charge charge;
        ChargeGroup chargeGroup;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            charge = new Charge();
            charge.setDescription("Charge description");
            chargeGroup = new ChargeGroup();
            charge.setGroup(chargeGroup);

            item = new InvoiceItemForLease();
            item.setCharge(charge);

        }

        public static class ITA_Test extends InvoiceItem_Test {

            @Before
            public void setUp() throws Exception {
                super.setUp();
                templateText = DocFragmentData.read("InvoiceItem_description_ITA.docFragment.ftl");
            }

            @Test
            public void adjustment_and_chargeGroupOfS_and_dueDate_after_startDate() throws Exception {

                // given

                final LocalDate itemDueDate = new LocalDate(2017,3,1);

                item.setAdjustment(true);
                chargeGroup.setReference("S");
                item.setDueDate(itemDueDate);
                item.setStartDate(itemDueDate.minusDays(1));

                item.setEffectiveStartDate(new LocalDate(2017,4,1));
                item.setEffectiveEndDate(new LocalDate(2017,10,15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Conguaglio: Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Test
            public void adjustment_and_chargeGroupOfS_but_dueDate_not_after_startDate() throws Exception {

                // given
                final LocalDate itemDueDate = new LocalDate(2017,3,1);

                item.setAdjustment(true);
                chargeGroup.setReference("S");
                item.setDueDate(itemDueDate);
                item.setStartDate(itemDueDate);

                item.setEffectiveStartDate(new LocalDate(2017,4,1));
                item.setEffectiveEndDate(new LocalDate(2017,10,15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Adeguamento: Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Test
            public void adjustment_but_not_chargeGroupOfS_and_startDate() throws Exception {

                // given
                final LocalDate itemDueDate = new LocalDate(2017,3,1);

                item.setAdjustment(true);
                chargeGroup.setReference("X");
                item.setStartDate(itemDueDate.minusDays(1));

                item.setEffectiveStartDate(new LocalDate(2017,4,1));
                item.setEffectiveEndDate(new LocalDate(2017,10,15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Adeguamento: Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Test
            public void adjustment_but_not_chargeGroupOfS_and_no_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("X");

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Adeguamento: Charge description");
            }

            @Test
            public void no_adjustment_with_startDate() throws Exception {

                // given
                final LocalDate itemDueDate = new LocalDate(2017,3,1);

                item.setAdjustment(false);
                item.setStartDate(itemDueDate.minusDays(1));

                item.setEffectiveStartDate(new LocalDate(2017,4,1));
                item.setEffectiveEndDate(new LocalDate(2017,10,15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Test
            public void no_adjustment_no_startDate() throws Exception {

                // given
                item.setAdjustment(false);

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description");
            }

            @Test
            public void null_adjustment_no_startDate() throws Exception {

                // given
                item.setAdjustment(null);

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description");
            }
        }


        public static class FRA_Test extends InvoiceItem_Test {

            @Before
            public void setUp() throws Exception {
                super.setUp();
                templateText = DocFragmentData.read("InvoiceItem_description_FRA.docFragment.ftl");
            }

            @Test
            public void chargeGroup_is_FR_S_and_charge_in_range_with_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("FR-S");
                charge.setReference("FR4000");
                item.setStartDate(new LocalDate(2017,3,1));

                item.setEffectiveStartDate(new LocalDate(2017,4,1));
                item.setEffectiveEndDate(new LocalDate(2017,10,15));

                // when
                String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Provision de Charges HT du 01-04-2017 au 15-10-2017");


                // given
                charge.setReference("FR4599");

                // when
                rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Provision de Charges HT du 01-04-2017 au 15-10-2017");

            }

            @Test
            public void chargeGroup_is_FR_S_but_charge_not_in_range_with_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("FR-S");
                charge.setReference("FR3999");
                item.setStartDate(new LocalDate(2017,3,1));

                item.setEffectiveStartDate(new LocalDate(2017,4,1));
                item.setEffectiveEndDate(new LocalDate(2017,10,15));

                // when
                String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description du 01-04-2017 au 15-10-2017");


                // given
                charge.setReference("FR4600");

                // when
                rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description du 01-04-2017 au 15-10-2017");
            }

            @Test
            public void chargeGroup_not_FR_S_with_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("XXXX");
                item.setStartDate(new LocalDate(2017,3,1));

                item.setEffectiveStartDate(new LocalDate(2017,4,1));
                item.setEffectiveEndDate(new LocalDate(2017,10,15));

                // when
                String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description du 01-04-2017 au 15-10-2017");
            }

            @Test
            public void chargeGroup_not_FR_S_with_no_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("XXXX");

                // when
                String rendered = freeMarkerService.render(templateName, templateText, item);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description");
            }

        }

    }

    public static class Invoice_Test extends DocFragments_for_Invoicing_Test {

        LocalDate tenancyStartDate;
        Lease lease;
        LeaseType leaseType;

        FixedAssetForTesting fixedAsset;

        SortedSet<Occupancy> occupancies;
        Occupancy occupancy;
        Brand brand;

        InvoiceForLease invoice;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            fixedAsset = new FixedAssetForTesting();

            tenancyStartDate = new LocalDate(2017, 4, 1);

            lease = new Lease();
            leaseType = new LeaseType();
            lease.setLeaseType(leaseType);
            lease.setTenancyStartDate(tenancyStartDate);

            occupancy = new Occupancy();
            occupancies = Sets.newTreeSet();
            occupancies.add(occupancy);

            brand = new Brand();
            brand.setName("Some brand");
            // have deliberately NOT set brand on occupancy

            invoice = new InvoiceForLease();
            invoice.setLease(lease);
        }

        public static class ITA_Test extends Invoice_Test {


            @Before
            public void setUp() throws Exception {
                super.setUp();

                templateText = DocFragmentData.read("Invoice_description_ITA.docFragment.ftl");
            }

            @Test
            public void when_affitto_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("AD", "OA", "PA", "SA")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(String.format("Contratto di affitto di ramo d'azienda con effetto dal 01-04-2017%n%n"));

                }
            }

            @Test
            public void when_commodato_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("CO", "CG")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(String.format("Contratto di commodato con effetto dal 01-04-2017%n%n"));

                }
            }

            @Test
            public void when_concessione_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("DH", "PP")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(String.format("Contratto di concessione con effetto dal 01-04-2017%n%n"));

                }
            }

            @Test
            public void when_locazione_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("LO", "OL", "PL", "SL", "AA")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(String.format("Contratto di locazione con effetto dal 01-04-2017%n%n"));

                }
            }

            @Test
            public void when_spazio_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("PR")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(String.format("Contratto di locazione di spazio con effetto dal 01-04-2017%n%n"));

                }
            }

            @Test
            public void when_fixed_asset_commerciale_with_no_brand() throws Exception {

                // given
                invoice.setFixedAsset(fixedAsset);

                for (String leaseType : Arrays.asList("AD", "OA", "PA", "SA")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).endsWith(String.format("con effetto dal 01-04-2017 - Esercizio Commerciale%n%n"));
                }
            }

            @Test
            public void when_fixed_asset_unita_with_no_brand() throws Exception {

                // given
                invoice.setFixedAsset(fixedAsset);

                for (String leaseType : Arrays.asList("CG", "CO", "LO", "PR", "OL", "PL", "SL")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).endsWith(String.format("con effetto dal 01-04-2017 - Unità%n%n"));
                }
            }

            @Test
            public void when_fixed_asset_spazio_with_no_brand() throws Exception {

                // given
                invoice.setFixedAsset(fixedAsset);

                for (String leaseType : Arrays.asList("DH")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).endsWith(String.format("con effetto dal 01-04-2017 - Spazio Commerciale%n%n"));
                }
            }

            @Test
            public void when_has_occupancy_but_no_brand_and_no_fixed_asset() throws Exception {

                // given
                lease.setOccupancies(occupancies);

                for (String leaseType : Arrays.asList("DH")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(String.format("Contratto di concessione con effetto dal 01-04-2017%n%n"));
                }
            }

            @Test
            public void when_has_occupancy_with_brand_but_no_fixed_asset() throws Exception {

                // given
                occupancy.setBrand(brand);
                lease.setOccupancies(occupancies);

                for (String leaseType : Arrays.asList("DH")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, invoice);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(String.format("Contratto di concessione Some brand con effetto dal 01-04-2017%n%n"));
                }
            }

        }

    }


}