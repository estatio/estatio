package org.estatio.agreement;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;


public enum AgreementItemType {

    DEFAULT("Rent", AgreementTerm.class);

    private final String title;
    private final Class<? extends AgreementTerm> clss;
    public static final Ordering<AgreementItemType> ORDERING_NATURAL = Ordering.<AgreementItemType>natural().nullsFirst();

    private AgreementItemType(String title, Class<? extends AgreementTerm> clss) {
        this.title = title;
        this.clss = clss;
    }

    public String title() {
        return title;
    }

    public AgreementTerm createAgreementTerm(DomainObjectContainer container){ 
        try {
            AgreementTerm term = container.newTransientInstance(clss);
            return term;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
