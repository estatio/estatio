package org.estatio.dom.lease.invoicing;

import java.util.Arrays;
import java.util.List;

import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.utils.StringUtils;

public enum InvoiceCalculationSelection {
    RENT_AND_SERVICE_CHARGE(
            LeaseItemType.RENT,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE),
    RENT(
            LeaseItemType.RENT),
    SERVICE_CHARGE(
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE),
    TURNOVER_RENT(
            LeaseItemType.TURNOVER_RENT),
    TAX(
            LeaseItemType.TAX),
    DISCOUNT(
            LeaseItemType.DISCOUNT),
    ALL(
            LeaseItemType.RENT,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE,
            LeaseItemType.TURNOVER_RENT,
            LeaseItemType.TAX,
            LeaseItemType.DISCOUNT);

    private LeaseItemType[] selectedTypes;

    private InvoiceCalculationSelection(LeaseItemType... selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public List<LeaseItemType> selectedTypes() {
        return Arrays.asList(selectedTypes);
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }
}
