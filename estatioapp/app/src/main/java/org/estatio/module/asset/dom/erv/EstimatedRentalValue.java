package org.estatio.module.asset.dom.erv;

import java.math.BigDecimal;

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

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.asset.dom.Unit;
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
                        + "FROM org.estatio.module.asset.dom.erv.EstimatedRentalValue "
                        + "WHERE unit == :unit && date == :date && type == :type "),
        @Query(
                name = "findByUnitAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.erv.EstimatedRentalValue "
                        + "WHERE unit == :unit && type == :type ORDER BY date DESC")
})
@Unique(name = "EstimatedRentalValue_unit_date_type_UNQ", members = { "unit", "date", "type" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class EstimatedRentalValue extends UdoDomainObject2<EstimatedRentalValue> {

    public EstimatedRentalValue() {
        super("unit, date, type");
    }

    public EstimatedRentalValue(
            final Unit unit,
            final LocalDate date,
            final Type type,
            final BigDecimal value) {
        this();
        this.unit = unit;
        this.date = date;
        this.type = type;
        this.value = value;
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getUnit())
                .withName(date)
                .withName(type)
                .toString();
    }

    @Column(allowsNull = "false", name = "unitId")
    @Getter @Setter
    private Unit unit;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate date;

    @Column(allowsNull = "false")
    @Getter @Setter
    private Type type;

    @Column(allowsNull = "false", scale = 2)
    @PropertyLayout(named = "value per m2")
    @Getter @Setter
    private BigDecimal value;

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getUnit().getApplicationTenancy();
    }

}
