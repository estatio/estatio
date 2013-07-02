package org.estatio.dom.agreement;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class AgreementTypes extends EstatioDomainService<AgreementType> {

    public AgreementTypes() {
        super(AgreementTypes.class, AgreementType.class);
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public AgreementType find(final String title) {
        return firstMatch("findByTitle", "title", title);
    }

}
