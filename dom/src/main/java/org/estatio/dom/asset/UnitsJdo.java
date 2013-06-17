package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.lease.UnitForLease;
import org.estatio.dom.utils.StringUtils;

public class UnitsJdo extends Units {

    public UnitsJdo() {
        super(UnitForLease.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Unit> findUnitsByReference(final String reference) {
        return (List)allMatches(queryForFindByReference(reference));
    }

    @Override
    public Unit findUnitByReference(final String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    private static QueryDefault<UnitForLease> queryForFindByReference(String reference) {
        return new QueryDefault<UnitForLease>(UnitForLease.class, "units_findUnitsByReference", "r", StringUtils.wildcardToRegex(reference));
    }
}
