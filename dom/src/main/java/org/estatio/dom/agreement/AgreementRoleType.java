package org.estatio.dom.agreement;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public enum AgreementRoleType {

    CREDITOR("Creditor", AgreementType.MANDATE), 
    DEBTOR("Debtor", AgreementType.MANDATE), 
    OWNER("Owner", AgreementType.MANDATE),
    TENANT("Tenant", AgreementType.LEASE), 
    LANDLORD("Landlord", AgreementType.LEASE), 
    MANAGER("Manager", AgreementType.LEASE); 

    private final String title;
    private final AgreementType appliesTo;

    private AgreementRoleType(String title, AgreementType appliesTo) {
        this.title = title;
        this.appliesTo = appliesTo;
    }

    public String title() {
        return title;
    }

    public static Set<AgreementRoleType> applicableTo(final AgreementType at) {
        Predicate<AgreementRoleType> predicate = new Predicate<AgreementRoleType>() {
            @Override
            public boolean apply(AgreementRoleType art) {
                return art.appliesTo.equals(at);
            }
        };
        return Sets.newLinkedHashSet(Iterables.filter(EnumSet.allOf(AgreementRoleType.class), predicate));
    }

}
