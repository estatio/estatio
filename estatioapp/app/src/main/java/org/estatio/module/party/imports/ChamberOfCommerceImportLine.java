package org.estatio.module.party.imports;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.ChamberOfCommerceImportLine"
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChamberOfCommerceImportLine {

    private String reference;

    private String code;

    private String properties;

}
