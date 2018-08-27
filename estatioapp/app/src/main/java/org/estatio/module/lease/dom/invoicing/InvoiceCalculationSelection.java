package org.estatio.module.lease.dom.invoicing;

import java.util.Arrays;
import java.util.List;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.module.lease.dom.LeaseItemType;

public enum InvoiceCalculationSelection {
    ALL_RENT_AND_SERVICE_CHARGE(
            LeaseItemType.RENT,
            LeaseItemType.RENT_DISCOUNT,
            LeaseItemType.RENT_FIXED,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE),
    ALL_RENT(
            LeaseItemType.RENT,
            LeaseItemType.RENT_FIXED,
            LeaseItemType.RENT_DISCOUNT),
    ALL_SERVICE_CHARGE(
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE),
    ALL_DISCOUNT(
            LeaseItemType.RENT_DISCOUNT_FIXED,
            LeaseItemType.RENT_DISCOUNT),
    DEFAULT_ITEMS(
            LeaseItemType.RENT,
            LeaseItemType.RENT_FIXED,
            LeaseItemType.RENT_DISCOUNT,
            LeaseItemType.RENT_DISCOUNT_FIXED,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE,
            LeaseItemType.SERVICE_CHARGE_DISCOUNT_FIXED,
            LeaseItemType.DEPOSIT,
            LeaseItemType.TAX,
            LeaseItemType.MARKETING,
            LeaseItemType.PROPERTY_TAX
    ),
    ALL_ITEMS(
            LeaseItemType.RENT,
            LeaseItemType.RENT_DISCOUNT,
            LeaseItemType.RENT_FIXED,
            LeaseItemType.RENT_DISCOUNT_FIXED,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_BUDGETED,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE,
            LeaseItemType.SERVICE_CHARGE_DISCOUNT_FIXED,
            LeaseItemType.TAX,
            LeaseItemType.TURNOVER_RENT,
            LeaseItemType.ENTRY_FEE,
            LeaseItemType.DEPOSIT,
            LeaseItemType.MARKETING,
            LeaseItemType.PROPERTY_TAX
    ),
    ONLY_RENT(
            LeaseItemType.RENT),
    ONLY_RENT_DISCOUNT(
            LeaseItemType.RENT_DISCOUNT),
    ONLY_RENT_FIXED(
            LeaseItemType.RENT_FIXED),
    ONLY_TURNOVER_RENT(
            LeaseItemType.TURNOVER_RENT),
    ONLY_SERVICE_CHARGE(
            LeaseItemType.SERVICE_CHARGE),
    ONLY_SERVICE_CHARGE_INDEXABLE(
            LeaseItemType.SERVICE_CHARGE),
    ONLY_DISCOUNT(
            LeaseItemType.RENT_DISCOUNT_FIXED),
    ONLY_ENTRY_FEE(
            LeaseItemType.ENTRY_FEE),
    ONLY_DEPOSIT(
            LeaseItemType.DEPOSIT),
    ONLY_TAX(
            LeaseItemType.TAX)
    ;

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
