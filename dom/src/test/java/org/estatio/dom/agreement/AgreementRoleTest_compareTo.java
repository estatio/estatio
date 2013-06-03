package org.estatio.dom.agreement;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.estatio.dom.ComparableContractTest_compareTo;

public class AgreementRoleTest_compareTo extends ComparableContractTest_compareTo<AgreementRole> {

    private AgreementRoleType type1;
    private AgreementRoleType type2;
    
    @Before
    public void setup() {
        type1 = new AgreementRoleType();
        type2 = new AgreementRoleType();
        type1.setTitle("Abc");
        type2.setTitle("Def");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<AgreementRole>> orderedTuples() {
        return listOf(
                    listOf(
                            newAgreementRole(null, null), 
                            newAgreementRole(type1, null), 
                            newAgreementRole(type1, null), 
                            newAgreementRole(type2, null)), 
                    listOf(
                            newAgreementRole(type1, null), 
                            newAgreementRole(type1, new LocalDate(2013,4,1)), 
                            newAgreementRole(type1, new LocalDate(2013,4,1)), 
                            newAgreementRole(type1, new LocalDate(2013,5,2)))
                );
    }

    private AgreementRole newAgreementRole(AgreementRoleType art, LocalDate date) {
        final AgreementRole ar = new AgreementRole();
        ar.setType(art);
        ar.setStartDate(date);
        return ar;
    }


}
