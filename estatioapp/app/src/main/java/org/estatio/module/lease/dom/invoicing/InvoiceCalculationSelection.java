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
    // NOTE: called in f.e. InvoiceServiceMenu#doDefault2CalculateInvoicesForProperty() using InvoiceCalculationSelection.values()[2];
    DEFAULT_ITEMS_ITA(
            LeaseItemType.RENT,
            LeaseItemType.SERVICE_CHARGE
    ),
    // NOTE: called in f.e. InvoiceServiceMenu#doDefault2CalculateInvoicesForProperty() using InvoiceCalculationSelection.values()[3];
    DEFAULT_ITEMS_FRA(
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
            LeaseItemType.SERVICE_CHARGE_INDEXABLE,
            LeaseItemType.SERVICE_CHARGE_DISCOUNT_FIXED,
            LeaseItemType.TAX,
            LeaseItemType.TURNOVER_RENT,
            LeaseItemType.ENTRY_FEE,
            LeaseItemType.DEPOSIT,
            LeaseItemType.MARKETING,
            LeaseItemType.PROPERTY_TAX
    ),
    ONLY_DEPOSIT(
            LeaseItemType.DEPOSIT)
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
