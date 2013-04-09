package org.estatio.dom.agreement;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

public enum AgreementType {

    //LEASE("Lease", Lease.class, AgreementRoleType.TENANT, AgreementRoleType.LANDLORD),
    MANDATE("Mandate", BankMandate.class, AgreementRoleType.DEBTOR, AgreementRoleType.CREDITOR);

    private final String title;
    private final List<AgreementRoleType> roles;
    private final Class<? extends Agreement> cls;

    private AgreementType(String title, Class<? extends Agreement> cls, AgreementRoleType... roles) {
        this.title = title;
        this.roles = Arrays.asList(roles);
        this.cls = cls;
    }

    public String title() {
        return title;
    }
    
    public List<AgreementRoleType> getRoles() {
        return roles;
    }
    
    public Agreement create(DomainObjectContainer container) {
        try {
            Agreement agreement = container.newTransientInstance(cls);
            agreement.setType(this);
            return agreement;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
