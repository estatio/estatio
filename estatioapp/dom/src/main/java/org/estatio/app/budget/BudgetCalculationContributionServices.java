package org.estatio.app.budget;

import java.math.BigDecimal;
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
import org.estatio.dom.budgeting.schedule.viewmodels.ValueAssignedToUnitLine;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BudgetCalculationContributionServices {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<ValueAssignedToUnitLine> distributionOverUnits(final ScheduleItem scheduleItem) {

        BigDecimal targetTotal = scheduleItem.getBudgetItem().getBudgetedValue()
                .multiply(scheduleItem.getPercentage())
                .divide(new BigDecimal(100));

        List<Distributable> input = new ArrayList<>();

        for (KeyItem keyItem : scheduleItem.getKeyTable().getItems()) {
            ValueAssignedToUnitLine valueAssignedToUnitLine =
                    new ValueAssignedToUnitLine(keyItem.getUnit(), BigDecimal.ONE, targetTotal.multiply(keyItem.getValue()));
            input.add(valueAssignedToUnitLine);
        }

        DistributionService distributionService = new DistributionService();
        distributionService.distribute(input, targetTotal, 2);

        return (List<ValueAssignedToUnitLine>)(Object) input;

    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<ValueAssignedToUnitLine> distributionOverUnits(final Schedule schedule) {

        //init
        BigDecimal overallTargetTotal = BigDecimal.ZERO;
        List<ValueAssignedToUnitLine> output = new ArrayList<>();

        for (ScheduleItem scheduleItem : schedule.getScheduleItems()) {

            overallTargetTotal = overallTargetTotal.add(
                    scheduleItem.getBudgetItem().getBudgetedValue()
                    .multiply(scheduleItem.getPercentage())
                    .divide(new BigDecimal(100))
            );

            List<ValueAssignedToUnitLine> scheduleItemOuput = distributionOverUnits(scheduleItem);

            for (ValueAssignedToUnitLine item : scheduleItemOuput) {

                boolean unitFound = false;
                for (ValueAssignedToUnitLine outputItem : output) {
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

        }

        return output;

    }

}
