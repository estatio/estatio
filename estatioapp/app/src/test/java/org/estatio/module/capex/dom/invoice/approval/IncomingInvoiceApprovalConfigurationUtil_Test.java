package org.estatio.module.capex.dom.invoice.approval;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.party.dom.Organisation;

import static org.junit.Assert.*;

public class IncomingInvoiceApprovalConfigurationUtil_Test {

    @Test
    public void hasMonitoring() {
        // given
        Property property = new Property();
        IncomingInvoice invoice = new IncomingInvoice();
        invoice.setProperty(property);

        // when
        property.setReference("AZ");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasMonitoring(invoice)).isTrue();

        // and when
        property.setReference("XX");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasMonitoring(invoice)).isFalse();
    }

    @Test
    public void hasHighSingleSignatureThreshold() {
        // given
        Organisation buyer = new Organisation();
        IncomingInvoice invoice = new IncomingInvoice();
        invoice.setBuyer(buyer);

        // when
        buyer.setReference("IT07");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasHighSingleSignatureThreshold(invoice)).isTrue();

        // and when
        buyer.setReference("ITXX");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasHighSingleSignatureThreshold(invoice)).isFalse();
    }

    @Test
    public void hasAllTypesApprovedByAssetManager() {
        // given
        Organisation buyer = new Organisation();
        IncomingInvoice invoice = new IncomingInvoice();
        invoice.setBuyer(buyer);

        // when
        buyer.setReference("IT07");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasAllTypesApprovedByAssetManager(invoice)).isTrue();

        // and when
        buyer.setReference("ITXX");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasAllTypesApprovedByAssetManager(invoice)).isFalse();
    }

    @Test
    public void hasAllTypesCompletedByPropertyInvoiceManager() {
        // given
        Organisation buyer = new Organisation();
        IncomingInvoice invoice = new IncomingInvoice();
        invoice.setBuyer(buyer);

        // when
        buyer.setReference("IT04");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasAllTypesCompletedByPropertyInvoiceManager(invoice)).isTrue();

        // and when
        buyer.setReference("ITXX");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasAllTypesCompletedByPropertyInvoiceManager(invoice)).isFalse();

    }

    @Test
    public void hasRecoverableCompletedByPropertyInvoiceManager() {
        // given
        Organisation buyer = new Organisation();
        IncomingInvoice invoice = new IncomingInvoice();
        invoice.setBuyer(buyer);

        // when
        buyer.setReference("IT01");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasRecoverableCompletedByPropertyInvoiceManager(invoice)).isTrue();

        // and when
        buyer.setReference("ITXX");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.hasRecoverableCompletedByPropertyInvoiceManager(invoice)).isFalse();

    }
}