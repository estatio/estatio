package org.estatio.module.budget.dom.ponderingareacalculation;


import lombok.Getter;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitType;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.incode.module.base.integtests.VT.bd;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PonderingAreaCalculationService {

    // TODO: Decide whether you should be able to apply specific coefficient rules for a unit
//    public BigDecimal calculateTotalPonderingAreaForUnitWithSpecifiedRules(final Unit unit, final PonderingAreaCoefficientRules specifiedRules) {
//        if (specifiedRules!=null) {
//            return calculateTotalPonderingAreaForUnitIfPossible(unit, specifiedRules);
//        }
//
//        // coefficients not specified, choose corresponding one for unit type
//        final PonderingAreaCoefficientRules rules;
//        if (unit.getType().equals(UnitType.HYPERMARKET)) {
//            rules = PonderingAreaCoefficientRules.FOR_HYPERMARKET;
//        } else {
//            rules = PonderingAreaCoefficientRules.DEFAULT;
//        }
//
//        return calculateTotalPonderingAreaForUnitIfPossible(unit, rules);
//    }

    private boolean areaIsDividedForUnit(Unit unit) {
        return unit.getStorageArea()!=null || unit.getSalesArea()!=null;
    }

    public BigDecimal calculateTotalPonderingAreaForUnitIfPossible(final Unit unit) {
        // If area of unit is not divided then pondering area calculation is not possible; just return the GLA
        if (!areaIsDividedForUnit(unit)) {
            return unit.getArea();
        }

        final PonderingAreaCoefficientRules rules;
        if (unit.getType().equals(UnitType.HYPERMARKET)) {
            rules = PonderingAreaCoefficientRules.FOR_HYPERMARKET;
        } else {
            rules = PonderingAreaCoefficientRules.DEFAULT;
        }

        BigDecimal totalPonderingArea = BigDecimal.ZERO;
        // Calculate pondering area for storage if possible
        if (unit.getStorageArea()!=null) {
            totalPonderingArea = totalPonderingArea.add(
                    calculatePonderingAreaForUnitAreaType(unit.getStorageArea(), rules.getStorageAreaCoefficientRule()));
        }
        // Calculates pondering area for sales if possible
        if (unit.getSalesArea()!=null) {
            totalPonderingArea = totalPonderingArea.add(
                    calculatePonderingAreaForUnitAreaType(unit.getSalesArea(), rules.getSalesAreaCoefficientRule()));
        }

        return totalPonderingArea;
    }

    private BigDecimal calculatePonderingAreaForUnitAreaType(BigDecimal areaRemaining, final List<PonderingAreaCoefficientRules.Tuple> rule) {
        BigDecimal ponderingArea = BigDecimal.ZERO;
        for (PonderingAreaCoefficientRules.Tuple t : rule) {
            if (t.area!=null) {
                if (areaRemaining.subtract(t.area).compareTo(BigDecimal.ZERO) <= 0) {
                    // Area remaining is less than or equal to the tuple area value; multiply the remainder of the area by the corresponding coefficient and add it to result
                    return ponderingArea.add(areaRemaining.multiply(t.coefficient));
                } else {
                    // Enough area remaining for the tuple area value to 'take' from; multiply the until value by the corresponding coefficient and add it to result
                    ponderingArea = ponderingArea.add(t.area.multiply(t.coefficient));
                    areaRemaining = areaRemaining.subtract(t.area);
                }
            } else {
                // No tuple area value; multiply the remainder of the area by the corresponding coefficient and add it to result
                return ponderingArea.add(areaRemaining.multiply(t.coefficient));
            }
        }

        return ponderingArea;
    }


    private enum PonderingAreaCoefficientRules {
        DEFAULT(
                Arrays.asList(
                        new Tuple(bd("350.0"), bd("1.00")),
                        new Tuple(null, bd("0.60"))),
                Arrays.asList(
                        new Tuple(null, bd("0.40"))
                )),
        FOR_HYPERMARKET(
                Arrays.asList(
                        new Tuple(bd("1000.0"), bd("1.00")),
                        new Tuple(bd("1000.0"), bd("0.90")),
                        new Tuple(bd("2000.0"), bd("0.80")),
                        new Tuple(bd("2000.0"), bd("0.70")),
                        new Tuple(null, bd("0.60"))),
                Arrays.asList(
                        new Tuple(null, bd("0.40"))
                ));

        @Getter
        private List<Tuple> salesAreaCoefficientRule;

        @Getter
        private List<Tuple> storageAreaCoefficientRule;

        PonderingAreaCoefficientRules(final List<Tuple> salesAreaCoefficientRule, final List<Tuple> storageAreaCoefficientRule) {
            this.salesAreaCoefficientRule = salesAreaCoefficientRule;
            this.storageAreaCoefficientRule = storageAreaCoefficientRule;
        }

        private static class Tuple {
            public final BigDecimal area;
            public final BigDecimal coefficient;

            public Tuple(@Nullable BigDecimal area, BigDecimal coefficient) {
                this.area = area;
                this.coefficient = coefficient;
            }
        }
    }
}
