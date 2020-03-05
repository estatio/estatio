package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.message.MessageService2;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.dom.project.ForecastLineViewmodel"
)
@NoArgsConstructor
public class ForecastLineViewmodel {

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String projectReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String chargeReference;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private BigDecimal forecastedAmount;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private int year;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private BigDecimal amountQ1;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal amountQ2;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private BigDecimal amountQ3;

    @Getter @Setter
    @MemberOrder(sequence = "8")
    private BigDecimal amountQ4;

    @Getter @Setter
    @MemberOrder(sequence = "9")
    private boolean forecastedAmountCovered;

    @Getter @Setter
    @MemberOrder(sequence = "10")
    private BigDecimal sumTerms;

    public void importData(final Project project, final LocalDate forecastDate) {

        final BudgetForecast forecast = budgetForecastRepositoryAndFactory.findAndUpdateItemsIfNotSubmittedOrCreate(project, forecastDate);
        if (forecast.getSubmittedOn()!=null) {
            messageService2.raiseError(String.format("Forecast for %s is submitted and cannot be changed", forecast.getDate()));
            return;
        }
        final Charge charge = chargeRepository.findByReference(chargeReference);
        if (charge==null) {
            messageService2.raiseError(String.format("Charge with reference %s not found", getChargeReference()));
            return;
        }
        final ProjectItem itemForCharge = project.findItemForCharge(charge);
        if (itemForCharge==null){
            messageService2.raiseError(String.format("Project item with charge %s not found for project %s", getChargeReference(), project.getReference()));
            return;
        }
        final BudgetForecastItem forecastItem = forecast.findItemFor(itemForCharge);
        if (forecastItem==null){
            messageService2.raiseError(String.format("Forecast item with charge %s not found for project %s and date %s", getChargeReference(), project.getReference(), forecastDate));
            return;
        }

        budgetForecastRepositoryAndFactory.addTermsUntil(forecast, new LocalDate(getYear(), 12, 31)); // we allways try to create for the year and all items

        final BudgetForecastTerm termForQ1 = getTermForQ(forecastItem, ForecastFrequency.Quarter.Q1);
        if (termForQ1!=null && getAmountQ1()!=null) {
            termForQ1.setAmount(getAmountQ1());
        }
        final BudgetForecastTerm termForQ2 = getTermForQ(forecastItem, ForecastFrequency.Quarter.Q2);
        if (termForQ2!=null && getAmountQ2()!=null) {
            termForQ2.setAmount(getAmountQ2());
        }
        final BudgetForecastTerm termForQ3 = getTermForQ(forecastItem, ForecastFrequency.Quarter.Q3);
        if (termForQ3!=null && getAmountQ3()!=null) {
            termForQ3.setAmount(getAmountQ3());
        }
        final BudgetForecastTerm termForQ4 = getTermForQ(forecastItem, ForecastFrequency.Quarter.Q4);
        if (termForQ4!=null && getAmountQ4()!=null) {   // termForQ4 should never be null actually
            termForQ4.setAmount(getAmountQ4());
        }
    }

    private BudgetForecastTerm getTermForQ(final BudgetForecastItem forecastItem, final ForecastFrequency.Quarter q) {
        final BudgetForecastTerm term = forecastItem
                .findTermForDate(ForecastFrequency.getStartDateForQuarter(getYear(), q));
        return term;
    }

    @Inject
    private BudgetForecastRepositoryAndFactory budgetForecastRepositoryAndFactory;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private MessageService2 messageService2;

}
