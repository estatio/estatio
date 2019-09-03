package org.estatio.module.capex.dom.order.buyer;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "OrderSecondaryBuyerLink"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByOrder", language = "JDOQL",
                value = "SELECT " +
                        "FROM order.buyer.OrderSecondaryBuyerLink " +
                        "WHERE ordr == :order")
})
@Uniques({
        @Unique(name = "OrderSecondaryBuyerLink_ordr_UNQ", members = { "ordr" })
})
@DomainObject(
        objectType = "order.buyer.OrderSecondaryBuyerLink"
)
public class OrderSecondaryBuyerLink {

    @Getter @Setter
    @Column(allowsNull = "false", name = "orderId")
    @PropertyLayout(named = "Order")
    private Order ordr;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Party secondaryBuyer;

}
