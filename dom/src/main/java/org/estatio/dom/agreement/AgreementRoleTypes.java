package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class AgreementRoleTypes extends EstatioDomainService<AgreementRoleType> {

    public AgreementRoleTypes() {
        super(AgreementRoleTypes.class, AgreementRoleType.class);
    }
    
    // //////////////////////////////////////


    // TODO: naive implementation
    public AgreementRoleType find(final String title) {
        return getContainer().firstMatch(AgreementRoleType.class, new Filter<AgreementRoleType>(){
            @Override
            public boolean accept(AgreementRoleType t) {
                return title.equals(t.getTitle());
            }
        });
    }

    // TODO: naive implementation
    public List<AgreementRoleType> applicableTo(AgreementType agreementType) {
        return allMatches(AgreementRoleType.class, artAppliesTo(agreementType));
    }


    static Filter<AgreementRoleType> artAppliesTo(final AgreementType at) {
        return new Filter<AgreementRoleType>(){
            @Override
            public boolean accept(AgreementRoleType art) {
                return art.getAppliesTo() == at;
            }
        };
    }

}
