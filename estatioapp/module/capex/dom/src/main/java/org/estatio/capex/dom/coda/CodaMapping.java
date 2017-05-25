package org.estatio.capex.dom.coda;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.dom.charge.Charge;

import lombok.Getter;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.capex.dom.coda.CodaMapping")
@PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "dbo")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAlllllllll", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.coda.CodaMapping "
                        + "WHERE "
                        + "atPath == :atPath && "
                        + "documentType == :documentType && "
                        + "codaTransactionType == :codaTransactionType && "
                        + "charge == :charge && "
                        + "projectFilter == :projectFilter && "
                        + "propertyFilter == :propertyFilter && "
                        + "budgetFilter == :budgetFilter && "
                        + "propertyIsFullyOwned == :propertyIsFullyOwned && "
                        + "periodStartDate == :periodStartDate && "
                        + "periodEndDate == :periodEndDate && "
                        + "startDate == :startDate && "
                        + "endDate == :endDate && "
                        + "codaElement == :codaElement"
        ),
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "CodaMapping_Alllllllll_UNQ", members = { "atPath", "documentType", "codaTransactionType", "charge", "projectFilter", "propertyFilter", "budgetFilter", "propertyIsFullyOwned", "periodStartDate", "periodEndDate", "startDate", "endDate", "codaElement"}),
})
@Setter @Getter
public class CodaMapping {


    @Column(length = 50, allowsNull = "false")
    private String atPath;

    @Column(length = 50, allowsNull = "false")
    private DocumentType documentType;

    private CodaTransactionType codaTransactionType;

    @Column(length = 50, allowsNull = "false")
    private Charge charge;

    private CodaMappingFilter propertyFilter;

    private CodaMappingFilter projectFilter;

    private CodaMappingFilter budgetFilter;

    private boolean propertyIsFullyOwned;

    @Column(allowsNull = "true")
    private LocalDate periodStartDate;

    @Column(allowsNull = "true")
    private LocalDate periodEndDate;

    @Column(allowsNull = "false", length = 50, name = "codaElementId")
    private CodaElement codaElement;

    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Column(allowsNull = "true")
    private LocalDate endDate;


}
