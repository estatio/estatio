package org.apache.isis.viewer.wicket.ui.components.collectioncontents.fullcalendar;

import java.io.Serializable;

import net.ftlines.wicket.fullcalendar.CalendarResponse;
import net.ftlines.wicket.fullcalendar.Config;
import net.ftlines.wicket.fullcalendar.FullCalendar;
import net.ftlines.wicket.fullcalendar.callback.ClickedEvent;
import net.ftlines.wicket.fullcalendar.callback.DroppedEvent;
import net.ftlines.wicket.fullcalendar.callback.ResizedEvent;
import net.ftlines.wicket.fullcalendar.callback.SelectedRange;
import net.ftlines.wicket.fullcalendar.callback.View;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.oid.RootOid;
import org.apache.isis.core.metamodel.adapter.oid.RootOidDefault;
import org.apache.isis.runtimes.dflt.runtime.system.context.IsisContext;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.pages.entity.EntityPage;

final class FullCalendarWithEventHandling extends FullCalendar {
    private final FeedbackPanel feedback;
    private static final long serialVersionUID = 1L;

    FullCalendarWithEventHandling(String id, Config config, FeedbackPanel feedback) {
        super(id, config);
        this.feedback = feedback;
    }

//    @Override
//    protected void onDateRangeSelected(SelectedRange range,
//            CalendarResponse response) {
//        info("Selected region: " + range.getStart() + " - "
//                + range.getEnd() + " / allDay: " + range.isAllDay());
//
//        response.getTarget().add(feedback);
//    }
//
//    @Override
//    protected boolean onEventDropped(DroppedEvent event,
//            CalendarResponse response) {
//        info("Event drop. eventId: " + event.getEvent().getId()
//                + " sourceId: " + event.getSource().getUuid()
//                + " dayDelta: " + event.getDaysDelta()
//                + " minuteDelta: " + event.getMinutesDelta()
//                + " allDay: " + event.isAllDay());
//        info("Original start time: " + event.getEvent().getStart()
//                + ", original end time: " + event.getEvent().getEnd());
//        info("New start time: " + event.getNewStartTime()
//                + ", new end time: " + event.getNewEndTime());
//
//        response.getTarget().add(feedback);
//        return false;
//    }
//
//    @Override
//    protected boolean onEventResized(ResizedEvent event,
//            CalendarResponse response) {
//        info("Event resized. eventId: " + event.getEvent().getId()
//                + " sourceId: " + event.getSource().getUuid()
//                + " dayDelta: " + event.getDaysDelta()
//                + " minuteDelta: " + event.getMinutesDelta());
//        response.getTarget().add(feedback);
//        return false;
//    }

    @Override
    protected void onEventClicked(ClickedEvent event,
            CalendarResponse response) {

        final AjaxRequestTarget target = response.getTarget();
//        response.refetchEvents();
//        info("Event clicked. eventId: " + event.getEvent().getId()
//                + ", sourceId: " + event.getSource().getUuid());
        //target.add(feedback);

        final String oidStr = (String) event.getEvent().getPayload();
        final RootOid oid = RootOidDefault.deString(oidStr, IsisContext.getOidMarshaller());
        final ObjectAdapter adapter = IsisContext.getPersistenceSession().getAdapterManager().adapterFor(oid);
        final PageParameters params = new EntityModel(adapter).asPageParameters();
        throw new RestartResponseException(EntityPage.class, params);
    }

//    @Override
//    protected void onViewDisplayed(View view, CalendarResponse response) {
//
//        info("View displayed. viewType: " + view.getType().name()
//                + ", start: " + view.getStart() + ", end: "
//                + view.getEnd());
//
//        response.getTarget().add(feedback);
//    }
}