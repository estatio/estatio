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

    public BigDecimal calculateTotalPonderingAreaForUnitWithSpecifiedCoefficients(final Unit unit, final PonderingAreaCoefficients specifiedCoefficients) {
        if (specifiedCoefficients!=null) {
            return calculateTotalPonderingAreaForUnit(unit, specifiedCoefficients);
        }

        // coefficients not specified, choose corresponding one for unit type
        final PonderingAreaCoefficients coefficients;
        if (unit.getType().equals(UnitType.HYPERMARKET)) {
            coefficients = PonderingAreaCoefficients.FOR_HYPERMARKET;
        } else {
            coefficients = PonderingAreaCoefficients.DEFAULT;
        }

        return calculateTotalPonderingAreaForUnit(unit, coefficients);
    }

    private BigDecimal calculateTotalPonderingAreaForUnit(final Unit unit, final PonderingAreaCoefficients coefficients) {
        BigDecimal totalPonderingArea = BigDecimal.ZERO;
        // Calculate pondering area for storage if possible
        if (unit.getStorageArea()!=null) {
            totalPonderingArea = totalPonderingArea.add(
                    calculatePonderingAreaForUnitAreaType(unit.getStorageArea(), coefficients.getStorageAreaCoefficients()));
        }
        // Calculates pondering area for sales if possible
        if (unit.getSalesArea()!=null) {
            totalPonderingArea = totalPonderingArea.add(
                    calculatePonderingAreaForUnitAreaType(unit.getSalesArea(), coefficients.getSalesAreaCoefficients()));
        }

        return totalPonderingArea;
    }

    private BigDecimal calculatePonderingAreaForUnitAreaType(BigDecimal areaRemaining, final List<PonderingAreaCoefficients.Tuple> tuples) {
        BigDecimal ponderingArea = BigDecimal.ZERO;
        for (PonderingAreaCoefficients.Tuple t : tuples) {
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


    public enum PonderingAreaCoefficients {
        DEFAULT(
                Arrays.asList(
                        new PonderingAreaCoefficients.Tuple(bd("350.0"), bd("1.00")),
                        new PonderingAreaCoefficients.Tuple(null, bd("0.60"))),
                Arrays.asList(
                        new PonderingAreaCoefficients.Tuple(null, bd("0.40"))
                )),
        FOR_HYPERMARKET(
                Arrays.asList(
                        new PonderingAreaCoefficients.Tuple(bd("1000.0"), bd("1.00")),
                        new PonderingAreaCoefficients.Tuple(bd("1000.0"), bd("0.90")),
                        new PonderingAreaCoefficients.Tuple(bd("2000.0"), bd("0.80")),
                        new PonderingAreaCoefficients.Tuple(bd("2000.0"), bd("0.70")),
                        new PonderingAreaCoefficients.Tuple(null, bd("0.60"))),
                Arrays.asList(
                        new PonderingAreaCoefficients.Tuple(null, bd("0.40"))
                ));

        @Getter
        private List<PonderingAreaCoefficients.Tuple> salesAreaCoefficients;

        @Getter
        private List<PonderingAreaCoefficients.Tuple> storageAreaCoefficients;

        PonderingAreaCoefficients(final List<PonderingAreaCoefficients.Tuple> salesAreaCoefficients, final List<PonderingAreaCoefficients.Tuple> storageAreaCoefficients) {
            this.salesAreaCoefficients = salesAreaCoefficients;
            this.storageAreaCoefficients = storageAreaCoefficients;
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
