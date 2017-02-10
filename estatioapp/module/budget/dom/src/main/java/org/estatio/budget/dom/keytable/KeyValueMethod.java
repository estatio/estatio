/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.budget.dom.keytable;

import java.math.BigDecimal;
import java.util.Iterator;

import org.estatio.budget.dom.keyitem.KeyItem;

public enum KeyValueMethod {
    PROMILLE {
        @Override
        public boolean isValid(KeyTable keyTable) {
                if (!this.keySum(keyTable).equals(BigDecimal.valueOf(1000.000).setScale(keyTable.getPrecision(),BigDecimal.ROUND_HALF_UP))) {
                    return false;
                }
            return true;
        }
        @Override
        public BigDecimal keySum(KeyTable keyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<KeyItem> it = keyTable.getItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getValue());
            }
            return sum.setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP);
        }
        @Override
        public BigDecimal divider(KeyTable keyTable) {
            return new BigDecimal("1000");
        }
    },
    PERCENT {
        @Override
        public boolean isValid(KeyTable keyTable) {
            if (!this.keySum(keyTable).equals(BigDecimal.valueOf(100.000).setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP))) {
                return false;
            }
            return true;
        }
        @Override
        public BigDecimal keySum(KeyTable keyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<KeyItem> it = keyTable.getItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getValue());
            }
            return sum.setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP);
        }
        @Override
        public BigDecimal divider(KeyTable keyTable) {
            return new BigDecimal("100");
        }
    },
    DEFAULT {
         @Override
        public boolean isValid(KeyTable keyTable) {
            return true;
        }
        @Override
        public BigDecimal keySum(KeyTable keyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<KeyItem> it = keyTable.getItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getValue());
            }
            return sum;
        }
        @Override
        public BigDecimal divider(KeyTable keyTable) {
            return keySum(keyTable);
        }
    };

    public abstract BigDecimal divider(KeyTable keyTable);

    public abstract boolean isValid(final KeyTable keyTable);

    public abstract BigDecimal keySum(final KeyTable keyTable);


}
