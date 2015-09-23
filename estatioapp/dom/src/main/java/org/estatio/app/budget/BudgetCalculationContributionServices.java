package org.estatio.app.budget;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.app.budget.viewmodels.BudgetCalculation;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BudgetCalculationContributionServices {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetCalculation> distributionOverUnits(final ScheduleItem scheduleItem) {

        BigDecimal targetTotal = percentageOf(scheduleItem.getBudgetItem().getBudgetedValue(), scheduleItem.getPercentage());

        List<Distributable> input = new ArrayList<>();

        for (KeyItem keyItem : scheduleItem.getKeyTable().getItems()) {
            BudgetCalculation valueAssignedToUnitLine =
                    new BudgetCalculation(keyItem.getUnit(), BigDecimal.ONE, targetTotal.multiply(keyItem.getValue()));
            input.add(valueAssignedToUnitLine);
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

    private List<BudgetCalculation> merge(final List<BudgetCalculation> output, final List<BudgetCalculation> scheduleItemOuput) {
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
