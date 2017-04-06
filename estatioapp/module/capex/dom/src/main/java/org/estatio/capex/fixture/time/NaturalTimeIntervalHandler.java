package org.estatio.capex.fixture.time;

import java.util.regex.Pattern;

import javax.inject.Inject;

import org.estatio.capex.dom.time.CalendarType;
import org.estatio.capex.dom.time.TimeInterval;
import org.estatio.capex.dom.time.TimeIntervalRepository;

public class NaturalTimeIntervalHandler extends TimeIntervalHandler {

    public NaturalTimeIntervalHandler() {
        super(CalendarType.NATURAL);
    }

    final static Pattern quarterPattern = Pattern.compile("^\\d{4}Q\\d$");

    public TimeInterval getParent() {
        final String parentName = getParentName();
        return timeIntervalRepository.findByName(parentName);
    }

    String getParentName() {
        if(quarterPattern.matcher(getName()).matches()) {
            return getName().substring(0, 4);
        } else {
            return null;
        }
    }

    @Inject
    TimeIntervalRepository timeIntervalRepository;

}

