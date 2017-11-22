package org.estatio.module.index.dom.api;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.module.index.dom.IndexBase;

public interface IndexBaseCreator {

    IndexBase findOrCreateBase(
            final LocalDate indexBaseStartDate,
            final BigDecimal indexBaseFactor);

    IndexBase createBase(
            final LocalDate indexBaseStartDate,
            final BigDecimal indexBaseFactor);
}
