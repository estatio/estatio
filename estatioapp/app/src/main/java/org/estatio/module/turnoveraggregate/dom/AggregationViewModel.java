package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.module.turnoveraggregate.dom.AggregationViewModel"
)
@XmlRootElement(name = "aggregationViewModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class AggregationViewModel {

    public String title(){
        final TitleBuffer buf = new TitleBuffer();
        buf.append("Aggregation");
        buf.append(aggregationDate);
        buf.append(config.getOccupancy());
        return buf.toString();
    }

    public AggregationViewModel(){}

    public AggregationViewModel(final TurnoverReportingConfig config, final LocalDate aggregationDate, final TurnoverAggregation aggregation){
        this.config = config;
        this.turnoverAggregation = aggregation;
        this.aggregationDate = aggregationDate;
    }

    public AggregationViewModel next(){
        final LocalDate nextAggDate = getAggregationDate().plusMonths(1);
        return new AggregationViewModel(getConfig(),
                nextAggDate, getTurnoverAggregation(nextAggDate));
    }

    public AggregationViewModel previous(){
        final LocalDate prevAggDate = getAggregationDate().minusMonths(1);
        return new AggregationViewModel(getConfig(),
                prevAggDate, getTurnoverAggregation(prevAggDate));
    }

    @Getter @Setter
    private TurnoverReportingConfig config;

    @Getter @Setter
    private TurnoverAggregation turnoverAggregation;

    @Getter @Setter
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate aggregationDate;

    @CollectionLayout(defaultView = "table")
    public List<AggregationViewModelLine> getResults(){
        return aggregationViewModelHelperService.getLines(getTurnoverAggregation());
    }

    public String getToDateThisYear(){
        if (getTurnoverAggregation()==null) return null;
        return aggregationViewModelHelperService.helperCurrentYear(getTurnoverAggregation().getAggregateToDate());
    }

    public String getToDatePreviousYear(){
        if (getTurnoverAggregation()==null) return null;
        return aggregationViewModelHelperService.helperPreviousYear(getTurnoverAggregation().getAggregateToDate());
    }

    public String getToDateComparable(){
        if (getTurnoverAggregation()==null) return null;
        return getTurnoverAggregation().getAggregateToDate().isComparable() ? "comparable" : "non comparable";
    }

    private TurnoverAggregation getTurnoverAggregation(final LocalDate aggregationDate) {
        return turnoverAggregationRepository.findUnique(config, aggregationDate);
    }

    @Inject
    @XmlTransient
    TurnoverAggregationRepository turnoverAggregationRepository;

    @Inject
    @XmlTransient
    AggregationViewModelHelperService aggregationViewModelHelperService;

}
