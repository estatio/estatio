package org.estatio.module.turnover.dom.entry;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.user.UserService;

import org.isisaddons.module.security.app.user.MeService;

import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;

@Mixin
public class Turnover_enter {

    public static class TurnoverEnterEvent
            extends ActionDomainEvent<Turnover> {}

    private final Turnover turnover;

    public Turnover_enter(Turnover turnover) {
        this.turnover = turnover;
    }

    @Action()
    public Turnover $$(
            @Nullable final BigDecimal grossAmount,
            @Nullable final BigDecimal netAmount,
            @Nullable final BigInteger purchaseCount,
            final boolean nonComparable,
            @Nullable @ParameterLayout(multiLine = 4) final String comments) {
        turnover.setGrossAmount(grossAmount);
        turnover.setNetAmount(netAmount);
        turnover.setPurchaseCount(purchaseCount);
        turnover.setNonComparable(nonComparable);
        turnover.setComments(comments);
        turnover.setStatus(Status.APPROVED);
        turnover.setReportedBy(userService.getUser().getName());
        turnover.setReportedAt(clockService.nowAsLocalDateTime());

        TurnoverEnterEvent event = new TurnoverEnterEvent();
        event.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
        event.setSource(turnover);
        eventBusService.post(event);

        Turnover next = turnover.nextNew();
        return next!=null ? next : turnover;
    }

    public BigDecimal default0$$(){
        return turnover.getGrossAmount();
    }

    public BigDecimal default1$$(){
        return turnover.getNetAmount();
    }

    public BigInteger default2$$(){
        return turnover.getPurchaseCount();
    }

    public boolean default3$$(){
        return turnover.isNonComparable();
    }

    public String default4$$(){
        return turnover.getComments();
    }

    @Inject UserService userService;

    @Inject ClockService clockService;

    @Inject EventBusService eventBusService;

    @Inject MeService meService;

}
