package org.estatio.index.dom.api;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.index.dom.IndexValue;

public interface IndexValueCreator {

    IndexValue newIndexValue(
            final LocalDate startDate,
            final BigDecimal value);
}
