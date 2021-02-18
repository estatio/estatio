package org.estatio.module.capex.dom.invoice.approval;

import java.math.BigInteger;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.party.dom.Organisation;

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

    @Test
    public void isInvoiceForExternalCenterManager_works() throws Exception {

        // given
        IncomingInvoice invoice = new IncomingInvoice();
        // when, then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)).isFalse();

        // when
        Property property = new Property();
        invoice.setProperty(property);
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)).isFalse();

        // when
        property.setReference("FAB");
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)).isFalse();

        // when
        IncomingInvoiceItem itemWithoutProject = new IncomingInvoiceItem();
        itemWithoutProject.setSequence(BigInteger.valueOf(1));
        invoice.getItems().add(itemWithoutProject);
        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)).isFalse();

        // when
        Project nonQualifyingproject = new Project();
        nonQualifyingproject.setReference(IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.get("COL"));
        IncomingInvoiceItem itemWithNonQualifyingProject = new IncomingInvoiceItem();
        itemWithNonQualifyingProject.setInvoice(invoice);
        itemWithNonQualifyingProject.setSequence(BigInteger.valueOf(2));
        itemWithNonQualifyingProject.setProject(nonQualifyingproject);
        invoice.getItems().add(itemWithNonQualifyingProject);

        // then
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)).isFalse();

        // when
        Project qualifyingproject = new Project();
        qualifyingproject.setReference(IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.get("FAB"));
        IncomingInvoiceItem itemWithQualifyingProject = new IncomingInvoiceItem();
        itemWithQualifyingProject.setInvoice(invoice);
        itemWithQualifyingProject.setSequence(BigInteger.valueOf(3));
        itemWithQualifyingProject.setProject(qualifyingproject);
        invoice.getItems().add(itemWithQualifyingProject);

        // then (finally all conditions met
        Assertions.assertThat(IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)).isTrue();

    }
}