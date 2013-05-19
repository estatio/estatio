package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;

@Named("Agreement Role Types")
@Hidden
public class AgreementRoleTypes extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "agreementRoleTypes";
    }

    public String iconName() {
        return "AgreementRoleTypes";
    }

    public AgreementRoleType find(final String title) {
        return getContainer().firstMatch(AgreementRoleType.class, new Filter<AgreementRoleType>(){
            @Override
            public boolean accept(AgreementRoleType t) {
                return title.equals(t.getTitle());
            }
        });
    }

    public List<AgreementRoleType> applicableTo(AgreementType agreementType) {
        return getContainer().allMatches(AgreementRoleType.class, artAppliesTo(agreementType));
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
