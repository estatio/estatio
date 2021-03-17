package org.estatio.module.asset.app;

import java.math.BigInteger;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ViewModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.asset.app.CountData"
)
@ViewModel
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CountData {

    @MemberOrder(sequence = "1")
    private String propertyReference;

    @MemberOrder(sequence = "2")
    private LocalDate date;

    @MemberOrder(sequence = "3")
    private BigInteger pedestrialCount;

    @MemberOrder(sequence = "4")
    private BigInteger carCount;

}
