package org.estatio.dom.lease;


import org.estatio.dom.tax.Tax;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

public enum DepositType {

    INDEXED_MGR_INCLUDING_VAT {
        @Override
        BigDecimal calculateDepositValue(final LeaseTermForDeposit term, final LocalDate date) {

            BigDecimal currentValue = BigDecimal.ZERO;
            List<LeaseItem> rentItems = term.getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);

            for (LeaseItem rentItem : rentItems) {
                Tax itemTax = rentItem.getTax();
                BigDecimal rentItemValueUntilVerificationDate = rentItem.valueForDate(date.minusDays(1));
                BigDecimal vatAmount = itemTax != null ?
                        itemTax.percentageFor(date.minusDays(1)).multiply(rentItemValueUntilVerificationDate).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP)
                        : BigDecimal.ZERO;
                if (rentItemValueUntilVerificationDate != null) {
                    currentValue = currentValue.add(rentItemValueUntilVerificationDate).add(vatAmount);
                }
            }

            return currentValue;
        }
    },
    BASE_MGR_INCLUDING_VAT {
        @Override
        BigDecimal calculateDepositValue(final LeaseTermForDeposit term, final LocalDate date) {

            BigDecimal currentValue = BigDecimal.ZERO;
            List<LeaseItem> rentItems = term.getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);

            for (LeaseItem rentItem : rentItems) {
                LeaseTermForIndexable termForIndexable = (LeaseTermForIndexable)rentItem.currentTerm(date.minusDays(1));
                Tax itemTax = rentItem.getTax();
                BigDecimal rentItemValueUntilVerificationDate = termForIndexable.getBaseValue();
                BigDecimal vatAmount = itemTax != null ?
                        itemTax.percentageFor(date.minusDays(1)).multiply(rentItemValueUntilVerificationDate).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP)
                        : BigDecimal.ZERO;
                if (rentItemValueUntilVerificationDate != null) {
                    currentValue = currentValue.add(rentItemValueUntilVerificationDate).add(vatAmount);
                }
            }

            return currentValue;
        }
    },
    INDEXED_MGR__EXCLUDING_VAT {
        @Override
        BigDecimal calculateDepositValue(final LeaseTermForDeposit term, final LocalDate date) {

            BigDecimal currentValue = BigDecimal.ZERO;
            List<LeaseItem> rentItems = term.getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);

            for (LeaseItem rentItem : rentItems) {
                BigDecimal rentItemValueUntilVerificationDate = rentItem.valueForDate(date.minusDays(1));
                if (rentItemValueUntilVerificationDate != null) {
                    currentValue = currentValue.add(rentItemValueUntilVerificationDate);
                }
            }

            return currentValue;
        }
    },
    BASE__MGR_EXCLUDING_VAT {
        @Override
        BigDecimal calculateDepositValue(final LeaseTermForDeposit term, final LocalDate date) {

            BigDecimal currentValue = BigDecimal.ZERO;
            List<LeaseItem> rentItems = term.getLeaseItem().getLease().findItemsOfType(LeaseItemType.RENT);

            for (LeaseItem rentItem : rentItems) {
                LeaseTermForIndexable termForIndexable = (LeaseTermForIndexable)rentItem.currentTerm(date.minusDays(1));
                BigDecimal rentItemValueUntilVerificationDate = termForIndexable.getBaseValue();
                if (rentItemValueUntilVerificationDate != null) {
                    currentValue = currentValue.add(rentItemValueUntilVerificationDate);
                }
            }

            return currentValue;
        }
    },
    MANUAL {
        @Override
        BigDecimal calculateDepositValue(LeaseTermForDeposit term, LocalDate date) {
            return BigDecimal.ZERO;
        }
    };

    abstract BigDecimal calculateDepositValue(final LeaseTermForDeposit term, final LocalDate date);

}
