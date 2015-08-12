package org.estatio.dom.budget;

import java.math.BigDecimal;

public interface Distributable {

    BigDecimal getSourceValue();
    BigDecimal getTargetValue();
    void setTargetValue(BigDecimal targetValue);

}
