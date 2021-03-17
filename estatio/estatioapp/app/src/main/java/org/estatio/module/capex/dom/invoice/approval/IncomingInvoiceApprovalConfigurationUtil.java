package org.estatio.module.capex.dom.invoice.approval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.invoice.dom.InvoiceItem;

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

    public static String CHARGE_REF_MARKETING_NR = "MARKETING EXPENSES (NR)";

    // ECP-1346
    public static final Map<String, String> PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP = ImmutableMap.of(
            "COL", "ITPR285",
            "GIG", "ITPR286",
            "FAB", "ITPR287",
            "RON", "TESTEXT"  // for intergration tests
    );

    // INCSUP-730
    public static List<String> propertyRefsWithExternalAssetManager = Arrays.asList("FIO");


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

    // ECP-1346
    public static boolean isInvoiceForExternalCenterManager(final IncomingInvoice incomingInvoice){

        final Property propertyOnInvoice = incomingInvoice.getProperty();
        if (propertyOnInvoice ==null) return false;

        final List<String> propertyRefsFound = new ArrayList<>();
        PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.forEach((k,v)->{
            if (k.equals(propertyOnInvoice.getReference()) && atLeastOneItemHasProjectWithReference(incomingInvoice, v)) propertyRefsFound.add(k);
        });

        return !propertyRefsFound.isEmpty();
    }

    private static boolean atLeastOneItemHasProjectWithReference(final IncomingInvoice invoice, final String projectRefExt) {
        for (InvoiceItem ii : invoice.getItems()){
            IncomingInvoiceItem cii = (IncomingInvoiceItem) ii;
            if (cii.getProject()!=null && cii.getProject().getReference().equals(projectRefExt)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasItemWithChargeMarketingNR(final IncomingInvoice incomingInvoice) {
        for (InvoiceItem ii : incomingInvoice.getItems()){
            if (ii.getCharge()!=null && ii.getCharge().getReference().equals(CHARGE_REF_MARKETING_NR)) return true;
        }
        return false;
    }
}
