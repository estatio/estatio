package org.estatio.module.turnover.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;

@Mixin
public class Occupancy_turnovers {

    private final Occupancy occupancy;

    public Occupancy_turnovers(final Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Turnover> turnovers() {
        return turnoverRepository.findByOccupancy(occupancy);
    }

    @Inject
    TurnoverRepository turnoverRepository;


}
