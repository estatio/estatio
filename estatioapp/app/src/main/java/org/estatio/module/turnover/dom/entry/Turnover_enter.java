package org.estatio.module.turnover.dom.entry;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.user.UserService;

import org.estatio.module.turnover.dom.Status;
import org.estatio.module.turnover.dom.Turnover;

@Mixin
public class Turnover_enter {

    private final Turnover turnover;

    public Turnover_enter(Turnover turnover) {
        this.turnover = turnover;
    }

    @Action()
    public Turnover $$(
            @Nullable final BigDecimal grossAmount,
            @Nullable final BigDecimal netAmount,
            @Nullable final BigInteger purchaseCount,
            final boolean nonCompatable,
            @Nullable @ParameterLayout(multiLine = 4) final String comments) {
        turnover.setGrossAmount(grossAmount);
        turnover.setNetAmount(netAmount);
        turnover.setPurchaseCount(purchaseCount);
        turnover.setNonComparable(nonCompatable);
        turnover.setComments(comments);
        turnover.setStatus(Status.APPROVED);
        turnover.setReportedBy(userService.getUser().getName());
        turnover.setReportedAt(clockService.nowAsLocalDateTime());
        Turnover next = turnover.nextNew();
        return next!=null ? next : turnover;
    }

    @Inject UserService userService;

    @Inject ClockService clockService;

}
