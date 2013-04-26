package org.estatio.dom.agreement;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.estatio.dom.lease.Lease;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

public enum AgreementType {

    MANDATE("Mandate", BankMandate.class),
    LEASE("Lease", Lease.class);

    private final String title;
    private final Class<? extends Agreement> cls;

    private AgreementType(String title, Class<? extends Agreement> cls) {
        this.title = title;
        this.cls = cls;
    }

    public String title() {
        return title;
    }
    
    public Set<AgreementRoleType> getRoles() {
        return AgreementRoleType.applicableTo(this);
    }
    
    public Agreement create(DomainObjectContainer container) {
        try {
            Agreement agreement = container.newTransientInstance(cls);
            agreement.setAgreementType(this);
            return agreement;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
