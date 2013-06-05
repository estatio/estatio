package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.ComparableByTitle;

@javax.jdo.annotations.PersistenceCapable
@Immutable
public class AgreementRoleType extends EstatioRefDataObject implements ComparableByTitle<AgreementRoleType>  {

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


    // {{ AppliesTo (property)
    private AgreementType appliesTo;
    @MemberOrder(sequence = "2")
    public AgreementType getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(final AgreementType agreementType) {
        this.appliesTo = agreementType;
    }
    // }}



    public static List<AgreementRoleType> applicableTo(final AgreementType at) {
        return at.getApplicableTo();
    }


    // {{ Comparable impl
    @Override
    public int compareTo(AgreementRoleType other) {
        return ORDERING_BY_TITLE.compare(this, other);
    }
    
    // }}

    
    public static AgreementRoleType create(final String title, final AgreementType appliesTo, final DomainObjectContainer container) {
        final AgreementRoleType agreementRoleType = container.newTransientInstance(AgreementRoleType.class);
        agreementRoleType.setTitle(title);
        agreementRoleType.setAppliesTo(appliesTo);
        container.persist(agreementRoleType);
        return agreementRoleType;
    }

    public static AgreementRoleType find(final String title, DomainObjectContainer container) {
        return container.firstMatch(AgreementRoleType.class, new Filter<AgreementRoleType>(){

            @Override
            public boolean accept(AgreementRoleType t) {
                return title.equals(t.getTitle());
            }
        });
    }

}
