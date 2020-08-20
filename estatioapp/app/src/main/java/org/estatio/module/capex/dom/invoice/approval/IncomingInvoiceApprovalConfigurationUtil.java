package org.estatio.module.capex.dom.invoice.approval;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

/**
 * This class contains a bunch of static methods with "hard coded" user data for incoming invoice approval workflow configuration
 * At least it then lives in a single place ....
 */
public class IncomingInvoiceApprovalConfigurationUtil {

    private static List<String> propertyRefsWithMonitoring = Arrays.asList("AZ", "MAC"); // MAC is there for testing

    public static BigDecimal singleSignatureThresholdNormal = new BigDecimal("100000.00");

    public static BigDecimal singleSignatureThresholdHigh = new BigDecimal("150000.00");

    private static List<String> buyerRefsWithHighSingleSignatureThreshold = Arrays.asList("IT07");

    private static List<String> buyerRefsHavingAllTypesApprovedByAssetManager = Arrays.asList("IT07");

    private static List<String> buyerRefsHavingAllTypesCompletedByPropertyInvoiceManager = Arrays.asList("IT04");

    private static List<String> buyerRefsHavingRecoverableCompletedByPropertyInvoiceManager = Arrays.asList("IT01");

    // ECP-1208
    public static boolean hasMonitoring(final IncomingInvoice incomingInvoice) {
        if (incomingInvoice.getProperty()!=null && propertyRefsWithMonitoring.contains(incomingInvoice.getProperty().getReference())) return true;
        return false;
    }

    //ECP-1173
    public static boolean hasHighSingleSignatureThreshold(final IncomingInvoice incomingInvoice){
        return incomingInvoice.getBuyer()!=null && buyerRefsWithHighSingleSignatureThreshold.contains(incomingInvoice.getBuyer().getReference());
    }

    // ECP-1181
    public static boolean hasAllTypesApprovedByAssetManager(final IncomingInvoice incomingInvoice){
        return incomingInvoice.getBuyer()!=null && buyerRefsHavingAllTypesApprovedByAssetManager
                .contains(incomingInvoice.getBuyer().getReference());
    }

    public static boolean hasAllTypesCompletedByPropertyInvoiceManager(final IncomingInvoice incomingInvoice){
        return incomingInvoice.getBuyer()!=null && buyerRefsHavingAllTypesCompletedByPropertyInvoiceManager
                .contains(incomingInvoice.getBuyer().getReference());
    }

    public static boolean hasRecoverableCompletedByPropertyInvoiceManager(final IncomingInvoice incomingInvoice){
        return incomingInvoice.getBuyer()!=null && buyerRefsHavingRecoverableCompletedByPropertyInvoiceManager
                .contains(incomingInvoice.getBuyer().getReference());
    }

}
