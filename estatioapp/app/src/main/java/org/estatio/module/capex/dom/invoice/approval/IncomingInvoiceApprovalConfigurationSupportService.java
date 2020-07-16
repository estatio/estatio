package org.estatio.module.capex.dom.invoice.approval;

import java.util.Arrays;
import java.util.List;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

/**
 * This class contains a bunch of static methods with "hard coded" user data for incoming invoice approval work-flow configuration
 * At least it then lives in a single place ....
 */
public class IncomingInvoiceApprovalConfigurationSupportService {

    private static List<String> propertyRefsWithMonitoring = Arrays.asList("AZ");

    public static boolean hasMonitoring(final IncomingInvoice incomingInvoice) {
        if (incomingInvoice.getProperty()!=null && propertyRefsWithMonitoring.contains(incomingInvoice.getProperty().getReference())) return true;
        return false;
    }

}
