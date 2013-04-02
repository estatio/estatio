package org.estatio.agreement;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;

@Hidden
@Named("Agreement Terms")
public class AgreementTerms extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "agreementTerms";
    }

    public String iconName() {
        return "AgreementTerm";
    }
    // }}
    
    // {{ allAgreementTerms
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<AgreementTerm> allAgreementTerms() {
        return allInstances(AgreementTerm.class);
    }
    // }}
}
