package org.estatio.module.coda.contributions.codastatus;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

public class StatusMessageSummaryCacheTest {

    @Test
    public void findFor() {

        // given
        StatusMessageSummaryCache service = new StatusMessageSummaryCache(){
            Long idFor(final Object object) {
                return null;
            };
        };
        InvoiceForLease invoice = new InvoiceForLease();

        // when // then
        Assertions.assertThat(service.findFor(invoice)).isNull();

    }
}