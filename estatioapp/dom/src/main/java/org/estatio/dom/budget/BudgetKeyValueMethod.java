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
package org.estatio.dom.budget;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;

public enum BudgetKeyValueMethod {
    PROMILLE {
        @Override
        public boolean isValid(BudgetKeyTable budgetKeyTable) {
                if (!this.keySum(budgetKeyTable).equals(new BigDecimal(1000.000).setScale(budgetKeyTable.getNumberOfDigits(),BigDecimal.ROUND_HALF_UP))) {
                    return false;
                }
            return true;
        }
        @Override
        public BigDecimal keySum(BudgetKeyTable budgetKeyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<BudgetKeyItem> it = budgetKeyTable.getBudgetKeyItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getTargetValue());
            }
            return sum.setScale(budgetKeyTable.getNumberOfDigits(), BigDecimal.ROUND_HALF_UP);
        }
        @Override
        public BigDecimal targetTotal() {
            return new BigDecimal(1000, MathContext.DECIMAL32);
        }
    },
    PERCENT {
        @Override
        public boolean isValid(BudgetKeyTable budgetKeyTable) {
            if (!this.keySum(budgetKeyTable).equals(new BigDecimal(100.000).setScale(budgetKeyTable.getNumberOfDigits(), BigDecimal.ROUND_HALF_UP))) {
                return false;
            }
            return true;
        }
        @Override
        public BigDecimal keySum(BudgetKeyTable budgetKeyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<BudgetKeyItem> it = budgetKeyTable.getBudgetKeyItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getTargetValue());
            }
            return sum.setScale(budgetKeyTable.getNumberOfDigits(), BigDecimal.ROUND_HALF_UP);
        }
        @Override
        public BigDecimal targetTotal() {
            return new BigDecimal(100, MathContext.DECIMAL32);
        }
    },
    DEFAULT {
         @Override
        public boolean isValid(BudgetKeyTable budgetKeyTable) {
            return true;
        }
        @Override
        public BigDecimal keySum(BudgetKeyTable budgetKeyTable) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Iterator<BudgetKeyItem> it = budgetKeyTable.getBudgetKeyItems().iterator(); it.hasNext();) {
                sum = sum.add(it.next().getTargetValue());
            }
            return sum;
        }
        @Override
        public BigDecimal targetTotal() {
            return null;
        }
    };

    public abstract BigDecimal targetTotal();

    public abstract boolean isValid(final BudgetKeyTable budgetKeyTable);

    public abstract BigDecimal keySum(final BudgetKeyTable budgetKeyTable);


}
