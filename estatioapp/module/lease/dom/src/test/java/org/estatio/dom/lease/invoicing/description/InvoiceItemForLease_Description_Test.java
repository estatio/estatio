package org.estatio.dom.lease.invoicing.description;

import com.google.common.collect.ImmutableMap;

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

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.lease.fixture.seed.DocFragmentData;

public class InvoiceItemForLease_Description_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);


    InvoiceItemForLease item;
    Charge charge;
    ChargeGroup chargeGroup;

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

        charge = new Charge();
        charge.setDescription("Charge description");
        chargeGroup = new ChargeGroup();
        charge.setGroup(chargeGroup);

        templateName = "Template name";

        item = new InvoiceItemForLease();
        item.setCharge(charge);
    }

    public static class ITA_Test extends InvoiceItemForLease_Description_Test {

        @Before
        public void setUp() throws Exception {
            super.setUp();

            templateText = DocFragmentData.read("InvoiceItem_description_ITA.docFragment.txt");
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
            Assertions.assertThat(rendered).isEqualTo("Conguaglio: Charge description dal 01-Apr-2017 al 15-Oct-2017");
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
            Assertions.assertThat(rendered).isEqualTo("Adeguamento: Charge description dal 01-Apr-2017 al 15-Oct-2017");
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
            Assertions.assertThat(rendered).isEqualTo("Adeguamento: Charge description dal 01-Apr-2017 al 15-Oct-2017");
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
            Assertions.assertThat(rendered).isEqualTo("Charge description dal 01-Apr-2017 al 15-Oct-2017");
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
    }


    public static class FRA_Test extends InvoiceItemForLease_Description_Test {

        @Before
        public void setUp() throws Exception {
            super.setUp();

            templateText = DocFragmentData.read("InvoiceItem_description_FRA.docFragment.txt");
        }

        @Test
        public void chargeGroup_is_FR_S_and_charge_in_range_with_startDate() throws Exception {

            // given
            item.setAdjustment(true);
            chargeGroup.setReference("FR-S");
            charge.setReference("4000");
            item.setStartDate(new LocalDate(2017,3,1));

            item.setEffectiveStartDate(new LocalDate(2017,4,1));
            item.setEffectiveEndDate(new LocalDate(2017,10,15));

            // when
            String rendered = freeMarkerService.render(templateName, templateText, item);

            // then
            Assertions.assertThat(rendered).isEqualTo("Provision de Charges HT du 01-Apr-2017 au 15-Oct-2017");


            // given
            charge.setReference("4599");

            // when
            rendered = freeMarkerService.render(templateName, templateText, item);

            // then
            Assertions.assertThat(rendered).isEqualTo("Provision de Charges HT du 01-Apr-2017 au 15-Oct-2017");

        }

        @Test
        public void chargeGroup_is_FR_S_but_charge_not_in_range_with_startDate() throws Exception {

            // given
            item.setAdjustment(true);
            chargeGroup.setReference("FR-S");
            charge.setReference("3999");
            item.setStartDate(new LocalDate(2017,3,1));

            item.setEffectiveStartDate(new LocalDate(2017,4,1));
            item.setEffectiveEndDate(new LocalDate(2017,10,15));

            // when
            String rendered = freeMarkerService.render(templateName, templateText, item);

            // then
            Assertions.assertThat(rendered).isEqualTo("Charge description du 01-Apr-2017 au 15-Oct-2017");


            // given
            charge.setReference("4600");

            // when
            rendered = freeMarkerService.render(templateName, templateText, item);

            // then
            Assertions.assertThat(rendered).isEqualTo("Charge description du 01-Apr-2017 au 15-Oct-2017");
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
            Assertions.assertThat(rendered).isEqualTo("Charge description du 01-Apr-2017 au 15-Oct-2017");
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