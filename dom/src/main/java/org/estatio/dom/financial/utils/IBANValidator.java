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
package org.estatio.dom.financial.utils;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class IBANValidator {

    private static final int IBAN_LENGTH_MAX = 32;

    private static final int IBAN_LENGTH_MIN = 15;

    private static final int SUFFIX_OFFSET = 4;
    
    private static final int A_ASCII = 97;
    private static final int BASE_TEN = 10;

    public static int checksum(final String iban) {
        String tmp = (iban.substring(SUFFIX_OFFSET) + iban.substring(0, SUFFIX_OFFSET)).toUpperCase();
        StringBuffer digits = new StringBuffer();
        for (int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            if (c >= '0' && c <= '9') {
                digits.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                int n = c - 'A' + BASE_TEN;
                digits.append((char) ('0' + n / BASE_TEN));
                digits.append((char) ('0' + (n % BASE_TEN)));
            } else {
                return -1;
            }
        }
        BigDecimal n = new BigDecimal(digits.toString());
        return n.remainder(BigDecimal.valueOf(A_ASCII)).intValue();
    }

    public static String fixChecksum(final String ibanTemplate) {
        int remainder = checksum(ibanTemplate);
        String pp = StringUtils.leftPad(String.valueOf(1+A_ASCII - remainder), 2, '0');
        return ibanTemplate.substring(0, 2) + pp + ibanTemplate.substring(SUFFIX_OFFSET);
    }

    public static boolean valid(final String iban) {
        if (iban == null || 
            iban.length() < IBAN_LENGTH_MIN || 
            iban.length() > IBAN_LENGTH_MAX) {
            return false;
        }

        final int checksum = checksum(iban);
        return (checksum == 1);
    }

}
