package org.estatio.capex.dom.items;

import java.math.BigDecimal;

import org.estatio.capex.dom.time.CalendarType;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.time.TimeInterval;
import org.estatio.dom.project.Project;
import org.estatio.dom.tax.Tax;

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
    IncomingCharge getCharge();

    TimeInterval getPeriod();

    /**
     * Whether the {@link #getPeriod()} is a financial or natural calendar period.
     */
    CalendarType getCalendarType();

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
