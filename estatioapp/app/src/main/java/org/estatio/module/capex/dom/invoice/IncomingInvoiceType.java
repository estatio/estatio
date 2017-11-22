package org.estatio.module.capex.dom.invoice;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.asset.dom.Property;

public enum IncomingInvoiceType {

    CAPEX,
    PROPERTY_EXPENSES,
    SERVICE_CHARGES,
    LOCAL_EXPENSES,
    CORPORATE_EXPENSES,

    /**
     * Non-Property assets
     */
    TANGIBLE_FIXED_ASSET,
    INTERCOMPANY,
    RE_INVOICING,
    ;

    @Programmatic
    public String validateProperty(final Property property) {
        return relatesToProperty() && property == null ? "Property is required for " + this : null;
    }

    private boolean relatesToProperty() {
        return this == CAPEX || this == PROPERTY_EXPENSES || this == SERVICE_CHARGES;
    }

    public static IncomingInvoiceType parse(final String value) {
        if (value == null) {
            return CAPEX;
        }
        String trimmedLowerValue = value.trim().toUpperCase();
        try {
            return valueOf(trimmedLowerValue);
        } catch (IllegalArgumentException ex) {
            return CAPEX;
        }
    }

}
