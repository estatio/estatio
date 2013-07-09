/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integtest.specs;

import cucumber.api.Transformer;

import org.joda.time.format.DateTimeFormat;

/**
 * A set of basic value type converters.
 */
public class C  {
    private C() {}
    
    /**
     * Converts {@link String}s to {@link LocalDate}, but also recognizing the keyword 'null'.
     */
    public static class LocalDate extends Transformer<org.joda.time.LocalDate> {
        
        @Override
        public org.joda.time.LocalDate transform(String value) {
            return value == null || "null".equals(value)
                    ? null 
                            : DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(getLocale()).parseLocalDate(value);
        }
        
        public static org.joda.time.LocalDate as(Object value) {
            return value != null && value instanceof String
                    ? new LocalDate().transform((String)value)
                    : null;
        }
    }
}