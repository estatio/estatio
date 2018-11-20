package org.estatio.module.coda.dom.doc;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

public class CodaPeriodParser {

    private static final Logger LOG = LoggerFactory.getLogger(CodaPeriodParser.class);

    public enum Format {
        VALID {
            // 2018/1 or 2019/12
            final Pattern PATTERN =
                    Pattern.compile(
                            "^\\s*(?<year>\\d+?)\\s*"
                            + "[/]\\s*(?<month>\\d+?)\\s*$");

            @Override
            public Parsed parse(final String codaPeriod) {
                if(codaPeriod == null) {
                    return null;
                }
                final Matcher matcher = PATTERN.matcher(codaPeriod);
                if (matcher.matches()) {
                    return new Parsed(
                            Integer.parseInt(matcher.group("year")),
                            Integer.parseInt(matcher.group("month")),
                            this
                    );
                }
                return null;
            }
        },
        NOT_RECOGNIZED {
            @Override
            public Parsed parse(final String codaPeriod) {
                return new Parsed(0, 0, this);
            }
        };

        public abstract Parsed parse(final String Period);
    }


    public Parsed parse(final String codaPeriod) {
        return parse(codaPeriod, Stream.of(Format.VALID, Format.NOT_RECOGNIZED));
    }

    Parsed parse(final String codaPeriod, final Stream<Format> formats) {
        // chain of responsibilty
        return formats
                .map(format -> format.parse(codaPeriod))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Parsing should always succeed, even if only to result in a Parsed with a format of NOT_RECOGNIZED"));
    }

    @Data
    public static class Parsed {
        private final int year;
        private final int month;
        private final Format format;

        public String asQuarter() {
            if(format == Format.NOT_RECOGNIZED) {
                return "unknown";
            }
            return year + "q" + asQuarter(month);
        }

        private int asQuarter(final int month) {
            if(month <= 3) return 1;
            if(month <= 6) return 2;
            if(month <= 9) return 3;
            if(month <= 12) return 4;
            if(month <= 15) return 5;
            if(month <= 18) return 6;
            return 99;
        }
    }

}
