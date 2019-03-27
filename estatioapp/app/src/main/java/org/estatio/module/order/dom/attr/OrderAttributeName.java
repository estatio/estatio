package org.estatio.module.order.dom.attr;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderAttributeName {
    CONFIRMATION_SUBJECT("orderConfirmSubject"),
    CONFIRMATION_TOTAL_WORK_COST("confirmationTotalWorkCost"),
    ;

    @Getter
    private String fragmentName;

}
