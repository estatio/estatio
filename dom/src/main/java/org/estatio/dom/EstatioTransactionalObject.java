package org.estatio.dom;

import javax.jdo.JDOHelper;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Where;

/**
 * A domain object that is mutable and can be changed by multiple users over time,
 * and should therefore have optimistic locking controls in place.
 * 
 * <p>
 * Subclasses must be annotated with:
 * <pre>
 * @javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
 * public class MyDomainObject extends EstationTransactionalObject {
 *   ...
 * }
 * </pre>
 */
public abstract class EstatioTransactionalObject extends EstatioDomainObject {


    // {{ ID (derived property)
    @Hidden()
    @Disabled
    @MemberOrder(name="Detail", sequence = "100")
    @Named("Id")
    public String getId() {
        final String id = JDOHelper.getObjectId(this).toString().split("\\[OID\\]")[0];
        return id;
    }
    // }}

    // {{ Version (derived property)
    @Hidden()
    @Disabled
    @MemberOrder(name="Detail", sequence = "101")
    @Named("Version")
    public Long getVersionSequence() {
        final Long version = (Long) JDOHelper.getVersion(this);
        return version;
    }
    // }}

}
