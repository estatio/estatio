package org.estatio.capex.dom.coda;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import lombok.Getter;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.capex.dom.coda.Element")
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "dbo")
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
public class CodaElement {

    @Getter @Setter @Column(allowsNull = "false", length = 12)
    private CodaElementLevel level;

    @Getter @Setter @Column(allowsNull = "false", length = 50)
    private String code;

    @Getter @Setter @Column(allowsNull = "false", length = 80)
    private String name;

}
