package org.estatio.capex.dom.items;

import java.math.BigDecimal;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.time.TimeInterval;
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
    IncomingCharge getIncomingCharge();

    /**
     * Optional, though either this or {@link #getFinancialTimeInterval()} are expected to be defined.
     */
    TimeInterval getNaturalTimeInterval();
    /**
     * Optional, though either this or {@link #getNaturalTimeInterval()} are expected to be defined.
     */
    TimeInterval getFinancialTimeInterval();

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
