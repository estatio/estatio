/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.index;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.dom.lease.LeaseTermFrequency;

public interface Indexable {

    LocalDate getBaseIndexStartDate();

    BigDecimal getBaseIndexValue();

    BigDecimal getBaseValue();

    Index getIndex();

    BigDecimal getIndexationPercentage();

    BigDecimal getLevellingPercentage();

    LocalDate getNextIndexStartDate();

    BigDecimal getNextIndexValue();

    BigDecimal getRebaseFactor();

    void setBaseIndexStartDate(LocalDate baseIndexStartDate);

    void setBaseIndexValue(BigDecimal baseIndexValue);

    void setIndex(Index index);

    void setIndexationPercentage(BigDecimal indexationPercentage);

    void setIndexedValue(BigDecimal indexedValue);

    void setNextIndexStartDate(LocalDate nextIndexStartDate);

    void setNextIndexValue(BigDecimal nextIndexValue);

    void setRebaseFactor(BigDecimal rebaseFactor);

    BigDecimal getIndexedValue();

    void setBaseValue(BigDecimal baseValue);

    LeaseTermFrequency getFrequency();

    LocalDate getEffectiveDate();

    void setEffectiveDate(LocalDate localDate);

    BigDecimal getEffectiveIndexedValue();

    void setEffectiveIndexedValue(BigDecimal max);

    BigDecimal getSettledValue();
}
