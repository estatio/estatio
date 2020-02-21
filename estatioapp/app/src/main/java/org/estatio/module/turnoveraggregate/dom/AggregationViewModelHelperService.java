package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AggregationViewModelHelperService {

    public List<AggregationViewModelLine> getLines(final TurnoverAggregation aggregation){
        List<AggregationViewModelLine> result = new ArrayList<>();
        if (aggregation!=null) {
            final List<String> valsCY = aggregation.aggregatesForPeriod().stream().map(a -> helperCurrentYear(a))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("amount current year", valsCY.get(0), valsCY.get(1), valsCY.get(2), valsCY.get(3), valsCY.get(4), valsCY.get(5)));

            final List<String> valsPY = aggregation.aggregatesForPeriod().stream().map(a -> helperPreviousYear(a))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("amount previous year", valsPY.get(0), valsPY.get(1), valsPY.get(2), valsPY.get(3), valsPY.get(4), valsPY.get(5)));

            final List<String> valsCom = aggregation.aggregatesForPeriod().stream().map(a -> helperComparable(a))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("amount", valsCom.get(0), valsCom.get(1), valsCom.get(2), valsCom.get(3), valsCom.get(4), valsCom.get(5)));

            final List<String> cntCY = aggregation.purchaseCountAggregatesForPeriod().stream().map(p -> helperCountCurrentYear(p))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("count current year", cntCY.get(0), "", cntCY.get(1), cntCY.get(2), "", cntCY.get(3)));

            final List<String> cntPY = aggregation.purchaseCountAggregatesForPeriod().stream().map(p -> helperCountPreviousYear(p))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("count previous year", cntPY.get(0), "", cntPY.get(1), cntPY.get(2), "", cntPY.get(3)));

            final List<String> cntCom = aggregation.purchaseCountAggregatesForPeriod().stream().map(p -> helperComparable(p))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("count", cntCom.get(0), "", cntCom.get(1), cntCom.get(2), "", cntCom.get(3)));
        }

        return result;
    }

    public String helperCurrentYear(final TurnoverAggregateForPeriod aggregateForPeriod){
        if (aggregateForPeriod==null) return "---";
        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        builder.append("Gross ");
        final BigDecimal grossAmount = aggregateForPeriod.getGrossAmount();
        builder.append(df.format(grossAmount !=null ? grossAmount : BigDecimal.ZERO));
        builder.append(" | Net ");
        final BigDecimal netAmount = aggregateForPeriod.getNetAmount();
        builder.append(df.format(netAmount != null ? netAmount : BigDecimal.ZERO));
        return builder.toString();
    }

    public String helperPreviousYear(final TurnoverAggregateForPeriod aggregateForPeriod){
        if (aggregateForPeriod==null) return "---";
        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        builder.append("Gross ");
        final BigDecimal grossAmount = aggregateForPeriod.getGrossAmountPreviousYear();
        builder.append(df.format(grossAmount !=null ? grossAmount : BigDecimal.ZERO));
        builder.append(" | Net ");
        final BigDecimal netAmount = aggregateForPeriod.getNetAmountPreviousYear();
        builder.append(df.format(netAmount != null ? netAmount : BigDecimal.ZERO));
        return builder.toString();
    }

    public String helperComparable(final TurnoverAggregateForPeriod aggregateForPeriod){
        return aggregateForPeriod.isComparable() ? "comparable" : "non comparable";
    }

    public String helperCountCurrentYear(final PurchaseCountAggregateForPeriod aggregateForPeriod){
        final BigInteger count = aggregateForPeriod.getCount();
        return count != null ? count.toString() : "0";
    }

    public String helperCountPreviousYear(final PurchaseCountAggregateForPeriod aggregateForPeriod){
        final BigInteger count = aggregateForPeriod.getCountPreviousYear();
        return count != null ? count.toString() : "0";
    }

    public String helperComparable(final PurchaseCountAggregateForPeriod aggregateForPeriod){
        return aggregateForPeriod.isComparable() ? "comparable" : "non comparable";
    }

    public String helperCurrentYear(final TurnoverAggregateToDate aggregateToDate){
        if (aggregateToDate==null) return "---";
        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        builder.append("Gross ");
        final BigDecimal grossAmount = aggregateToDate.getGrossAmount();
        builder.append(df.format(grossAmount !=null ? grossAmount : BigDecimal.ZERO));
        builder.append(" | Net ");
        final BigDecimal netAmount = aggregateToDate.getNetAmount();
        builder.append(df.format(netAmount != null ? netAmount : BigDecimal.ZERO));
        return builder.toString();
    }


    public String helperPreviousYear(final TurnoverAggregateToDate aggregateToDate){
        if (aggregateToDate==null) return "---";
        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        builder.append("Gross ");
        final BigDecimal grossAmount = aggregateToDate.getGrossAmountPreviousYear();
        builder.append(df.format(grossAmount !=null ? grossAmount : BigDecimal.ZERO));
        builder.append(" | Net ");
        final BigDecimal netAmount = aggregateToDate.getNetAmountPreviousYear();
        builder.append(df.format(netAmount != null ? netAmount : BigDecimal.ZERO));
        return builder.toString();
    }

}
