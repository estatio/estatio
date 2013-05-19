package org.estatio.dom.agreement;

import java.util.List;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.utils.ClassUtils;

@PersistenceCapable
@Immutable
public class AgreementType extends EstatioRefDataObject implements Comparable<AgreementType> {

    // {{ Title (property)
    private String title;

    @MemberOrder(sequence = "1")
    @Title
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
    // }}


    
    // {{ ImplementationClassName (property)
    private String implementationClassName;

    @Hidden
    @MemberOrder(sequence = "2")
    public String getImplementationClassName() {
        return implementationClassName;
    }

    public void setImplementationClassName(final String implementationClassName) {
        this.implementationClassName = implementationClassName;
    }
    // }}


    @NotPersisted // else Isis tries to persist graph when setting up fixture data.
    public List<AgreementRoleType> getRoles() {
        return AgreementRoleType.applicableTo(this);
    }

    public Agreement create(DomainObjectContainer container) {
        try {
            Class<? extends Agreement> cls = ClassUtils.load(implementationClassName, Agreement.class);
            Agreement agreement = container.newTransientInstance(cls);
            agreement.setAgreementType(this);
            return agreement;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    @Override
    public int compareTo(AgreementType o) {
        return getTitle().compareTo(o.getTitle());
    }


    @Override
    @Hidden
    public DomainObjectContainer getContainer() {
        return super.getContainer();
    }

    
    public static AgreementType create(final String title, final String implementationClassName, final DomainObjectContainer container) {
        final AgreementType agreementType = container.newTransientInstance(AgreementType.class);
        agreementType.setTitle(title);
        agreementType.setImplementationClassName(implementationClassName);
        container.persist(agreementType);
        return agreementType;
    }
}
