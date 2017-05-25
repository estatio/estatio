package org.estatio.capex.dom.coda;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.DomainObject;

import lombok.Getter;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.capex.dom.coda.Element")
@PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "dbo")
public class CodaElement {

    @Getter @Setter @Column(allowsNull = "true", length = 12)
    private CodaElementLevel level;

    @Getter @Setter @Column(allowsNull = "true", length = 50)
    private String code;

    @Getter @Setter @Column(allowsNull = "true", length = 80)
    private String name;


}
