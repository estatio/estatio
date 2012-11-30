package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.invoice.Charge;
import com.eurocommercialproperties.estatio.dom.invoice.Charges;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class ChargesJdo extends Charges {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Charge findChargeByReference(@Named("Reference") String reference) {
        return firstMatch(queryForFindChargeByReference(reference));
    }
    
    private static QueryDefault<Charge> queryForFindChargeByReference(String reference) {
        return new QueryDefault<Charge>(Charge.class, "charge_findChargeByReference", "r", reference);
    }

}
