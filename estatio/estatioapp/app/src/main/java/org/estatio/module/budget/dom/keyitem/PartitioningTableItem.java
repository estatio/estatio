package org.estatio.module.budget.dom.keyitem;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.budget.dom.keytable.PartitioningTable;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.budgeting.keyitem.PartitioningTableItem"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByPartitioningTableAndUnit", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.keyitem.PartitioningTableItem " +
                        "WHERE partitioningTable == :partitioningTable && unit == :unit")
})
@Unique(name = "PartitioningTableItem_partitioningTable_unit", members = { "partitioningTable", "unit" })
@DomainObject(
        objectType = "org.estatio.dom.budgeting.keytable.PartitioningTable"
)
public abstract class PartitioningTableItem extends UdoDomainObject2<PartitioningTableItem>
        implements WithApplicationTenancyProperty {

    public PartitioningTableItem(
            final String keyProperties) {
        super(keyProperties);
    }

    @Column(name="partitioningTableId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES )
    @Getter @Setter
    private PartitioningTable partitioningTable;

    @Column(name="unitId", allowsNull = "false")
    @Getter @Setter
    private Unit unit;

}
