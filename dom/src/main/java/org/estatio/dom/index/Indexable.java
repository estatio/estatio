package org.estatio.dom.index;

import java.math.BigDecimal;

public interface Indexable {
    
    public void setBaseIndexValue(BigDecimal baseIndexValue);
    
    public void setNextIndexValue(BigDecimal nextIndexValue);

    public void setIndexationPercentage(BigDecimal indexationPercentage);
    
    public void setIndexedValue(BigDecimal indexedValue);
    
}
