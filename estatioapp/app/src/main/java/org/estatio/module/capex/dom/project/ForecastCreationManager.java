package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.dom.project.ForecastCreationManager"
)
public class ForecastCreationManager {

    public ForecastCreationManager(){}

    public ForecastCreationManager(final Project project, final LocalDate date){
        this.project = project;
        this.date = ForecastFrequency.QUARTERLY.getStartDateFor(date); // just in case ...
    }

    public String title(){
        return TitleBuilder.start().withName("Forecast creation for ").withParent(getProject()).withName(getDate()).toString();
    }

    @Getter @Setter
    private Project project;

    @Getter @Setter
    private LocalDate date;

    @CollectionLayout(defaultView = "table")
    public List<ForecastLineViewmodel> getForecastLines(){
        List<ForecastLineViewmodel> result = new ArrayList<>();
        final BudgetForecast forecast = budgetForecastRepositoryAndFactory.findAndUpdateItemsIfNotSubmittedOrCreate(getProject(), getDate());
        if (forecast.getSubmittedOn()!=null) return null; // Extra guard
        Lists.newArrayList(forecast.getItems()).forEach(fi->{
            result.addAll(linesForForecastItem(fi));
        });
        return result.stream().sorted(Comparator.comparing(ForecastLineViewmodel::getChargeReference).thenComparing(
                ForecastLineViewmodel::getYear)).collect(
                Collectors.toList());
    }

    List<ForecastLineViewmodel> linesForForecastItem(final BudgetForecastItem item){
        List<ForecastLineViewmodel> result = new ArrayList<>();
        if (item.getTerms().isEmpty()) return result;

        final Integer minYear = Lists.newArrayList(item.getTerms()).stream()
                .map(BudgetForecastTerm::getStartDate).min(Comparator.comparing(LocalDate::getYear)).map(LocalDate::getYear).get();
        final Integer maxYear = Lists.newArrayList(item.getTerms()).stream()
                .map(BudgetForecastTerm::getStartDate).max(Comparator.comparing(LocalDate::getYear)).map(LocalDate::getYear).get();

        int i = minYear;
        while (i <= maxYear){
            ForecastLineViewmodel line = new ForecastLineViewmodel();
            line.setProjectReference(item.getForecast().getProject().getReference());
            line.setChargeReference(item.getProjectItem().getCharge().getReference());
            line.setYear(i);
            line.setForecastedAmount(item.getAmount());
            line.setForecastedAmountCovered(item.getForecastedAmountCovered());
            line.setSumTerms(item.getSumTerms());

            final int year = i;
            line.setAmountQ1(getAmountForQuarter(item, year, ForecastFrequency.Quarter.Q1));
            line.setAmountQ2(getAmountForQuarter(item, year, ForecastFrequency.Quarter.Q2));
            line.setAmountQ3(getAmountForQuarter(item, year, ForecastFrequency.Quarter.Q3));
            line.setAmountQ4(getAmountForQuarter(item, year, ForecastFrequency.Quarter.Q4));

            result.add(line);

            i++;
        }

        return result;
    }

    private BigDecimal getAmountForQuarter(final BudgetForecastItem item, final int year, final ForecastFrequency.Quarter q1) {
        return Lists.newArrayList(item.getTerms()).stream()
                .filter(t -> (t.getStartDate().getYear()) == year)
                .filter(t -> t.getForecastItem().getForecast().getFrequency().getQuarterFor(t.getStartDate())
                        .equals(q1))
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public ForecastCreationManager addTermsUntil(final int year){
        final BudgetForecast forecastIfAny = budgetForecastRepositoryAndFactory.findUnique(getProject(), getDate());
        if (forecastIfAny==null) return new ForecastCreationManager(getProject(), getDate()); // THIS SHOULD NOT HAPPEN
        budgetForecastRepositoryAndFactory.addTermsUntil(forecastIfAny, new LocalDate(year, 12, 31));
        return new ForecastCreationManager(getProject(), getDate());
    }

    public int default0AddTermsUntil(){
        return clockService.now().getYear() + 1;
    }


    @Action(semantics = SemanticsOf.SAFE)
    public Blob download(final String filename){
        WorksheetSpec forecastLineSpec = new WorksheetSpec(ForecastLineViewmodel.class, "forecastLines");
        WorksheetContent forecastLineContent = new WorksheetContent(getForecastLines(), forecastLineSpec);
        return excelService.toExcel(Arrays.asList(forecastLineContent), filename);
    }

    public String default0Download(){
        return "Budget forecast" + getProject().getReference() + " " + getDate().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public BudgetForecast upload(final Blob spreadSheet){
        excelService.fromExcel(spreadSheet, ForecastLineViewmodel.class, "forecastLines", Mode.RELAXED).forEach(imp->imp.importData(getProject(), getDate()));
        return budgetForecastRepositoryAndFactory.findUnique(getProject(), getDate());
    }

    @Inject
    BudgetForecastRepositoryAndFactory budgetForecastRepositoryAndFactory;

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

}
