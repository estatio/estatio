package org.estatio.module.turnover.dom.entry;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.party.dom.Person;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class TurnoverEntryService {

    public void produceEmptyTurnoversFor(final LocalDate date) {
        turnoverReportingConfigRepository.findAllActiveOnDate(date).forEach(
                c->c.produceEmptyTurnovers(date)
        );
    }

    public List<TurnoverEntryRequest> getTurnoverEntryRequestsForPerson(final Person person){
        turnoverReportingConfigRepository.findByReporter(person);
        return null;
    }

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

}
