package org.estatio.dom.agreement;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.EstatioDomainService;

@Named("Agreement Types")
@Hidden
public class AgreementTypes extends EstatioDomainService {

    public AgreementTypes() {
        super(AgreementTypes.class, AgreementType.class);
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
