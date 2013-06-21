package org.estatio.dom.agreement;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class AgreementTypes extends EstatioDomainService<AgreementType> {

    public AgreementTypes() {
        super(AgreementTypes.class, AgreementType.class);
    }
    
    // //////////////////////////////////////

    // TODO: naive implementation.
    public AgreementType find(final String title) {
        return getContainer().firstMatch(AgreementType.class, new Filter<AgreementType>(){
            @Override
            public boolean accept(AgreementType t) {
                return title.equals(t.getTitle());
            }
        });
    }

}
