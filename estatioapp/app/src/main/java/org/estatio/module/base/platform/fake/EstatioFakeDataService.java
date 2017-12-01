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

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.javafaker.Business;
import com.github.javafaker.Code;
import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import com.github.javafaker.Name;
import com.github.javafaker.Options;
import com.github.javafaker.PhoneNumber;
import com.github.javafaker.service.RandomService;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.fakedata.dom.FakeDataService;

/**
 * TODO: add to {@link FakeDataService}.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class EstatioFakeDataService {

    static Random random;
    static {
        random = new Random(System.currentTimeMillis());
    }

    Faker faker;
    @PostConstruct
    public void init() {
        faker = new Faker(random);
    }

    private RandomService randomService;

    private final Strings strings;
    private final Chars chars;
    private final Dates dates;
    private final Periods periods;

    public EstatioFakeDataService() {

        randomService = new RandomService(random);

        this.strings = new Strings();
        this.chars = new Chars();
        this.dates = new Dates();
        this.periods = new Periods();
    }

    @Programmatic
    public PhoneNumber phoneNumber() {
        return faker.phoneNumber();
    }

    @Programmatic
    public Options options() {
        return faker.options();
    }

    @Programmatic
    public Internet internet() {
        return faker.internet();
    }

    @Programmatic
    public Code code() {
        return faker.code();
    }

    @Programmatic
    public Business business() {
        return faker.business();
    }

    @Programmatic
    public Name name() {
        return faker.name();
    }

    public Strings strings() {
        return strings;
    }

    public Chars chars() {
        return chars;
    }

    public Dates dates() {
        return dates;
    }

    public Periods periods() {
        return periods;
    }

    public class Strings {

        @Programmatic
        public String fixedUpper(final int numChars) {
            final StringBuilder buf = new StringBuilder();
            final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            for (int i = 0; i < numChars; i++) {
                char c = chars().anyOf(chars);
                buf.append(c);
            }
            return buf.toString();
        }

    }

    public class Chars {

        @Programmatic
        public char anyOf(final char[] elements) {
            final int randomIdx = randomService.nextInt(elements.length);
            return elements[randomIdx];
        }

    }


    public class Dates {

        @Programmatic
        public LocalDate around(final Period period) {
            final LocalDate now = clockService.now();
            return randomService.nextDouble() < 0.5 ? before(period): after(period);
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
            return Period.days(fakeDataService.ints().between(minDays, maxDays));
        }

        @Programmatic
        public Period daysUpTo(final int maxDays) {
            return days(0, maxDays);
        }

        @Programmatic
        public Period months(final int minMonths, final int maxMonths) {
            return Period.months(fakeDataService.ints().between(minMonths, maxMonths));
        }

        @Programmatic
        public Period monthsUpTo(final int months) {
            return months(0, months);
        }

        @Programmatic
        public Period years(final int minYears, final int maxYears) {
            return Period.years(fakeDataService.ints().between(minYears, maxYears));
        }

        @Programmatic
        public Period yearsUpTo(final int years) {
            return years(0, years);
        }

    }

    @Inject
    FakeDataService fakeDataService;

    @Inject
    ClockService clockService;


}