package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.module.turnoveraggregate.dom.AggregationViewModelLine"
)
@XmlRootElement(name = "aggregationViewModel")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class AggregationViewModelLine {

    @Getter
    @MemberOrder(sequence = "1")
    private String title;

    @Getter
    @MemberOrder(sequence = "2")
    private String agg1M;

    @Getter
    @MemberOrder(sequence = "3")
    private String agg2M;

    @Getter
    @MemberOrder(sequence = "4")
    private String agg3M;

    @Getter
    @MemberOrder(sequence = "5")
    private String agg6M;

    @Getter
    @MemberOrder(sequence = "6")
    private String agg9M;

    @Getter
    @MemberOrder(sequence = "7")
    private String agg12M;

}
