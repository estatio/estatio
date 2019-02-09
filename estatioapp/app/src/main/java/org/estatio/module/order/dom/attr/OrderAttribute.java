package org.estatio.module.order.dom.attr;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.NotesType;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.capex.dom.order.Order;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(identityType = IdentityType.DATASTORE ,schema = "dbo")
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version( strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(
                name = "findByOrder", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.order.dom.attr.OrderAttribute "
                        + "WHERE ordr == :order"),
        @Query(
                name = "findByOrderAndName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.order.dom.attr.OrderAttribute "
                        + "WHERE ordr == :order "
                        + "   && name == :name")
})
@DomainObject(
        objectType = "org.estatio.module.order.dom.attr.OrderAttribute"
)
public class OrderAttribute extends UdoDomainObject2<OrderAttribute> {

    public OrderAttribute() {
        super("order,name");
    }

    @Override public ApplicationTenancy getApplicationTenancy() {
        return ordr.getApplicationTenancy();
    }

    @Column(name = "orderId", allowsNull = "false")
    @Getter @Setter
    @Property(hidden = Where.PARENTED_TABLES)
    private Order ordr;

    @Column(allowsNull = "false")
    @Getter @Setter
    private OrderAttributeName name;

    @Column(allowsNull = "true", length = ValueType.Meta.MAX_LEN)
    @Getter @Setter
    private String value;

//    @Getter @Setter
//    private boolean overridden;



    public static class ValueType {
        private ValueType() {}
        public static class Meta {
            public static final int MAX_LEN = NotesType.Meta.MAX_LEN;
            private Meta() {}
        }
    }


}
