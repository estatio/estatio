package org.estatio.module.order.dom.attr;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderAttributeName {
    CONFIRMATION_SUBJECT("orderConfirmSubject"),
    CONFIRMATION_INTRODUCTION("confirmationIntroduction"),
    CONFIRMATION_ORDER_DESCRIPTION("confirmationOrderDescription"),
    CONFIRMATION_TOTAL_WORK_COST("confirmationTotalWorkCost"),
    CONFIRMATION_WORK_SCHEDULE("confirmationWorkSchedule"),
    CONFIRMATION_PRICE_AND_PAYMENTS("confirmationPriceAndPayments"),
    CONFIRMATION_SIGNATURE("confirmationSignature"),
    ;

    @Getter
    private String fragmentName;

}
