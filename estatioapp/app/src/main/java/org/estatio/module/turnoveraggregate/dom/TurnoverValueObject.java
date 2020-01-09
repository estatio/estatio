package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.poi.ss.formula.functions.T;
import org.joda.time.LocalDate;

import org.estatio.module.turnover.dom.Turnover;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
public class TurnoverValueObject implements Comparable{

    public TurnoverValueObject(final Turnover turnover){
        this.date = turnover.getDate();
        this.grossAmount = turnover.getGrossAmount();
        this.netAmount = turnover.getNetAmount();
        this.purchaseCount = turnover.getPurchaseCount();
        this.comments = turnover.getComments();
        this.nonComparable = turnover.isNonComparable();
        this.turnoverCount = 1;
    }

    public TurnoverValueObject(final TurnoverValueObject other){
        this.date = other.getDate();
        this.grossAmount = other.getGrossAmount();
        this.netAmount = other.getNetAmount();
        this.purchaseCount = other.getPurchaseCount();
        this.comments = other.getComments();
        this.nonComparable = other.isNonComparable();
        this.turnoverCount = other.getTurnoverCount();
    }

    private LocalDate date;
    private BigDecimal grossAmount;
    private BigDecimal netAmount;
    private BigInteger purchaseCount;
    private String comments;
    private boolean nonComparable;
    private int turnoverCount;

    public void add(final TurnoverValueObject t){
        if (this.getDate().equals(t.getDate())){
            if (this.getGrossAmount()!=null && t.getGrossAmount()!=null) {
                setGrossAmount(this.getGrossAmount().add(t.getGrossAmount()));
            } else {
                if (this.getGrossAmount()==null && t.getGrossAmount()!=null) setGrossAmount(t.getGrossAmount());
            }

            if (this.getNetAmount()!=null && t.getNetAmount()!=null) {
                setNetAmount(this.getNetAmount().add(t.getNetAmount()));
            } else {
                if (this.getNetAmount()==null && t.getNetAmount()!=null) setNetAmount(t.getNetAmount());
            }

            if (this.getPurchaseCount()!=null && t.getPurchaseCount()!=null) {
                setPurchaseCount(this.getPurchaseCount().add(t.getPurchaseCount()));
            } else {
                if (this.getPurchaseCount()==null && t.getPurchaseCount()!=null) setPurchaseCount(t.getPurchaseCount());
            }

            if (this.getComments()!=null && t.getComments()!=null) {
                setComments(this.getComments().concat(" | ").concat(t.getComments()));
            } else {
                if (this.getComments()==null && t.getPurchaseCount()!=null) setComments(t.getComments());
            }

            setNonComparable(this.nonComparable || t.nonComparable);

            setTurnoverCount(getTurnoverCount() + t.getTurnoverCount());

        }
    }

    public TurnoverValueObject addIgnoringDate(final TurnoverValueObject t){
            TurnoverValueObject copy = new TurnoverValueObject(t);
            copy.setDate(this.getDate());
            this.add(copy);
            return this;
    }


    @Override
    public int compareTo(final Object o) {
        TurnoverValueObject other = (TurnoverValueObject) o;
        return this.getDate().compareTo(other.getDate());
    }
}
