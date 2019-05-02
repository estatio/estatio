package org.estatio.module.turnover.dom.entry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class TurnoverEntryService {

    public void produceEmptyTurnoversFor(final LocalDate date) {
        turnoverReportingConfigRepository.findAllActiveOnDate(date).forEach(
                c->c.produceEmptyTurnovers(date)
        );
    }

    public Turnover nextNewForReporter(final Person reporter, final Turnover current) {
        // first offer those of same property, type and date
        List<Turnover> samePropertyTypeAndDay = samePropertyTypeAndDay(reporter, current);
        if (!samePropertyTypeAndDay.isEmpty()) return samePropertyTypeAndDay.get(0);

        // first offer those of same type and date
        List<Turnover> sameTypeAndDay = sameTypeAndDay(reporter, current);
        if (!sameTypeAndDay.isEmpty()) return sameTypeAndDay.get(0);

        // else offer those of same type
        List<Turnover> sameType = sameType(reporter, current);
        if (!sameType.isEmpty()) return sameType.get(0);

        // else offer anything
        List<Turnover> anythingNew = anythingNew(reporter);

        return anythingNew.stream().findFirst().orElse(null);
    }

    public List<Turnover> allNewForReporter(final Person reporter) {
        return anythingNew(reporter);
    }

    private List<Turnover> anythingNew(final Person reporter) {
        List<Turnover> anythingNew = new ArrayList<>();
        turnoverReportingConfigRepository.findByReporter(reporter).stream().forEach(cf->{
            anythingNew.addAll(turnoverRepository.findByConfigWithStatusNew(cf));
        });
        return anythingNew.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    private List<Turnover> sameType(final Person reporter, final Turnover current) {
        List<Turnover> sameType = new ArrayList<>();
        turnoverReportingConfigRepository.findByReporter(reporter).stream().forEach(cf->{
            sameType.addAll(turnoverRepository.findByConfigAndTypeWithStatusNew(cf, current.getType()));
        });
        return sameType.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    private List<Turnover> sameTypeAndDay(final Person reporter, final Turnover current) {
        List<Turnover> sameTypeAndDay = new ArrayList<>();
        turnoverReportingConfigRepository.findByReporter(reporter).stream().forEach(cf->{
            sameTypeAndDay.addAll(turnoverRepository.findByConfigAndTypeAndDateWithStatusNew(cf, current.getType(), current.getDate()));
        });
        return sameTypeAndDay.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    private List<Turnover> samePropertyTypeAndDay(final Person reporter, final Turnover current) {
        List<Turnover> samePropertyTypeAndDay = new ArrayList<>();
        turnoverReportingConfigRepository.findByReporter(reporter).stream()
                .filter(cf->cf.getOccupancy().getUnit().getProperty().equals(current.getConfig().getOccupancy().getUnit().getProperty()))
                .forEach(cf->{
            samePropertyTypeAndDay.addAll(turnoverRepository.findByConfigAndTypeAndDateWithStatusNew(cf, current.getType(), current.getDate()));
        });
        return samePropertyTypeAndDay.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    private static Comparator<Turnover> turnoverComparatorByOccupancyThenDateDesc() {
        return Comparator.comparing(Turnover::getOccupancy).thenComparing(Turnover::getDate, Comparator.reverseOrder());
    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;
}
