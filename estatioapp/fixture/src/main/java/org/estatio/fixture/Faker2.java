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
package org.estatio.fixture;

/**
 * Required for jacoco-site. 
 *
 */

import java.util.List;
import java.util.Random;
import javax.inject.Inject;
import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.clock.ClockService;

public class Faker2 extends Faker {

    @Inject
    private ClockService clockService;

    @Inject
    private DomainObjectContainer container;

    private RandomService randomService;

    private final Values values;
    private final Collections collections;
    private final Dates dates;
    private final Periods periods;

    public Faker2(final Random random) {

        super(random);

        randomService = new RandomService(random);

        this.values = new Values();
        this.collections = new Collections();
        this.dates = new Dates();
        this.periods = new Periods();
    }

    public Values values() {
        return values;
    }

    public Collections collections() {
        return collections;
    }

    public Dates dates() {
        return dates;
    }

    public Periods periods() {
        return periods;
    }

    public class Values {

        public String code(final int numChars) {
            final StringBuilder buf = new StringBuilder();
            final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            for (int i = 0; i < numChars; i++) {
                char c = collections().anElement(chars);
                buf.append(c);
            }
            return buf.toString();
        }

        public int anInt(final int upTo) {
            return randomService.nextInt(upTo);
        }

        public int anInt(final int min, final int max) {
            return min + randomService.nextInt(max-min);
        }

        public int aDouble(final int upTo) {
            return randomService.nextInt(upTo);
        }

        public boolean aCoinFlip() {
            return randomService.nextDouble() < 0.5;
        }

        public boolean aDiceRollOf6() {
            return anInt(6) == 5;
        }

    }

    public class Collections  {

        public <E extends Enum<E>> E anEnum(final Class<E> enumType) {
            final E[] enumConstants = enumType.getEnumConstants();
            return enumConstants[values().anInt(enumConstants.length)];
        }

        public <T> T aBounded(final Class<T> cls) {
            final List<T> list = container.allInstances(cls);
            return anElement(list);
        }

        public <T> T anElement(final List<T> list) {
            final int randomIdx = values().anInt(list.size());
            return list.get(randomIdx);
        }

        public char anElement(final char[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public byte anElement(final byte[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public short anElement(final short[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public int anElement(final int[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public long anElement(final long[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public float anElement(final float[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public double anElement(final double[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public boolean anElement(final boolean[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        public <T> T anElement(final T[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }
    }


    public class Dates {

        public LocalDate around(final Period period) {
            final LocalDate now = clockService.now();
            return values().aCoinFlip() ? before(period): after(period);
        }

        public LocalDate before(final Period period) {
            final LocalDate now = clockService.now();
            return now.minus(period);
        }

        public LocalDate after(final Period period) {
            final LocalDate now = clockService.now();
            return now.plus(period);
        }

    }

    public class Periods {

        public Period days(final int minDays, final int maxDays) {
            return Period.days(values().anInt(minDays, maxDays));
        }

        public Period daysUpTo(final int maxDays) {
            return days(0, maxDays);
        }

        public Period months(final int minMonths, final int maxMonths) {
            return Period.months(values().anInt(minMonths, maxMonths));
        }

        public Period monthsUpTo(final int months) {
            return months(0, months);
        }

        public Period years(final int minYears, final int maxYears) {
            return Period.years(values().anInt(minYears, maxYears));
        }

        public Period yearsUpTo(final int years) {
            return years(0, years);
        }

    }
}