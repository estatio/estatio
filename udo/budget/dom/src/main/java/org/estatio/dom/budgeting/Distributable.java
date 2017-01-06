package org.estatio.dom.budgeting;

import java.math.BigDecimal;

public interface Distributable {

    BigDecimal getSourceValue();
    BigDecimal getValue();
    void setValue(BigDecimal value);

}
