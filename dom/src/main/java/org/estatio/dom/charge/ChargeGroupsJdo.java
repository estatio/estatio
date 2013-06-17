package org.estatio.dom.charge;


import org.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

@Named("ChargeGroups")
public class ChargeGroupsJdo extends ChargeGroups {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public ChargeGroup findChargeGroupByReference(@Named("Reference") String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch(queryForFindChargeGroupByReference(rexeg));
    }
    
    private static QueryDefault<ChargeGroup> queryForFindChargeGroupByReference(String pattern) {
        return new QueryDefault<ChargeGroup>(ChargeGroup.class, "charge_findChargeByReference", "r", pattern);
    }

}
