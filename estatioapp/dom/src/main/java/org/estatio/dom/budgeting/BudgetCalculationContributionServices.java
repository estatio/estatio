package org.estatio.dom.budgeting;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BudgetCalculationContributionServices {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculation> distributionOverUnits(final ScheduleItem scheduleItem) {

        BigDecimal targetTotal = percentageOf(scheduleItem.getBudgetItem().getBudgetedValue(), scheduleItem.getPercentage());

        List<Distributable> input = new ArrayList<>();

        BigDecimal keySum = scheduleItem.getKeyTable().getKeyValueMethod().keySum(scheduleItem.getKeyTable());

        for (KeyItem keyItem : scheduleItem.getKeyTable().getItems()) {

            BudgetCalculation valueAssignedToUnit;

            // case all values in keyTable are zero
            if (keySum.compareTo(BigDecimal.ZERO) == 0) {
               valueAssignedToUnit =
                        new BudgetCalculation(
                                keyItem.getUnit(),
                                BigDecimal.ONE,
                                BigDecimal.ZERO
                        );
            } else {

                valueAssignedToUnit =
                        new BudgetCalculation(
                                keyItem.getUnit(),
                                BigDecimal.ONE,
                                targetTotal.multiply(keyItem.getValue()).
                                        divide(keySum, MathContext.DECIMAL64)
                        );

            }

            input.add(valueAssignedToUnit);
        }

        DistributionService distributionService = new DistributionService();
        distributionService.distribute(input, targetTotal, 2);

        return (List<BudgetCalculation>)(Object) input;

    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculation> distributionOverUnits(final Schedule schedule) {

        List<BudgetCalculation> output = new ArrayList<>();

        for (ScheduleItem scheduleItem : schedule.getScheduleItems()) {
            output = merge(output, distributionOverUnits(scheduleItem));
        }
        return output;

    }

    List<BudgetCalculation> merge(final List<BudgetCalculation> output, final List<BudgetCalculation> scheduleItemOuput) {
        for (BudgetCalculation item : scheduleItemOuput) {
           boolean unitFound = false;
            for (BudgetCalculation outputItem : output) {
                if (!unitFound && outputItem.getUnit().equals(item.getUnit())) {
                    outputItem.setValue(outputItem.getValue().add(item.getValue()));
                    outputItem.setSourceValue(outputItem.getSourceValue().add(item.getSourceValue()));
                    unitFound=true;
                }
            }
            if (!unitFound) {
                output.add(item);
            }
        }
        return output;
    }

    private BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {
        return value
                .multiply(percentage)
                .divide(new BigDecimal(100), MathContext.DECIMAL64);
    }

}
