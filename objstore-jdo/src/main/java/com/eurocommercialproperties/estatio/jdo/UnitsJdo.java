package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;

import org.apache.isis.applib.query.QueryDefault;

public class UnitsJdo extends Units {

    public Unit findByReference(final String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    private static QueryDefault<Unit> queryForFindByReference(String reference) {
        return new QueryDefault<Unit>(Unit.class, "units_findUnitByReference", "r", matches(reference));
    }

    private static String matches(final String reference) {
        return ".*" + reference.toUpperCase() + ".*";
    }

}
