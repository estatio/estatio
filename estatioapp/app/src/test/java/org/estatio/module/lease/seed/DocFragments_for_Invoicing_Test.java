package org.estatio.module.lease.seed;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import org.assertj.core.api.Assertions;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.unittestsupport.dom.reflect.ReflectUtils;

import org.estatio.module.asset.dom.FixedAssetForTesting;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.invoicing.ssrs.InvoiceAttributesVM;
import org.estatio.module.lease.dom.invoicing.ssrs.InvoiceItemAttributesVM;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.party.dom.PartyForTesting;

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
        ReflectUtils.inject(freeMarkerService, "configurationService", mockConfigurationService);
        final ImmutableMap<String, String> props = ImmutableMap.of();
        freeMarkerService.init(props);

        templateName = "Template name";
    }

    public static class InvoiceItem_Description_Test extends DocFragments_for_Invoicing_Test {

        InvoiceItemForLease item;

        Charge charge;
        ChargeGroup chargeGroup;

        InvoiceItemAttributesVM vm;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            charge = new Charge();
            charge.setDescription("Charge description");
            chargeGroup = new ChargeGroup();
            charge.setGroup(chargeGroup);

            item = new InvoiceItemForLease();
            item.setCharge(charge);

            vm = new InvoiceItemAttributesVM(item);
        }

        public static class ITA_Test extends InvoiceItem_Description_Test {

            @Before
            public void setUp() throws Exception {
                super.setUp();
                templateText = DocFragmentData.read("InvoiceItem_description_ITA.docFragment.ftl");
            }

            @Ignore // in EST-1197
            @Test
            public void adjustment_and_chargeGroupOfS_and_dueDate_after_startDate() throws Exception {

                // given

                final LocalDate itemDueDate = new LocalDate(2017, 3, 1);

                item.setAdjustment(true);
                chargeGroup.setReference("S");
                item.setDueDate(itemDueDate);
                item.setStartDate(itemDueDate.minusDays(1));

                item.setEffectiveStartDate(new LocalDate(2017, 4, 1));
                item.setEffectiveEndDate(new LocalDate(2017, 10, 15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered)
                        .isEqualTo("Conguaglio: Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Ignore // in EST-1197
            @Test
            public void adjustment_and_chargeGroupOfS_but_dueDate_not_after_startDate() throws Exception {

                // given
                final LocalDate itemDueDate = new LocalDate(2017, 3, 1);

                item.setAdjustment(true);
                chargeGroup.setReference("S");
                item.setDueDate(itemDueDate);
                item.setStartDate(itemDueDate);

                item.setEffectiveStartDate(new LocalDate(2017, 4, 1));
                item.setEffectiveEndDate(new LocalDate(2017, 10, 15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered)
                        .isEqualTo("Adeguamento: Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Ignore // in EST-1197
            @Test
            public void adjustment_but_not_chargeGroupOfS_and_startDate() throws Exception {

                // given
                final LocalDate itemDueDate = new LocalDate(2017, 3, 1);

                item.setAdjustment(true);
                chargeGroup.setReference("X");
                item.setStartDate(itemDueDate.minusDays(1));

                item.setEffectiveStartDate(new LocalDate(2017, 4, 1));
                item.setEffectiveEndDate(new LocalDate(2017, 10, 15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered)
                        .isEqualTo("Adeguamento: Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Ignore // in EST-1197
            @Test
            public void adjustment_but_not_chargeGroupOfS_and_no_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("X");

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Adeguamento: Charge description");
            }

            @Ignore // in EST-1197
            @Test
            public void no_adjustment_with_startDate() throws Exception {

                // given
                final LocalDate itemDueDate = new LocalDate(2017, 3, 1);

                item.setAdjustment(false);
                item.setStartDate(itemDueDate.minusDays(1));

                item.setEffectiveStartDate(new LocalDate(2017, 4, 1));
                item.setEffectiveEndDate(new LocalDate(2017, 10, 15));

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description dal 01-04-2017 al 15-10-2017");
            }

            @Ignore // in EST-1197
            @Test
            public void no_adjustment_no_startDate() throws Exception {

                // given
                item.setAdjustment(false);

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description");
            }

            @Ignore // in EST-1197
            @Test
            public void null_adjustment_no_startDate() throws Exception {

                // given
                item.setAdjustment(null);

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description");
            }
        }

        public static class FRA_Test extends InvoiceItem_Description_Test {

            @Before
            public void setUp() throws Exception {
                super.setUp();
                templateText = DocFragmentData.read("InvoiceItem_description_FRA.docFragment.ftl");
            }

            @Ignore // EST-1151
            @Test
            public void chargeGroup_is_FR_S_and_charge_in_range_with_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("FR-S");
                charge.setReference("FR4000");
                item.setStartDate(new LocalDate(2017, 3, 1));

                item.setEffectiveStartDate(new LocalDate(2017, 4, 1));
                item.setEffectiveEndDate(new LocalDate(2017, 10, 15));

                // when
                String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Provision de Charges HT du 01-04-2017 au 15-10-2017");

                // given
                charge.setReference("FR4599");

                // when
                rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Provision de Charges HT du 01-04-2017 au 15-10-2017");

            }

            @Ignore // EST-1151
            @Test
            public void chargeGroup_is_FR_S_but_charge_not_in_range_with_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("FR-S");
                charge.setReference("FR3999");
                item.setStartDate(new LocalDate(2017, 3, 1));

                item.setEffectiveStartDate(new LocalDate(2017, 4, 1));
                item.setEffectiveEndDate(new LocalDate(2017, 10, 15));

                // when
                String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description du 01-04-2017 au 15-10-2017");

                // given
                charge.setReference("FR4600");

                // when
                rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description du 01-04-2017 au 15-10-2017");
            }

            @Ignore // EST-1151
            @Test
            public void chargeGroup_not_FR_S_with_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("XXXX");
                item.setStartDate(new LocalDate(2017, 3, 1));

                item.setEffectiveStartDate(new LocalDate(2017, 4, 1));
                item.setEffectiveEndDate(new LocalDate(2017, 10, 15));

                // when
                String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description du 01-04-2017 au 15-10-2017");
            }

            @Test
            public void chargeGroup_not_FR_S_with_no_startDate() throws Exception {

                // given
                item.setAdjustment(true);
                chargeGroup.setReference("XXXX");

                // when
                String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo("Charge description");
            }

        }

    }

    public static class Invoice_Description_Test extends DocFragments_for_Invoicing_Test {

        LocalDate tenancyStartDate;
        Lease lease;
        LeaseType leaseType;

        FixedAssetForTesting fixedAsset;

        SortedSet<Occupancy> occupancies;
        Occupancy occupancy;
        Brand brand;

        InvoiceForLease invoice;
        InvoiceItemForLease invoiceItemForLease;
        Unit unit;

        InvoiceAttributesVM vm;

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


            unit = new Unit();
            unit.setName("123");

            invoiceItemForLease = new InvoiceItemForLease();
            invoiceItemForLease.setFixedAsset(unit);

            invoice = new InvoiceForLease();
            invoice.setLease(lease);

            vm = new InvoiceAttributesVM(invoice);
        }

        public static class ITA_Test extends Invoice_Description_Test {

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
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(
                            String.format("Contratto di affitto di ramo d'azienda con effetto dal 01-04-2017"));

                }
            }

            @Test
            public void when_commodato_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("CO", "CG")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered)
                            .isEqualTo(String.format("Contratto di commodato con effetto dal 01-04-2017"));

                }
            }

            @Test
            public void when_concessione_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("DH", "PP")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered)
                            .isEqualTo(String.format("Contratto di concessione con effetto dal 01-04-2017"));

                }
            }

            @Test
            public void when_locazione_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("LO", "OL", "PL", "SL", "AA")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered)
                            .isEqualTo(String.format("Contratto di locazione con effetto dal 01-04-2017"));

                }
            }

            @Test
            public void when_spazio_but_with_no_brand_and_no_fixed_asset() throws Exception {

                for (String leaseType : Arrays.asList("PR")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(
                            String.format("Contratto di locazione di spazio con effetto dal 01-04-2017"));

                }
            }

            @Test
            public void when_fixed_asset_commerciale_with_no_brand() throws Exception {

                // given
                invoice.setFixedAsset(fixedAsset);
                invoice.getItems().add(invoiceItemForLease);


                for (String leaseType : Arrays.asList("AD", "OA", "PA", "SA")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered)
                            .endsWith(String.format("con effetto dal 01-04-2017 - Esercizio Commerciale 123"));
                }
            }

            @Test
            public void when_fixed_asset_unita_with_no_brand() throws Exception {

                // given
                invoice.setFixedAsset(fixedAsset);
                invoice.getItems().add(invoiceItemForLease);


                for (String leaseType : Arrays.asList("CG", "CO", "LO", "PR", "OL", "PL", "SL")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).endsWith(String.format("con effetto dal 01-04-2017 - Unità 123"));
                }
            }

            @Test
            public void when_fixed_asset_spazio_with_no_brand() throws Exception {

                // given
                invoice.setFixedAsset(fixedAsset);
                invoice.getItems().add(invoiceItemForLease);

                for (String leaseType : Arrays.asList("DH")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered)
                            .endsWith(String.format("con effetto dal 01-04-2017 - Spazio Commerciale 123"));
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
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered)
                            .isEqualTo(String.format("Contratto di concessione con effetto dal 01-04-2017"));
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
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(
                            String.format("Contratto di concessione con effetto dal 01-04-2017 - Insegna: Some brand"));
                }
            }

            @Test
            public void when_has_occupancy_with_brand_and_unit() throws Exception {

                // given
                occupancy.setBrand(brand);
                lease.setOccupancies(occupancies);
                invoice.setFixedAsset(fixedAsset);
                invoice.getItems().add(invoiceItemForLease);


                for (String leaseType : Arrays.asList("DH")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).isEqualTo(
                            String.format("Contratto di concessione con effetto dal 01-04-2017 - Insegna: Some brand - Spazio Commerciale 123"));
                }
            }

        }

    }

    public static class Invoice_PrelimLetterDescription_Test extends DocFragments_for_Invoicing_Test {

        LocalDate tenancyStartDate;
        Lease lease;
        LeaseType leaseType;

        FixedAssetForTesting fixedAsset;

        SortedSet<Occupancy> occupancies;
        Occupancy occupancy;
        Unit unit;
        Property property;

        InvoiceForLease invoice;
        TreeSet<InvoiceItem> items;

        Charge charge1;
        Charge charge2;

        InvoiceItemForLease item1;
        InvoiceItemForLease item2;

        InvoiceAttributesVM vm;
        private PartyForTesting seller;
        private BankAccount sellerBankAccount;
        private PartyForTesting bank;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            fixedAsset = new FixedAssetForTesting();

            tenancyStartDate = new LocalDate(2017, 4, 1);

            property = new Property();
            property.setName("Some Property Name");
            unit = new Unit();
            unit.setName("Some Unit Name");
            unit.setProperty(property);

            seller = new PartyForTesting();
            seller.setName("Some Seller");

            bank = new PartyForTesting();
            bank.setName("Some Bank");

            sellerBankAccount = new BankAccount();
            sellerBankAccount.setBank(bank);
            sellerBankAccount.setIban("Some Iban");

            lease = new Lease();
            leaseType = new LeaseType();
            lease.setLeaseType(leaseType);
            lease.setTenancyStartDate(tenancyStartDate);

            occupancy = new Occupancy();
            occupancy.setUnit(unit);

            occupancies = Sets.newTreeSet();
            occupancies.add(occupancy);

            lease.setOccupancies(occupancies);

            invoice = new InvoiceForLease() {
                @Override
                public FinancialAccount getSellerBankAccount() {
                    return sellerBankAccount;
                }
            };
            invoice.setSeller(seller);

            invoice.setLease(lease);

            charge1 = new Charge();
            charge1.setReference("CHARGE-1");
            charge1.setDescription("Charge 1");
            charge2 = new Charge();
            charge2.setReference("CHARGE-2");
            charge2.setDescription("Charge 2");

            items = new TreeSet<>();
            invoice.setItems(items);

            item1 = new InvoiceItemForLease();
            item1.setCharge(charge1);
            item1.setGrossAmount(new BigDecimal("1234.56"));

            item2 = new InvoiceItemForLease();
            item2.setCharge(charge2);
            item2.setGrossAmount(new BigDecimal("6543.21"));

            items.add(item1);
            items.add(item2);

            invoice.setDueDate(new LocalDate(2016,3,12));

            vm = new InvoiceAttributesVM(invoice);
        }

        public static class ITA_Test extends Invoice_PrelimLetterDescription_Test {

            @Before
            public void setUp() throws Exception {
                super.setUp();

                templateText = DocFragmentData.read("Invoice_preliminaryLetterDescription_ITA.docFragment.ftl");
            }

            @Test
            public void when_occupancyNotV_and_leaseType_for_Esercezio() throws Exception {

                // given
                invoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
                unit.setReference("AAA"); // doesn't contain a -V

                for (String leaseType : Arrays.asList("AD", "OA", "PA", "SA")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).contains("Esercizio Commerciale");
                }
            }

            @Test
            public void when_occupancyNotV_and_leaseType_for_Unita() throws Exception {

                // given
                invoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
                unit.setReference("AAA"); // doesn't contain a -V

                for (String leaseType : Arrays.asList("CG", "CA", "LO", "PR", "OL", "PL", "SL")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).contains("Unità");
                }
            }

            @Test
            public void when_occupancy_not_V_and_leaseType_for_Spazio_Commerciale() throws Exception {

                // given
                invoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
                unit.setReference("AAA"); // doesn't contain a -V

                for (String leaseType : Arrays.asList("DH")) {

                    // given
                    this.leaseType.setReference(leaseType);

                    // when
                    final String rendered = freeMarkerService.render(templateName, templateText, vm);

                    // then
                    Assertions.assertThat(rendered).contains("Spazio Commerciale");
                }
            }

            @Test
            public void when_occupancy_is_V() throws Exception {

                // given
                invoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
                unit.setReference("-V"); // doesn't contain a -V

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).contains("Some Property Name / / Fatturazione");
            }

            @Ignore // TODO
            @Test
            public void when_frequency_is_QUARTER() throws Exception {
            }

            @Ignore // TODO
            @Test
            public void when_frequency_is_MONTH() throws Exception {
            }

            @Ignore // TODO
            @Test
            public void when_paymentMethod_is_DIRECT_DEBIT() throws Exception {
            }

            @Ignore // TODO
            @Test
            public void when_paymentMethod_is_BILLING_ACCOUNT() throws Exception {
            }

            @Ignore // TODO
            @Test
            public void when_paymentMethod_is_BANK_TRANSFER() throws Exception {
            }

            @Ignore // TODO
            @Test
            public void when_paymentMethod_is_CASH() throws Exception {
            }

            @Ignore // TODO
            @Test
            public void when_paymentMethod_is_CHEQUE() throws Exception {
            }

            @Test
            public void smoke_test() throws Exception {

                // given
                invoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
                unit.setReference("AAA"); // doesn't contain a -V
                this.leaseType.setReference("CG");

                // when
                final String rendered = freeMarkerService.render(templateName, templateText, vm);

                // then
                Assertions.assertThat(rendered).isEqualTo(
                          "<b>OGGETTO</b>: Some Property Name / Unità Some Unit Name / Fatturazione Charge 1 e Charge 2."
                        + "<br /><br />"
                        + "Come a Voi già noto, la fatturazione relativa al Charge 1 e Charge 2, verrà effettuata alla stessa data stabilita per il pagamento ."
                        + "<br /><br />"
                        + "Pertanto, Vi invitiamo a voler predisporre il pagamento a mezzo bonifico bancario sul conto corrente intestato "
                            + "alla Some Seller Some Bank - Some Iban per l'importo di <b>€ 7.777,77</b> con scadenza 12-03-2016 così suddiviso:");
            }
        }
    }
}