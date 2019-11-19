package org.estatio.module.capex.subscriptions;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class IncomingInvoiceAttachPaperclipSubscriberTest {

    @Test
    public void deriveBarcodeFromDocumentName() {

        // given
        IncomingInvoiceAttachPaperclipSubscriber subscriber = new IncomingInvoiceAttachPaperclipSubscriber();

        // when, then
        // normal stuff
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("S123456789.PDF")).isEqualTo("S123456789");
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("S123456789.pdf")).isEqualTo("S123456789");
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("S123456789.pDf")).isEqualTo("S123456789");
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("123456789.pDf")).isEqualTo("123456789");

        // weird stuff
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("123456789.xml")).isEqualTo("123456789.xml");
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("123456789..pDf")).isEqualTo("123456789.");
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("123456789..pDfX")).isEqualTo("123456789.X");
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("S123456789.pD")).isEqualTo("S123456789.pD");
        Assertions.assertThat(subscriber.deriveBarcodeFromDocumentName("S123456789pDf")).isEqualTo("S123456789pDf");

    }
}