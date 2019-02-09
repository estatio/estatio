package org.estatio.module.order.dom.attr;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderAttributeName {
    ORDER_CONFIRM_ADHOC_1("orderConfirmAdhoc1"),
    ORDER_CONFIRM_ADHOC_2("orderConfirmAdhoc2"),
    ORDER_CONFIRM_ADHOC_3("orderConfirmAdhoc3"),
    ORDER_CONFIRM_SIGNATURE("orderConfirmSignature"),
    ;

    @Getter
    private String fragmentName;

}
