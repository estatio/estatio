package org.estatio.dom.lease.invoicing;

import java.util.Arrays;
import java.util.List;

import org.estatio.dom.lease.LeaseItemType;
import org.incode.module.base.dom.utils.StringUtils;

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
    ALL_ITEMS(
            LeaseItemType.RENT,
            LeaseItemType.RENT_DISCOUNT,
            LeaseItemType.RENT_FIXED,
            LeaseItemType.SERVICE_CHARGE,
            LeaseItemType.SERVICE_CHARGE_INDEXABLE,
            LeaseItemType.TURNOVER_RENT,
            LeaseItemType.RENTAL_FEE,
            LeaseItemType.TAX,
            LeaseItemType.RENT_DISCOUNT_FIXED,
            LeaseItemType.ENTRY_FEE,
            LeaseItemType.DEPOSIT),
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
    ONLY_RENTAL_FEE(
            LeaseItemType.RENTAL_FEE),
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
