package org.estatio.module.turnover.dom.entry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

@DomainService(nature = NatureOfService.DOMAIN)
public class TurnoverEntryService {

    public void produceEmptyTurnoversFor(final LocalDate date) {
        turnoverReportingConfigRepository.findAllActiveOnDate(date).forEach(
                c->c.produceEmptyTurnovers(date)
        );
    }

    public Turnover nextNewForReporter(final Person reporter, final Turnover current) {
        // first offer those of same property, type and date
        List<Turnover> samePropertyTypeAndDay = findNewByReporterPropertyTypeAndDate(reporter, current.getOccupancy().getUnit().getProperty(), current.getType(), current.getDate());
        if (!samePropertyTypeAndDay.isEmpty()) return samePropertyTypeAndDay.get(0);

        // first offer those of same type and date
        List<Turnover> sameTypeAndDay = sameTypeAndDayOtherProperty(reporter, current);
        if (!sameTypeAndDay.isEmpty()) return sameTypeAndDay.get(0);

        // else offer those of same type
        List<Turnover> sameType = sameType(reporter, current);
        if (!sameType.isEmpty()) return sameType.get(0);

        // else offer anything
        List<Turnover> anythingNew = anythingNew(reporter, clockService.now());

        return anythingNew.stream().findFirst().orElse(null);
    }

    public List<Turnover> allNewForReporter(final Person reporter) {
        return anythingNew(reporter, clockService.now());
    }

    private List<Turnover> anythingNew(final Person reporter, final LocalDate date) {
        List<Turnover> anythingNew = new ArrayList<>();
        propertiesForReporter(reporter).forEach(p->{
            turnoverReportingConfigRepository.findByPropertyActiveOnDate(p, date)
                    .stream()
                    .filter(cf->cf.effectiveReporter().equals(reporter))
                    .forEach(cf->{
                        anythingNew.addAll(turnoverRepository.findByConfigWithStatusNew(cf));
                    });
        });
        return anythingNew.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    private List<Turnover> sameType(final Person reporter, final Turnover current) {
        List<Turnover> sameType = new ArrayList<>();
        propertiesForReporter(reporter).forEach(
                p->{
                    turnoverReportingConfigRepository.findByPropertyAndTypeActiveOnDate(p, current.getType(), current.getDate())
                            .stream()
                            .filter(cf->cf.effectiveReporter().equals(reporter))
                            .forEach(cf->{
                                sameType.addAll(turnoverRepository.findByConfigAndTypeWithStatusNew(cf, current.getType()));
                            });
                }
        );
        return sameType.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    private List<Turnover> sameTypeAndDayOtherProperty(final Person reporter, final Turnover current) {
        List<Property> otherPropertiesForReporter = propertiesForReporter(reporter)
                .stream()
                .filter(p->p!=current.getOccupancy().getUnit().getProperty())
                .collect(Collectors.toList());

        List<Turnover> sameTypeAndDay = new ArrayList<>();
        otherPropertiesForReporter.forEach(
                p->{
                    turnoverReportingConfigRepository.findByPropertyAndTypeActiveOnDate(p, current.getType(), current.getDate())
                            .stream()
                            .filter(cf->cf.effectiveReporter().equals(reporter))
                            .forEach(cf->{
                                sameTypeAndDay.addAll(turnoverRepository.findByConfigAndTypeAndDateWithStatusNew(cf, current.getType(), current.getDate()));
                            });
                }
        );
        return sameTypeAndDay.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    public List<Turnover> findNewByReporterPropertyTypeAndDate(final Person reporter, final Property property, final Type type, final LocalDate date) {
        List<Turnover> result = new ArrayList<>();
        turnoverReportingConfigRepository.findByPropertyAndTypeActiveOnDate(property, type, date)
                .stream()
                .filter(cf->cf.effectiveReporter().equals(reporter))
                .forEach(cf->{
                    result.addAll(turnoverRepository.findByConfigAndTypeAndDateWithStatusNew(cf, type, date));
                });
        return result.stream().sorted(turnoverComparatorByOccupancyThenDateDesc()).collect(Collectors.toList());
    }

    public List<Property> propertiesForReporter(final Person reporter) {
        return fixedAssetRoleRepository.findByPartyAndType(reporter, FixedAssetRoleTypeEnum.TURNOVER_REPORTER).stream()
                .map(r->r.getAsset())
                .filter(a->a.getClass().isAssignableFrom(Property.class))
                .map(Property.class::cast)
                .collect(Collectors.toList());
    }

    public Turnover findTurnoverPreviousYear(final Turnover turnover) {
        return turnoverRepository.findUnique(turnover.getConfig(), turnover.getDate().minusYears(1), turnover.getType());
    }

    private static Comparator<Turnover> turnoverComparatorByOccupancyThenDateDesc() {
        return Comparator.comparing(Turnover::getOccupancy).thenComparing(Turnover::getDate, Comparator.reverseOrder());
    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject FixedAssetRoleRepository fixedAssetRoleRepository;

    @Inject ClockService clockService;
}
