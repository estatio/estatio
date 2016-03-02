package org.estatio.dom.lease.invoicing;

import java.util.Arrays;
import java.util.List;

import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.utils.StringUtils;

public enum InvoiceCalculationSelection {
    RENT_AND_SERVICE_CHARGE(
            LeaseItemType.RENT,
            LeaseItemType.DISCOUNT_INDEXABLE,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE),
    RENT(
            LeaseItemType.RENT,
            LeaseItemType.DISCOUNT_INDEXABLE),
    RENT_FIXED(
            LeaseItemType.RENT_FIXED
    ),
    SERVICE_CHARGE(
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE),
    TURNOVER_RENT(
            LeaseItemType.TURNOVER_RENT),
    RENTAL_FEE(
            LeaseItemType.RENTAL_FEE),
    TAX(
            LeaseItemType.TAX),
    DISCOUNT(
            LeaseItemType.DISCOUNT),
    ENTRY_FEE(
            LeaseItemType.ENTRY_FEE),
    DEPOSIT(
            LeaseItemType.DEPOSIT),
    ALL(
            LeaseItemType.RENT,
            LeaseItemType.RENT_FIXED,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE,
            LeaseItemType.TURNOVER_RENT,
            LeaseItemType.RENTAL_FEE,
            LeaseItemType.TAX,
            LeaseItemType.DISCOUNT,
            LeaseItemType.ENTRY_FEE,
            LeaseItemType.DEPOSIT,
            LeaseItemType.DISCOUNT_INDEXABLE);

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
