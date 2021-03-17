package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.lease.dom.occupancy.Occupancy;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AggregationViewModelHelperService {

    public List<AggregationViewModelLine> getLines(final TurnoverAggregation aggregation){
        List<AggregationViewModelLine> result = new ArrayList<>();
        if (aggregation!=null) {
            final List<String> valsCY = aggregation.aggregatesForPeriod().stream().map(a -> helperAggregateForPeriod(a, false))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("turnover", valsCY.get(0), valsCY.get(1), valsCY.get(2), valsCY.get(3), valsCY.get(4), valsCY.get(5)));

            final List<String> valsPY = aggregation.aggregatesForPeriod().stream().map(a -> helperAggregateForPeriod(a,true))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("turnover previous year", valsPY.get(0), valsPY.get(1), valsPY.get(2), valsPY.get(3), valsPY.get(4), valsPY.get(5)));

            final List<String> valsCom = aggregation.aggregatesForPeriod().stream().map(a -> helperComparable(a))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("turnover comparable", valsCom.get(0), valsCom.get(1), valsCom.get(2), valsCom.get(3), valsCom.get(4), valsCom.get(5)));

            final List<String> cntCY = aggregation.purchaseCountAggregatesForPeriod().stream().map(p -> helperCount(p, false))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("purchase count", cntCY.get(0), "", cntCY.get(1), cntCY.get(2), "", cntCY.get(3)));

            final List<String> cntPY = aggregation.purchaseCountAggregatesForPeriod().stream().map(p -> helperCount(p, true))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("purchase count previous year", cntPY.get(0), "", cntPY.get(1), cntPY.get(2), "", cntPY.get(3)));

            final List<String> cntCom = aggregation.purchaseCountAggregatesForPeriod().stream().map(p -> helperComparable(p))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("purchase count comparable", cntCom.get(0), "", cntCom.get(1), cntCom.get(2), "", cntCom.get(3)));

            final List<String> leasesInvolved = aggregation.aggregatesForPeriod().stream().map(p -> helperLeaseInvolved(aggregation, p))
                    .collect(Collectors.toList());
            result.add(new AggregationViewModelLine("leases involved", leasesInvolved.get(0), leasesInvolved.get(1), leasesInvolved.get(2), leasesInvolved.get(3), leasesInvolved.get(4), leasesInvolved.get(5)));
        }

        return result;
    }

    public String helperAggregateForPeriod(final TurnoverAggregateForPeriod aggregateForPeriod, final boolean previousYear){
        if (aggregateForPeriod==null) return "---";
        StringBuilder builder = new StringBuilder();
        builder.append("Gross ");
        builder.append(formatAmount(previousYear ? aggregateForPeriod.getGrossAmountPreviousYear() : aggregateForPeriod.getGrossAmount()));
        builder.append(" | Net ");
        builder.append(formatAmount(previousYear ? aggregateForPeriod.getNetAmountPreviousYear() : aggregateForPeriod.getNetAmount()));
        return builder.toString();
    }

    public String helperComparable(final TurnoverAggregateForPeriod aggregateForPeriod){
        return aggregateForPeriod.isComparable() ? "comparable" : "non comparable";
    }

    public String helperCount(final PurchaseCountAggregateForPeriod aggregateForPeriod, final boolean previousYear){
        final BigInteger count = previousYear ? aggregateForPeriod.getCountPreviousYear() : aggregateForPeriod.getCount();
        return count != null ? count.toString() : "---";
    }

    public String helperComparable(final PurchaseCountAggregateForPeriod aggregateForPeriod){
        return aggregateForPeriod.isComparable() ? "comparable" : "non comparable";
    }

    public String helperAggregateToDate(final TurnoverAggregateToDate aggregateToDate, final boolean previousYear){
        if (aggregateToDate==null) return "---";
        StringBuilder builder = new StringBuilder();
        builder.append("Gross ");
        builder.append(formatAmount(previousYear ? aggregateToDate.getGrossAmountPreviousYear() : aggregateToDate.getGrossAmount()));
        builder.append(" | Net ");
        builder.append(formatAmount(previousYear ? aggregateToDate.getNetAmountPreviousYear() : aggregateToDate.getNetAmount()));
        return builder.toString();
    }

    private String formatAmount(final BigDecimal amount) {
        if (amount != null) {
            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(2);
            return df.format(amount);
        } else {
            return "---";
        }
    }

    public String helperLeaseInvolved(final TurnoverAggregation aggregation, final TurnoverAggregateForPeriod aggregateForPeriod){
        if (aggregateForPeriod==null) return "---";
        final List<Occupancy> occupancies = aggregateForPeriod.distinctOccupanciesThisYear(aggregation);
        occupancies.addAll(aggregateForPeriod.distinctOccupanciesPreviousYear(aggregation));
        final List<String> leaseRefs = occupancies.stream()
                .map(o->o.getLease())
                .distinct().map(l -> l.getReference())
                .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        if (leaseRefs.isEmpty()){
            return "---";
        } else {
            boolean first = true;
            for (String leaseRef : leaseRefs){
                if (!first) builder.append(" | ");
                builder.append(leaseRef);
                first = false;
            }
        }
        return builder.toString();
    }

}
