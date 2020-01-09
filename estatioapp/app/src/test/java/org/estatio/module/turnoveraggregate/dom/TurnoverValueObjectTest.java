package org.estatio.module.turnoveraggregate.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.formula.functions.T;
import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.module.turnover.dom.Turnover;

import static org.junit.Assert.*;

public class TurnoverValueObjectTest {

    @Test
    public void xxx() throws Exception {
        TurnoverValueObject tvo;

        // given
        Turnover t = new Turnover();
        // when
        tvo = new TurnoverValueObject(t);
        // then
        Assertions.assertThat(tvo.getDate()).isEqualTo(t.getDate());
        Assertions.assertThat(tvo.getComments()).isEqualTo(t.getComments());
        Assertions.assertThat(tvo.getGrossAmount()).isEqualTo(t.getGrossAmount());
        Assertions.assertThat(tvo.getNetAmount()).isEqualTo(t.getNetAmount());
        Assertions.assertThat(tvo.getPurchaseCount()).isEqualTo(t.getPurchaseCount());
        Assertions.assertThat(tvo.isNonComparable()).isEqualTo(t.isNonComparable());

        // when
        t.setDate(new LocalDate(2020,1,1));
        t.setComments("comments");
        t.setGrossAmount(new BigDecimal("1.23"));
        t.setNetAmount(new BigDecimal("1.00"));
        t.setPurchaseCount(new BigInteger("123"));
        t.setNonComparable(true);
        tvo = new TurnoverValueObject(t);
        // then
        Assertions.assertThat(tvo.getDate()).isEqualTo(t.getDate());
        Assertions.assertThat(tvo.getDate()).isEqualTo(new LocalDate(2020,1,1));
        Assertions.assertThat(tvo.getComments()).isEqualTo(t.getComments());
        Assertions.assertThat(tvo.getGrossAmount()).isEqualTo(t.getGrossAmount());
        Assertions.assertThat(tvo.getNetAmount()).isEqualTo(t.getNetAmount());
        Assertions.assertThat(tvo.getPurchaseCount()).isEqualTo(t.getPurchaseCount());
        Assertions.assertThat(tvo.isNonComparable()).isEqualTo(t.isNonComparable());
        Assertions.assertThat(tvo.getTurnoverCount()).isEqualTo(1);

    }

    @Test
    public void add() {

        TurnoverValueObject tvo;
        TurnoverValueObject tvoAdd1;
        TurnoverValueObject tvoAdd2;
        TurnoverValueObject tvoAdd3;

        // given
        Turnover t = new Turnover();
        t.setDate(new LocalDate(2020,1,1));
        t.setComments("comments");
        t.setGrossAmount(new BigDecimal("1.23"));
        t.setNetAmount(new BigDecimal("1.00"));
        t.setPurchaseCount(new BigInteger("123"));
        t.setNonComparable(false);
        tvo = new TurnoverValueObject(t);

        Turnover t1 = new Turnover();
        t1.setDate(new LocalDate(2020,1,1));
        t1.setComments("comments2");
        t1.setGrossAmount(new BigDecimal("2.34"));
        tvoAdd1 = new TurnoverValueObject(t1);

        Turnover t2 = new Turnover();
        t2.setDate(new LocalDate(2020,1,1));
        t2.setNetAmount(new BigDecimal("1.23"));
        t2.setPurchaseCount(new BigInteger("234"));
        t2.setNonComparable(true);
        tvoAdd2 = new TurnoverValueObject(t2);

        tvoAdd3 = new TurnoverValueObject(t2);
        tvoAdd3.setDate(new LocalDate(2020,2,1));

        // when
        tvo.add(tvoAdd1);
        // then
        Assertions.assertThat(tvo.getComments()).isEqualTo("comments | comments2");
        Assertions.assertThat(tvo.getGrossAmount()).isEqualTo(new BigDecimal("3.57"));
        Assertions.assertThat(tvo.getNetAmount()).isEqualTo(t.getNetAmount());
        Assertions.assertThat(tvo.getPurchaseCount()).isEqualTo(t.getPurchaseCount());
        Assertions.assertThat(tvo.isNonComparable()).isEqualTo(t.isNonComparable());
        Assertions.assertThat(tvo.getTurnoverCount()).isEqualTo(2);

        // when
        tvo.add(tvoAdd2);
        // then
        Assertions.assertThat(tvo.getComments()).isEqualTo("comments | comments2");
        Assertions.assertThat(tvo.getGrossAmount()).isEqualTo(new BigDecimal("3.57"));
        Assertions.assertThat(tvo.getNetAmount()).isEqualTo(new BigDecimal("2.23"));
        Assertions.assertThat(tvo.getPurchaseCount()).isEqualTo(new BigInteger("357"));
        Assertions.assertThat(tvo.isNonComparable()).isEqualTo(true);
        Assertions.assertThat(tvo.getTurnoverCount()).isEqualTo(3);

        // and when
        tvo.add(tvoAdd3);
        // then nothing changes for dates are different
        Assertions.assertThat(tvo.getComments()).isEqualTo("comments | comments2");
        Assertions.assertThat(tvo.getGrossAmount()).isEqualTo(new BigDecimal("3.57"));
        Assertions.assertThat(tvo.getNetAmount()).isEqualTo(new BigDecimal("2.23"));
        Assertions.assertThat(tvo.getPurchaseCount()).isEqualTo(new BigInteger("357"));
        Assertions.assertThat(tvo.isNonComparable()).isEqualTo(true);
        Assertions.assertThat(tvo.getTurnoverCount()).isEqualTo(3);

    }

    @Test
    public void addIgnoringDate_works() throws Exception {

        TurnoverValueObject tvo;
        TurnoverValueObject tvoAdd1;
        TurnoverValueObject tvoAdd2;

        // given
        Turnover t = new Turnover();
        t.setDate(new LocalDate(2020,1,1));
        t.setComments("comments");
        t.setGrossAmount(new BigDecimal("1.23"));
        t.setNetAmount(new BigDecimal("1.00"));
        t.setPurchaseCount(new BigInteger("123"));
        t.setNonComparable(false);
        tvo = new TurnoverValueObject(t);

        Turnover t1 = new Turnover();
        t1.setDate(new LocalDate(2020,2,1));
        t1.setComments("comments2");
        t1.setGrossAmount(new BigDecimal("2.34"));
        tvoAdd1 = new TurnoverValueObject(t1);

        Turnover t2 = new Turnover();
        t2.setDate(new LocalDate(2020,1,1));
        t2.setNetAmount(new BigDecimal("1.23"));
        t2.setPurchaseCount(new BigInteger("234"));
        t2.setNonComparable(true);
        tvoAdd2 = new TurnoverValueObject(t2);

        // when
        tvo.addIgnoringDate(tvoAdd1);

        // then
        Assertions.assertThat(tvo.getComments()).isEqualTo("comments | comments2");
        Assertions.assertThat(tvo.getGrossAmount()).isEqualTo(new BigDecimal("3.57"));
        Assertions.assertThat(tvo.getNetAmount()).isEqualTo(t.getNetAmount());
        Assertions.assertThat(tvo.getPurchaseCount()).isEqualTo(t.getPurchaseCount());
        Assertions.assertThat(tvo.isNonComparable()).isEqualTo(t.isNonComparable());
        Assertions.assertThat(tvo.getTurnoverCount()).isEqualTo(2);
        Assertions.assertThat(tvo.getDate()).isEqualTo(t.getDate());

        // still!!
        Assertions.assertThat(tvoAdd1.getDate()).isEqualTo(t1.getDate());

        // and when
        List<TurnoverValueObject> listWithOriginals = new ArrayList<>();
        listWithOriginals.add(tvo);
        listWithOriginals.add(tvoAdd1);
        listWithOriginals.add(tvoAdd2);

        final TurnoverValueObject obj = listWithOriginals.stream()
                .reduce(TurnoverValueObject::addIgnoringDate).get();

        // then
        Assertions.assertThat(obj.getDate()).isEqualTo(tvo.getDate());
        Assertions.assertThat(obj.getGrossAmount()).isEqualTo(new BigDecimal("5.91"));
        Assertions.assertThat(obj.getNetAmount()).isEqualTo(new BigDecimal("2.23"));
        // etc
        Assertions.assertThat(obj.getComments()).isEqualTo("comments | comments2 | comments2");
        //NOTE THAT
        Assertions.assertThat(tvo.getComments()).isEqualTo("comments | comments2 | comments2");
        // because
        Assertions.assertThat(obj).isEqualTo(tvo);




    }

    @Test
    public void compareTo() {

        // given
        Turnover t = new Turnover();
        final LocalDate date = new LocalDate(2020, 1, 1);
        TurnoverValueObject tvo1 = new TurnoverValueObject(t);
        tvo1.setDate(date);
        TurnoverValueObject tvo2 = new TurnoverValueObject(t);
        tvo2.setDate(date);

        // when, then
        Assertions.assertThat(tvo1.compareTo(tvo2)).isEqualTo(0);

        // when
        tvo1.setDate(date.plusDays(1));
        // then
        Assertions.assertThat(tvo1.compareTo(tvo2)).isEqualTo(1);

        // when
        tvo1.setDate(date.minusDays(1));
        // then
        Assertions.assertThat(tvo1.compareTo(tvo2)).isEqualTo(-1);

    }
}