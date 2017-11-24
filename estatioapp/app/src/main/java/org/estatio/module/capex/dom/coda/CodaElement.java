package org.estatio.module.capex.dom.coda;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.capex.dom.coda.CodaElement",
        autoCompleteRepository = CodaElementRepository.class)
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "dbo")
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(
                name="searchByCodeOrName",
                value = "SELECT FROM org.estatio.module.capex.dom.coda.CodaElement "
                        + "WHERE code.matches(:regex) || name.matches(:regex)")
})
public class CodaElement {

    public String title() {
        return String.format("%s, %s,", getCode(), getName());
    }

    @Getter @Setter @Column(allowsNull = "false", length = 12)
    private CodaElementLevel level;

    @Getter @Setter @Column(allowsNull = "false", length = 50)
    private String code;

    @Getter @Setter @Column(allowsNull = "false", length = 80)
    private String name;

}
