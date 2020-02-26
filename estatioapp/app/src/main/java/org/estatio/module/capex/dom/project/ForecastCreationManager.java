package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

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
        this.frequency = ForecastFrequency.QUARTERLY; // Currently the only implementation
    }

    @Getter @Setter
    private Project project;

    @Getter @Setter
    private LocalDate date;

    @Getter @Setter
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private ForecastFrequency frequency;

    @CollectionLayout(defaultView = "table")
    public List<ForecastLineViewmodel> getForecastLines(){
        List<ForecastLineViewmodel> result = new ArrayList<>();
        final BudgetForecast forecastIfAny = budgetForecastRepository.findUnique(getProject(), getDate());
        if (forecastIfAny!=null){
            if (forecastIfAny.getApprovedOn()!=null) return null; // Extra guard
            Lists.newArrayList(forecastIfAny.getItems()).forEach(fi->{
                Lists.newArrayList(fi.getTerms()).forEach(ft->{
                    result.add(new ForecastLineViewmodel(ft));
                });
            });
        } else {
            // create and initialize new forecast
            final BudgetForecast newForecast = budgetForecastRepository.findOrCreate(getProject(), getDate());
            Lists.newArrayList(project.getProjectBudget().getItems()).forEach(bi->{
                // TODO: calculate amounts and bring responsibility to repository (treat it as factory)
                final BudgetForecastItem newForecastItem = budgetForecastRepository
                        .findOrCreateItem(newForecast, bi.getProjectItem(), BigDecimal.ZERO, BigDecimal.ZERO,
                                BigDecimal.ZERO);
                final BudgetForecastItemTerm newTerm = budgetForecastRepository.findOrCreateTerm(newForecastItem,
                getFrequency().getIntervalFor(getDate()), BigDecimal.ZERO);
                result.add(new ForecastLineViewmodel(newTerm));
            });
        }
        return result.stream().sorted(Comparator.comparing(ForecastLineViewmodel::getChargeReference).thenComparing(ForecastLineViewmodel::getTermStartDate)).collect(
                Collectors.toList());
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public ForecastCreationManager addTermsUntil(final LocalDate date){
        final LocalDate startDateToUse = ForecastFrequency.QUARTERLY.getStartDateFor(date);
        final BudgetForecast forecastIfAny = budgetForecastRepository.findUnique(getProject(), getDate());
        if (forecastIfAny==null) return new ForecastCreationManager(getProject(), getDate()); // THIS SHOULD NOT HAPPEN
        Lists.newArrayList(forecastIfAny.getItems()).forEach(fi->{
            LocalDate d = getDate();
            while (!d.isAfter(startDateToUse)){
                final LocalDateInterval interval = getFrequency().getIntervalFor(d);
                budgetForecastRepository.findOrCreateTerm(fi, interval, BigDecimal.ZERO);
                d = interval.endDateExcluding();
            }
        });
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

    @Inject
    BudgetForecastRepository budgetForecastRepository;

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

}
