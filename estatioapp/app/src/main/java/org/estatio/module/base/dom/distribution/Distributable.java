package org.estatio.module.base.dom.distribution;

import java.math.BigDecimal;

public interface Distributable {

    BigDecimal getSourceValue();
    BigDecimal getValue();
    void setValue(BigDecimal value);

}
