package org.estatio.dom.agreement;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;

@Named("Agreement Types")
@Hidden
public class AgreementTypes extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "agreementTypes";
    }

    public String iconName() {
        return "AgreementTypes";
    }

    public AgreementType find(final String title) {
        return getContainer().firstMatch(AgreementType.class, new Filter<AgreementType>(){
            @Override
            public boolean accept(AgreementType t) {
                return title.equals(t.getTitle());
            }
        });
    }

}
