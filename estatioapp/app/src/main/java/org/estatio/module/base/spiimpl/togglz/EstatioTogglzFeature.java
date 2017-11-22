package org.estatio.module.base.spiimpl.togglz;

import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum EstatioTogglzFeature implements org.togglz.core.Feature {

    @Label("whether invoice calculation should use multi-select of LeaseItemType (rather than InvoiceCalculationSelection)")
    @EnabledByDefault
    invoiceCalculationMultiSelect,

    @Label("whether 'approve by proxy' is enabled (meaning that the task need not be assigned to current user)")
    ApproveByProxy;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
