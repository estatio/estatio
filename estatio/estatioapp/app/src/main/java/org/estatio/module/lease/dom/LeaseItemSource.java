package org.estatio.module.lease.dom;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPropertyLocal;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByItem",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseItemSource "
                        + "WHERE item == :item "),
        @javax.jdo.annotations.Query(
                name = "findBySourceItem",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseItemSource "
                        + "WHERE sourceItem == :sourceItem "),
        @javax.jdo.annotations.Query(
                name = "findByItemAndSourceItem",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseItemSource "
                        + "WHERE item == :item && sourceItem == :sourceItem")
})
@Unique(name = "LeaseItemSource_item_sourceItem_UNQ", members = {"item", "sourceItem"})
@DomainObject(
        objectType = "org.estatio.dom.lease.LeaseItemSource"
)
public class LeaseItemSource extends UdoDomainObject2<LeaseItemSource> implements WithApplicationTenancyPropertyLocal {

    public LeaseItemSource() {
        super("item, sourceItem");
    }

    public LeaseItemSource(LeaseItem item, LeaseItem sourceItem) {
        super("item, sourceItem");
        this.item = item;
        this.sourceItem = sourceItem;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void remove(){
        remove(this);
    }

    @Override
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public ApplicationTenancy getApplicationTenancy() {
        return item.getApplicationTenancy();
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private LeaseItem item;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LeaseItem sourceItem;

}
