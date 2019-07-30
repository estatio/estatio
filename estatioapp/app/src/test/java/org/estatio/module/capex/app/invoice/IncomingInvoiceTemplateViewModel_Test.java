package org.estatio.module.capex.app.invoice;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.party.dom.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceTemplateViewModel_Test {

    IncomingInvoiceTemplateViewModel template;

    @Before
    public void setUp() throws Exception {
        template = new IncomingInvoiceTemplateViewModel();

        Organisation supplier = new Organisation();
        supplier.setName("Incode");
        BigDecimal netAmount = BigDecimal.valueOf(999.95);

        template.setSupplier(supplier);
        template.setNetAmount(netAmount);
        template.setType(IncomingInvoiceType.CORPORATE_EXPENSES);
    }

    @Test
    public void title_when_property() throws Exception {
        // given
        Property property = new Property();
        property.setName("Centre Commercial");
        template.setProperty(property);

        // when
        final String title = template.title();

        // then
        assertThat(title).isEqualTo("Incode: CORPORATE_EXPENSES/Centre Commercial, 999.95");
    }

    @Test
    public void title_when_no_property() throws Exception {
        // when
        final String title = template.title();

        // then
        assertThat(title).isEqualTo("Incode: CORPORATE_EXPENSES, 999.95");
    }

}