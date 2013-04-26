package org.estatio.dom.agreement;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

@Named("Agreements")
@Hidden
public class Agreements extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "agreements";
    }

    public String iconName() {
        return "Agreements";
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Agreement newAgreement(@Named("Type") AgreementType type, final @Named("Reference") String reference, final @Named("Name") String name) {
        Agreement agreement = type.create(getContainer());
        return agreement;
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Agreement findByReference(final @Named("Reference") String reference) {
        throw new NotImplementedException();
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Agreement> allAgreements() {
        return allInstances(Agreement.class);
    }

}
