/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.utils;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DateTimeUtils {

    public static Period stringToPeriod(String inputString) {
        inputString = inputString.replaceAll(" ", "").toLowerCase();
        PeriodFormatter formatter = new PeriodFormatterBuilder().
                appendYears().appendSuffix("y").
                appendMonths().appendSuffix("m").
                appendDays().appendSuffix("d").
                appendHours().appendSuffix("h").
                appendMinutes().appendSuffix("min").
                toFormatter();
        try {
            Period p = formatter.parsePeriod(inputString);
            return p;
        } catch (Exception e) {
            return null;
        }
    }

    public static String periodToString(Period period) {
        StringBuilder sb = new StringBuilder();
        Period leftOver = period;
        int y = leftOver.getYears();
        if (y > 0) {
            sb.append(String.format("%1$d year(s) ", y));
            leftOver.minusYears(y);
        }
        int m = leftOver.getMonths();
        if (m > 0) {
            sb.append(String.format("%1$d month(s) ", m));
            leftOver.minusMonths(y);
        }
        int d = leftOver.getDays();
        if (d > 0) {
            sb.append(String.format("%1$d day(s) ", d));
            leftOver.minusDays(y);
        }
        return sb.toString().trim();
    }
}
