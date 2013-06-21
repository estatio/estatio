package org.estatio.dom.lease;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;

/**
 * By subclassing, in effect defines the factory for the implementation of {@link Unit}.
 */
public class UnitsForLease extends Units<UnitForLease> {

    public UnitsForLease() {
        super(UnitForLease.class);
    }
}
