package org.estatio.module.index.dom.api;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.module.index.dom.IndexValue;

public interface IndexValueCreator {

    IndexValue newIndexValue(
            final LocalDate startDate,
            final BigDecimal value);
}
