package org.estatio.module.turnover.dom.entry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "org.estatio.module.turnover.dom.entry.TurnoverEntryRequest")
public class TurnoverEntryRequest {

    public TurnoverEntryRequest(){}

    public TurnoverEntryRequest(
            final TurnoverReportingConfig config,
            final LocalDate date,
            final Type type,
            final Frequency frequency
    ){
        this.config = config;
        this.date = date;
        this.type = type;
        this.frequency = frequency;
    }

    @Getter @Setter
    @Property(hidden = Where.EVERYWHERE)
    private TurnoverReportingConfig config;

    public Lease getlease(){
        return getConfig().getOccupancy().getLease();
    }

    public Unit getUnit(){
        return getConfig().getOccupancy().getUnit();
    }

    @Getter @Setter
    private LocalDate date;

    @Getter @Setter
    private Type type;

    @Getter @Setter
    private Frequency frequency;

    @Getter @Setter
    @Property(editing = Editing.ENABLED)
    private BigDecimal netAmount;

    @Getter @Setter
    @Property(editing = Editing.ENABLED)
    private BigDecimal grossAmount;

    @Getter @Setter
    @Property(editing = Editing.ENABLED)
    private BigInteger purchaseCount;

    @Getter @Setter
    @Property(editing = Editing.ENABLED)
    private boolean nonComparable;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Turnover> getPreviousTurnoverEntries(){
        return turnoverRepository.findApprovedByOccupancyAndTypeAndFrequencyBeforeDate(getConfig().getOccupancy(), getType(), getFrequency(), getDate());
    }

    @Inject
    TurnoverRepository turnoverRepository;
}
