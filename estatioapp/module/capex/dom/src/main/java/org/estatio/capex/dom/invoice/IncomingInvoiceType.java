package org.estatio.capex.dom.invoice;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.asset.Property;

public enum IncomingInvoiceType {

    LEGAL,
    CAPEX,
    PROPERTY_EXPENSES,
    /**
     * These are a sub-category of property expenses, being those that can be re-charged onto tenants.
     */
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

    public boolean isToCompleteByPropertyManagers() {
        return this == LEGAL || this == CAPEX || this == PROPERTY_EXPENSES || this == SERVICE_CHARGES;
    }

    public boolean isToCompleteByOfficeAdministrator() {
        return this == LOCAL_EXPENSES;
    }

    public boolean isToCompleteByCorporateAdministrator() {
        return this == CORPORATE_EXPENSES;
    }

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
