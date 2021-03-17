package org.estatio.module.capex.dom.items;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.tax.dom.Tax;

public interface FinancialItem {

    /**
     * From which we can infer the rolledUp costs and the remaining; but this probably won't be a programmatic
     * responsibility of the item, rather we'll use the database (eg CTE queries) to answer.
     */
    BigDecimal value();

    /**
     * Mandatory
     */
    FinancialItemType getType();
    /**
     * Mandatory
     */
    Charge getCharge();

    LocalDate getStartDate();

    LocalDate getEndDate();

    /**
     * Optional
     */
    Tax getTax();
    /**
     * Optional
     */
    FixedAsset<?> getFixedAsset();
    /**
     * Optional
     */
    Project getProject();
}
