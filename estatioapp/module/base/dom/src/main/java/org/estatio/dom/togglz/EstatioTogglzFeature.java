package org.estatio.dom.togglz;

import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum EstatioTogglzFeature implements org.togglz.core.Feature {

    @Label("whether invoice calculation should use multi-select of LeaseItemType (rather than InvoiceCalculationSelection)")
    @EnabledByDefault
    invoiceCalculationMultiSelect;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
