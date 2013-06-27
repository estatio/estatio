package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
        name = "findByTitle", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannelType " +
                "WHERE title == :title "),
    @javax.jdo.annotations.Query(
            name = "findByAgreementType", language = "JDOQL", 
            value = "SELECT " +
                    "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannelType " +
            "WHERE appliesTo == :agreementType ")
})
@Immutable
@Bounded
public class AgreementRoleCommunicationChannelType extends EstatioRefDataObject<AgreementRoleCommunicationChannelType> {

    public AgreementRoleCommunicationChannelType() {
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

    @javax.jdo.annotations.Column(name="APPLIESTO_ID")
    private AgreementType appliesTo;

    @MemberOrder(sequence = "2")
    public AgreementType getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(final AgreementType agreementType) {
        this.appliesTo = agreementType;
    }

    // //////////////////////////////////////

    public static List<AgreementRoleCommunicationChannelType> applicableTo(final AgreementType at) {
        return at.getRoleChannelTypesApplicableTo();
    }


}
