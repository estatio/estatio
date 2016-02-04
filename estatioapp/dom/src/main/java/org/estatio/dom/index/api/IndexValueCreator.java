package org.estatio.dom.index.api;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.dom.index.IndexValue;

public interface IndexValueCreator {

    IndexValue newIndexValue(
            final LocalDate startDate,
            final BigDecimal value);
}
