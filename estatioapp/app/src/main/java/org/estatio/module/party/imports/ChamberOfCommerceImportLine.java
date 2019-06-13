package org.estatio.module.party.imports;

import org.apache.isis.applib.annotation.DomainObject;
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

    private String reference;

    private String code;

}
