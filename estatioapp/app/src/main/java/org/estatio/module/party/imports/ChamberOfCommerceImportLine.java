package org.estatio.module.party.imports;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.ChamberOfCommerceImportLine"
)
@Getter @Setter
public class ChamberOfCommerceImportLine {

    public ChamberOfCommerceImportLine() {
    }

    public ChamberOfCommerceImportLine(final String reference, final String properties) {
        this.reference = reference;
        this.properties = properties;
    }

    @MemberOrder(sequence = "1")
    private String reference;

    @MemberOrder(sequence = "2")
    private String name;

    @MemberOrder(sequence = "3")
    private String properties;

    @MemberOrder(sequence = "4")
    private String code;


}
