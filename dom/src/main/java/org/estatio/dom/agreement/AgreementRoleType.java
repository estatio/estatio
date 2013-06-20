package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable
@Immutable
public class AgreementRoleType extends EstatioRefDataObject<AgreementRoleType> {

    public AgreementRoleType() {
        super("title");
    }

    // //////////////////////////////////////

    private String title;

    @MemberOrder(sequence = "1")
    @Title
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    // //////////////////////////////////////

    private AgreementType appliesTo;

    @MemberOrder(sequence = "2")
    public AgreementType getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(final AgreementType agreementType) {
        this.appliesTo = agreementType;
    }

    // //////////////////////////////////////

    public static List<AgreementRoleType> applicableTo(final AgreementType at) {
        return at.getApplicableTo();
    }

    public static AgreementRoleType find(final String title, DomainObjectContainer container) {
        return container.firstMatch(AgreementRoleType.class, new Filter<AgreementRoleType>() {

            @Override
            public boolean accept(AgreementRoleType t) {
                return title.equals(t.getTitle());
            }
        });
    }

    // //////////////////////////////////////

    /**
     * For fixtures
     */
    public static AgreementRoleType create(final String title, final AgreementType appliesTo, final DomainObjectContainer container) {
        final AgreementRoleType agreementRoleType = container.newTransientInstance(AgreementRoleType.class);
        agreementRoleType.setTitle(title);
        agreementRoleType.setAppliesTo(appliesTo);
        container.persist(agreementRoleType);
        return agreementRoleType;
    }

}
