package org.estatio.module.lease.dom.amendments;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.lease.dom.LeaseItemType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "amendments.AmendmentItem"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByAmendmentAndType",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.AmendmentItem "
                        + "WHERE amendment == :amendment "
                        + "&& type == :type ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.lease.LeaseItem"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public abstract class AmendmentItem extends UdoDomainObject2<AmendmentItem> {

    public AmendmentItem() {
        super("amendment, type");
    }

    @Column(name = "amendmentId", allowsNull = "false")
    @Getter @Setter
    private Amendment amendment;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate startDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String applicableTo;

    @Action(semantics = SemanticsOf.SAFE)
    public AmendmentType getType(){
        return this.getClass().isAssignableFrom(AmendmentItemForFrequencyChange.class) ? AmendmentType.INVOICING_FREQUENCY_CHANGE : AmendmentType.DISCOUNT;
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return amendment.getApplicationTenancy();
    }

    @Programmatic
    public static String applicableToToString(final List<LeaseItemType> applicableToTypes){
        StringBuilder builder = new StringBuilder();
        applicableToTypes.stream().forEach(t->{
            if (builder.toString().getBytes().length>0){
                builder.append(",");
            }
            builder.append(t.toString());
        });
        return builder.toString();
    }

    @Programmatic
    public static List<LeaseItemType> applicableToFromString(final String applicableToString){
        List<LeaseItemType> result = new ArrayList<>();
        final String[] strings = applicableToString.split(",");
        for (String s : strings){
            result.add(LeaseItemType.valueOf(s));
        }
        return result;
    }

}
