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
package org.estatio.module.base.platform.fake;


import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.javafaker.Address;
import com.github.javafaker.Business;
import com.github.javafaker.Code;
import com.github.javafaker.Internet;
import com.github.javafaker.Lorem;
import com.github.javafaker.Name;
import com.github.javafaker.Options;
import com.github.javafaker.PhoneNumber;
import com.github.javafaker.service.RandomService;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

/**
 * TODO: replace with FakeDataService
 */
@Deprecated
@DomainService(nature = NatureOfService.DOMAIN)
public class EstatioFakeDataService {

    static Random random;
    static {
        random = new Random(System.currentTimeMillis());
    }

    Faker2 faker2;
    @PostConstruct
    public void init() {
        faker2 = new Faker2(random);
    }

    @Inject
    private ClockService clockService;

    @Inject
    private DomainObjectContainer container;

    private RandomService randomService;

    private final Values values;
    private final Collections collections;
    private final Dates dates;
    private final Periods periods;

    public EstatioFakeDataService() {

        randomService = new RandomService(random);

        this.values = new Values();
        this.collections = new Collections();
        this.dates = new Dates();
        this.periods = new Periods();
    }

    @Programmatic
    public PhoneNumber phoneNumber() {
        return faker2.phoneNumber();
    }

    @Programmatic
    public Options options() {
        return faker2.options();
    }

    @Programmatic
    public Internet internet() {
        return faker2.internet();
    }

    @Programmatic
    public Code code() {
        return faker2.code();
    }

    @Programmatic
    public Business business() {
        return faker2.business();
    }

    @Programmatic
    public Address address() {
        return faker2.address();
    }

    @Programmatic
    public Name name() {
        return faker2.name();
    }

    @Programmatic
    public Lorem lorem() {
        return faker2.lorem();
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

        @Programmatic
        public String code(final int numChars) {
            final StringBuilder buf = new StringBuilder();
            final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            for (int i = 0; i < numChars; i++) {
                char c = collections().anElement(chars);
                buf.append(c);
            }
            return buf.toString();
        }

        @Programmatic
        public int anInt(final int upTo) {
            return randomService.nextInt(upTo);
        }

        @Programmatic
        public int anInt(final int min, final int max) {
            return min + randomService.nextInt(max-min);
        }

        @Programmatic
        public int aDouble(final int upTo) {
            return randomService.nextInt(upTo);
        }

        @Programmatic
        public boolean aCoinFlip() {
            return randomService.nextDouble() < 0.5;
        }

        @Programmatic
        public boolean aDiceRollOf6() {
            return anInt(6) == 5;
        }

    }

    public class Collections  {

        @Programmatic
        public <E extends Enum<E>> E anEnum(final Class<E> enumType) {
            final E[] enumConstants = enumType.getEnumConstants();
            return enumConstants[values().anInt(enumConstants.length)];
        }

        @Programmatic
        public <T> T aBounded(final Class<T> cls) {
            final List<T> list = container.allInstances(cls);
            return anElement(list);
        }

        @Programmatic
        public <T> T anElement(final List<T> list) {
            final int randomIdx = values().anInt(list.size());
            return list.get(randomIdx);
        }

        @Programmatic
        public char anElement(final char[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public byte anElement(final byte[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public short anElement(final short[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public int anElement(final int[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public long anElement(final long[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public float anElement(final float[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public double anElement(final double[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public boolean anElement(final boolean[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }

        @Programmatic
        public <T> T anElement(final T[] elements) {
            final int randomIdx = values().anInt(elements.length);
            return elements[randomIdx];
        }
    }


    public class Dates {

        @Programmatic
        public LocalDate around(final Period period) {
            final LocalDate now = clockService.now();
            return values().aCoinFlip() ? before(period): after(period);
        }

        @Programmatic
        public LocalDate before(final Period period) {
            final LocalDate now = clockService.now();
            return now.minus(period);
        }

        @Programmatic
        public LocalDate after(final Period period) {
            final LocalDate now = clockService.now();
            return now.plus(period);
        }

    }

    public class Periods {

        @Programmatic
        public Period days(final int minDays, final int maxDays) {
            return Period.days(values().anInt(minDays, maxDays));
        }

        @Programmatic
        public Period daysUpTo(final int maxDays) {
            return days(0, maxDays);
        }

        @Programmatic
        public Period months(final int minMonths, final int maxMonths) {
            return Period.months(values().anInt(minMonths, maxMonths));
        }

        @Programmatic
        public Period monthsUpTo(final int months) {
            return months(0, months);
        }

        @Programmatic
        public Period years(final int minYears, final int maxYears) {
            return Period.years(values().anInt(minYears, maxYears));
        }

        @Programmatic
        public Period yearsUpTo(final int years) {
            return years(0, years);
        }

    }


}