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
package org.estatio.module.country.dom.util;

import com.google.common.base.Strings;

public final class AtPathUtils {

    private AtPathUtils() {
    }

    /**
     * This is intended for use when doing filtering within the query of data with an apptenancy of a fixed number
     * of parts, eg Property (which has 2 parts, "/ITA/CAR").
     *
     * However, it DOESN'T work for object with variable length app tenancies, eg Brand.
     */
    public static String toAtPathRegex(final String atPath, final int numParts) {

        if(numParts <= 0) {
            throw new IllegalArgumentException("Parts must be > 0, was " + numParts);
        }
        final StringBuilder buf = new StringBuilder();
        if(atPath == null) {
            for (int i = 0; i < numParts; i++) {
                buf.append("/null");
            }
            return buf.toString();
        }

        final String[] atPathParts = atPath.substring(1).split("/");
        for (int i = 0; i < numParts; i++) {
            buf.append("/");
            if (atPathParts.length >= i+1) {
                final String atPathPart = atPathParts[i];
                buf.append(Strings.isNullOrEmpty(atPathPart) ? ".*" : atPathPart);
            } else {
                buf.append(".*");
            }
        }
        return buf.toString();

    }

    public static String toCountryRefRegex(final String atPath) {

        if(atPath == null) {
            return "null";
        }

        final String[] atPathParts = atPath.split("/");
        switch (atPathParts.length) {
        case 0:
        case 1:
            return ".*";
        default:
            return atPathParts[1];
        }
    }
}
