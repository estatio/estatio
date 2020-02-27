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
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

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
        this.date = date;
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
        final BudgetForecast forecastIfAny = budgetForecastRepositoryAndFactory.findUnique(getProject(), getDate());
        if (forecastIfAny!=null){
            forecastIfAny.calculateAmounts(); //TODO: this is prone to error!! Change!
            if (forecastIfAny.getSubmittedBy()!=null) return null; // Extra guard
            Lists.newArrayList(forecastIfAny.getItems()).forEach(fi->{
                Lists.newArrayList(fi.getTerms()).forEach(ft->{
                    result.add(new ForecastLineViewmodel(ft));
                });
            });
        } else {
            // create new forecast
            final BudgetForecast newForecast = budgetForecastRepositoryAndFactory.findOrCreate(getProject(), getDate());
            newForecast.calculateAmounts(); //TODO: this is prone to error!! Change!
            Lists.newArrayList(newForecast.getItems()).forEach(fi->{
                Lists.newArrayList(fi.getTerms()).forEach(ft->{
                    result.add(new ForecastLineViewmodel(ft));
                });
            });
        }
        return result.stream().sorted(Comparator.comparing(ForecastLineViewmodel::getChargeReference).thenComparing(ForecastLineViewmodel::getTermStartDate)).collect(
                Collectors.toList());
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public ForecastCreationManager addTermsUntil(final LocalDate date){
        final BudgetForecast forecastIfAny = budgetForecastRepositoryAndFactory.findUnique(getProject(), getDate());
        if (forecastIfAny==null) return new ForecastCreationManager(getProject(), getDate()); // THIS SHOULD NOT HAPPEN
        budgetForecastRepositoryAndFactory.addTermsUntil(forecastIfAny, date);
        return new ForecastCreationManager(getProject(), getDate());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob download(final String filename){
        WorksheetSpec forecastLineSpec = new WorksheetSpec(ForecastLineViewmodel.class, "forecastLines");
        WorksheetContent forecastLineContent = new WorksheetContent(getForecastLines(), forecastLineSpec);
        return excelService.toExcel(Arrays.asList(forecastLineContent), filename);
    }

    public String default0Download(){
        return "Budget forecast" + getProject().getReference() + " " + clockService.now().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public ForecastCreationManager upload(final Blob spreadSheet){
        excelService.fromExcel(spreadSheet, ForecastLineViewmodel.class, "forecastLines", Mode.RELAXED).forEach(imp->imp.importData(getProject(), getDate()));
        return new ForecastCreationManager(getProject(), getDate());
    }

    @Inject
    BudgetForecastRepositoryAndFactory budgetForecastRepositoryAndFactory;

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

}
