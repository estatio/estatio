package org.estatio.module.turnoveraggregate.dom;

import java.math.BigInteger;
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
        objectType = "org.estatio.module.turnoveraggregate.dom.PurchaseCountAggregateForPeriod",
        publishing = Publishing.DISABLED,
        auditing = Auditing.DISABLED
)
public class PurchaseCountAggregateForPeriod {

    public String title(){
        TitleBuffer buffer = new TitleBuffer();
        buffer.append(getAggregationPeriod().getName());
        buffer.append(getCount());
        if (getCount()==null && getCountPreviousYear()==null){
            buffer.append("empty");
        } else {
            if (!isComparable()) {
                buffer.append("non comparable");
            }
        }
        return buffer.toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private AggregationPeriod aggregationPeriod;

     /*
      * TurnoverPurchaseCount1MCY
      * TurnoverPurchaseCount1MPY
      * IsComparableTPC1M
      */

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigInteger count;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigInteger countPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private boolean comparable;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigInteger count2019;

    @Programmatic
    public void remove() {
        repositoryService.removeAndFlush(this);
    }

    @Programmatic
    public void calculate(final TurnoverAggregation aggregation, final List<Turnover> turnovers) {
        turnoverAggregationService.calculatePurchaseCountAggregateForPeriod(this, aggregation, turnovers);
    }

    @Programmatic
    public List<Turnover> getTurnovers(final TurnoverAggregation aggregation){
        return turnoverAggregationService.getTurnoversForPurchaseCountAggregateForPeriod(this, aggregation, false);
    }

    @Programmatic
    public List<Turnover> getTurnoversPreviousYear(final TurnoverAggregation aggregation){
        return turnoverAggregationService.getTurnoversForPurchaseCountAggregateForPeriod(this, aggregation, true);
    }

    @Programmatic
    public List<Occupancy> distinctOccupanciesThisYear(final TurnoverAggregation aggregation){
        return getTurnovers(aggregation).stream().map(t->t.getConfig().getOccupancy()).distinct().collect(Collectors.toList());
    }

    @Programmatic
    public List<Occupancy> distinctOccupanciesPreviousYear(final TurnoverAggregation aggregation){
        return getTurnoversPreviousYear(aggregation).stream().map(t->t.getConfig().getOccupancy()).distinct().collect(Collectors.toList());
    }

    @Inject TurnoverAggregationService turnoverAggregationService;

    @Inject RepositoryService repositoryService;
}
