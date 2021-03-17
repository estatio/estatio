package org.estatio.module.asset.dom.counts;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainObject2;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.counts.Count "
                        + "WHERE property == :property && type == :type && date == :date "),
        @Query(
                name = "findByPropertyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.counts.Count "
                        + "WHERE property == :property "
                        + "&& type == :type "
                        + "ORDER BY date DESC")
})
@Unique(name = "Count_property_type_date_UNQ", members = { "property", "type", "date" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Count extends UdoDomainObject2<Count> {

    public Count() {
        super("property, date");
    }

    public Count(
            final Property property,
            final Type type,
            final LocalDate date,
            final BigInteger value) {
        this();
        this.property = property;
        this.type = type;
        this.date = date;
        this.value = value;
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getProperty())
                .withName(getDate())
                .toString();
    }

    @Column(allowsNull = "false", name = "propertyId")
    @Getter @Setter
    private Property property;

    @Column(allowsNull = "false")
    @Getter @Setter
    private Type type;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate date;

    @Column(allowsNull = "false")
    @Getter @Setter
    private BigInteger value;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Count> getPrevious(){
        return countRepository.findByPropertyAndType(getProperty(), getType()).stream().filter(c->c.getDate().isBefore(getDate())).collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Count changeCount(final BigInteger value){
        setValue(value);
        return this;
    }

    public BigInteger default0ChangeCount(){
        return getValue();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Property remove(){
        Property result = getProperty();
        countRepository.remove(this);
        return result;
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getProperty().getApplicationTenancy();
    }

    @Inject
    CountRepository countRepository;

}
