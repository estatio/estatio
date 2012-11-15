package org.apache.isis.viewer.wicket.ui.components.collectioncontents.fullcalendar;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import net.ftlines.wicket.fullcalendar.Event;
import net.ftlines.wicket.fullcalendar.EventNotFoundException;
import net.ftlines.wicket.fullcalendar.EventProvider;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.progmodel.facets.value.date.DateValueFacet;
import org.apache.isis.runtimes.dflt.runtime.system.context.IsisContext;
import org.apache.isis.viewer.wicket.model.models.EntityCollectionModel;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

public class DateAssociationEventsProvider implements EventProvider {

    private static final long serialVersionUID = 1L;

    private final Map<String, Event> eventById = Maps.newLinkedHashMap();

    private final static Predicate<Event> NOT_NULL = new Predicate<Event>() {
        @Override
        public boolean apply(Event input) {
            return input != null;
        }
    };

    public DateAssociationEventsProvider(final EntityCollectionModel model, final ObjectAssociation dateAssociation) {
        
        final Collection<ObjectAdapter> entityList = model.getObject();
        
        final Function<ObjectAdapter, Event> function = new Function<ObjectAdapter, Event>() {

            @Override
            public Event apply(ObjectAdapter input) {
                Event event = new Event();

                final String associationId = dateAssociation.getId();
                final String associationName = dateAssociation.getName();
                final DateValueFacet facet = dateAssociation.getSpecification().getFacet(DateValueFacet.class);
                final ObjectAdapter dateAdapter = dateAssociation.get(input);
                final Date dateValue = facet.dateValue(dateAdapter);
                if(dateValue == null) {
                    return null;
                }
                final DateTime start = new DateTime(dateValue.getTime());
                final DateTime end = start;

                event.setStart(start);
                event.setEnd(end);

                final String oidStr = input.getOid().enString(IsisContext.getOidMarshaller());
                event.setId(oidStr + "-" + associationId);

                event.setAllDay(true);
                //event.setBackgroundColor(backgroundColor)
                //event.setBorderColor(borderColor)
                event.setClassName("fullCalendar-event-" + associationId);
                //event.setColor(color)
                event.setEditable(false);
                event.setPayload(oidStr);
                //event.setTextColor(textColor)
                event.setTitle(input.titleString() + "\n" + associationName);
                //event.setUrl(url)
                
                return event;
            }
        };
        
        final Collection<Event> events = Collections2.filter(Collections2.transform(entityList, function), NOT_NULL);
        for (Event event : events) {
            eventById.put(event.getId(), event);
        }
    }

    @Override
    public Collection<Event> getEvents(final DateTime start, final DateTime end) {
        final Interval interval = new Interval(start, end);
        final Predicate<Event> withinInterval = new Predicate<Event>() {
            @Override
            public boolean apply(Event input) {
                return interval.contains(input.getStart());
            }
        };
        final Collection<Event> values = eventById.values();
        return Collections2.filter(values, withinInterval);
    }

    @Override
    public Event getEventForId(String id) throws EventNotFoundException {
        return eventById.get(id);
    }

}
