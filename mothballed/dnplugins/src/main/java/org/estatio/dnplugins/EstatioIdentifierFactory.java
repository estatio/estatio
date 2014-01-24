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
package org.estatio.dnplugins;

import java.util.Map;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.identifier.DN2IdentifierFactory;
import org.datanucleus.store.rdbms.identifier.IdentifierCase;

public class EstatioIdentifierFactory extends DN2IdentifierFactory {

    public EstatioIdentifierFactory(DatastoreAdapter dba, ClassLoaderResolver clr, @SuppressWarnings("rawtypes") Map props) {
        super(dba, clr, props);
    }


    @Override
    public String generateIdentifierNameForJavaName(String javaName) {
        return generateIdentifierNameForJavaName(javaName, identifierCase, wordSeparator);
    }

    /**
     * Extracted for testing...
     */
    static String generateIdentifierNameForJavaName(String javaName, final IdentifierCase identifierCase, final String wordSeparator) {
        if (javaName == null)
        {
            return null;
        }

        StringBuffer s = new StringBuffer();
        boolean skipUntilUnderscore = false;

        for (int i = 0; i < javaName.length(); ++i)
        {
            char c = javaName.charAt(i);

            if(skipUntilUnderscore) {
                if(c == '_') {
                    skipUntilUnderscore = false;
                }
                continue;
            }
            if (c >= 'A' && c <= 'Z' &&
                (identifierCase != IdentifierCase.MIXED_CASE && identifierCase != IdentifierCase.MIXED_CASE_QUOTED))
            {
                s.append(c);
            }
            else if (c >= 'A' && c <= 'Z' &&
                (identifierCase == IdentifierCase.MIXED_CASE || identifierCase == IdentifierCase.MIXED_CASE_QUOTED))
            {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' &&
                (identifierCase == IdentifierCase.MIXED_CASE || identifierCase == IdentifierCase.MIXED_CASE_QUOTED))
            {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' &&
                (identifierCase != IdentifierCase.MIXED_CASE && identifierCase != IdentifierCase.MIXED_CASE_QUOTED))
            {
                s.append((char)(c - ('a' - 'A')));
            }
            else if (c >= '0' && c <= '9' || c=='_')
            {
                s.append(c);
            }
            else if (c == '.')
            {
                s.append(wordSeparator);
                skipUntilUnderscore = true;
            }
            else
            {
                String cval = "000" + Integer.toHexString(c);

                s.append(cval.substring(cval.length() - (c > 0xff ? 4 : 2)));
            }
        }

        // Remove leading and trailing underscores
        while (s.length() > 0 && s.charAt(0) == '_')
        {
            s.deleteCharAt(0);
        }
        if (s.length() == 0)
        {
            throw new IllegalArgumentException("Illegal Java identifier: " + javaName);
        }

        return s.toString();
    }
    
    @Override
    protected String getColumnIdentifierSuffix(int role, boolean embedded) {
        return stripOidSuffixIfPresent(super.getColumnIdentifierSuffix(role, embedded));
    }

    /**
     * Extracted for testing
     */
    static String stripOidSuffixIfPresent(String columnIdentifierSuffix) {
        if(columnIdentifierSuffix.endsWith("_OID")) {
            return columnIdentifierSuffix.substring(0, columnIdentifierSuffix.length()-4);
        }
        return columnIdentifierSuffix;
    }

}
