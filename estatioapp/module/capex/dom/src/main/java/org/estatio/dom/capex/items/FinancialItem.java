package org.estatio.dom.capex.items;

import java.math.BigDecimal;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.capex.charge.IncomingCharge;
import org.estatio.dom.capex.time.TimeInterval;
import org.estatio.dom.project.Project;
import org.estatio.dom.tax.Tax;

public interface FinancialItem {

    BigDecimal value();
    BigDecimal rolledUp();
    BigDecimal remaining();

    /**
     * Mandatory
     */
    FinancialItemType getType();
    /**
     * Mandatory
     */
    TimeInterval getTimeInterval();
    /**
     * Mandatory
     */
    IncomingCharge getIncomingCharge();

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
