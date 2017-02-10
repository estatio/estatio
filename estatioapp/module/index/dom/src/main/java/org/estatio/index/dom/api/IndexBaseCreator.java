package org.estatio.index.dom.api;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.index.dom.IndexBase;

public interface IndexBaseCreator {

    IndexBase findOrCreateBase(
            final LocalDate indexBaseStartDate,
            final BigDecimal indexBaseFactor);

    IndexBase createBase(
            final LocalDate indexBaseStartDate,
            final BigDecimal indexBaseFactor);
}
