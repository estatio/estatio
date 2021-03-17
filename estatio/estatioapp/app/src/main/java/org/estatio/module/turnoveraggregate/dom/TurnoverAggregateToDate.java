package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.base.dom.types.MoneyType;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Turnover;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnoveraggregate.dom.TurnoverAggregateToDate",
        publishing = Publishing.DISABLED,
        auditing = Auditing.DISABLED
)
public class TurnoverAggregateToDate {

    public String title(){
        TitleBuffer buffer = new TitleBuffer();
        if (getGrossAmount()!=null) {
            buffer.append("gross");
            buffer.append(getGrossAmount());
        }
        if (getNetAmount()!=null) {
            buffer.append("net");
            buffer.append(getNetAmount());
        }
        if (getGrossAmount()==null && getNetAmount()==null && getGrossAmountPreviousYear()==null && getNetAmountPreviousYear()==null){
            buffer.append("empty");
        } else {
            if (!isComparable()) {
                buffer.append("non comparable");
            }
        }
        return buffer.toString();
    }

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal grossAmount;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal netAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Boolean nonComparableThisYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Integer turnoverCount;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal grossAmountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal netAmountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Boolean nonComparablePreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Integer turnoverCountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private boolean comparable;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal grossAmount2019;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal netAmount2019;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Integer turnoverCount2019;

    @Programmatic
    public void remove() {
        repositoryService.removeAndFlush(this);
    }

    @Programmatic
    public void calculate(final TurnoverAggregation aggregation, final List<Turnover> turnovers) {
        turnoverAggregationService.calculateTurnoverAggregateToDate(this, aggregation, turnovers);
    }

    @Programmatic
    public List<Turnover> getTurnovers(final TurnoverAggregation aggregation){
        return turnoverAggregationService.getTurnoversForAggregateToDate(aggregation, false);
    }

    @Programmatic
    public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation){
        return turnoverAggregationService.getTurnoversForAggregateToDate(aggregation, true);
    }

    @Programmatic
    public List<Occupancy> distinctOccupanciesThisYear(final TurnoverAggregation aggregation){
        return getTurnovers(aggregation).stream().map(t->t.getConfig().getOccupancy()).distinct().collect(Collectors.toList());
    }

    @Programmatic
    public List<Occupancy> distinctOccupanciesPreviousYear(final TurnoverAggregation aggregation){
        return getTurnoversPreviousYear(aggregation).stream().map(t->t.getConfig().getOccupancy()).distinct().collect(Collectors.toList());
    }

    @Inject
    TurnoverAggregationService turnoverAggregationService;

    @Inject RepositoryService repositoryService;
}
