package org.estatio.dom.charge;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Named("ChargeGroups")
public class ChargeGroups extends EstatioDomainService {

    public ChargeGroups() {
        super(ChargeGroups.class, ChargeGroup.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public ChargeGroup newChargeGroup() {
        ChargeGroup chargeGroup = newTransientInstance(ChargeGroup.class);
        persist(chargeGroup);
        return chargeGroup;
    }
    
    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public ChargeGroup findChargeGroupByReference(@Named("Reference") String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch(queryForFindChargeGroupByReference(rexeg));
    }
    
    private static QueryDefault<ChargeGroup> queryForFindChargeGroupByReference(String pattern) {
        return new QueryDefault<ChargeGroup>(ChargeGroup.class, "charge_findChargeByReference", "r", pattern);
    }

    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<ChargeGroup> allChargeGroups() {
        return allInstances(ChargeGroup.class);
    }


}
