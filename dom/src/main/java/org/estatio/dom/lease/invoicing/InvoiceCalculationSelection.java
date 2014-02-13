package org.estatio.dom.lease.invoicing;

import java.util.Arrays;

import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.utils.StringUtils;

public enum InvoiceCalculationSelection {
    RENT_AND_SERVICE_CHARGE(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
    TURNOVER_RENT(LeaseItemType.TURNOVER_RENT),
    TAX(LeaseItemType.TAX),
    RENT(LeaseItemType.RENT),
    SERVICE_CHARGE(LeaseItemType.SERVICE_CHARGE);

    private LeaseItemType[] selectedTypes;

    private InvoiceCalculationSelection(LeaseItemType... selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public LeaseItemType[] selectedTypes() {
        return selectedTypes;
    }

    public boolean contains(LeaseItemType leaseItemType) {
        return Arrays.asList(selectedTypes).contains(leaseItemType);
    }
    
    public String title() {
        return StringUtils.enumTitle(this.name());
    }
}
