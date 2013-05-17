package org.estatio.dom.agreement;

import java.util.Set;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.utils.ClassUtils;

public enum AgreementType {

    MANDATE("Mandate", "org.estatio.dom.agreement.BankMandate"),
    LEASE("Lease", "org.estatio.dom.lease.Lease");

    private final String title;
    private final String clsName;

    private AgreementType(String title, String clsName) {
        this.title = title;
        this.clsName = clsName;
    }

    public String title() {
        return title;
    }

    public Set<AgreementRoleType> getRoles() {
        return AgreementRoleType.applicableTo(this);
    }

    public Agreement create(DomainObjectContainer container) {
        try {
            Class<? extends Agreement> cls = ClassUtils.load(clsName, Agreement.class);
            Agreement agreement = container.newTransientInstance(cls);
            agreement.setAgreementType(this);
            return agreement;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
