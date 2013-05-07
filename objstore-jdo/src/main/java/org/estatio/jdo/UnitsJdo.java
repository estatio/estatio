package org.estatio.jdo;

import java.util.List;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.query.QueryDefault;

public class UnitsJdo extends Units {

    @Override
    public List<Unit> findUnitsByReference(final String reference) {
        return allMatches(queryForFindByReference(reference));
    }

    @Override
    public Unit findUnitByReference(final String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    private static QueryDefault<Unit> queryForFindByReference(String reference) {
        return new QueryDefault<Unit>(Unit.class, "units_findUnitsByReference", "r", StringUtils.wildcardToRegex(reference));
    }
}
