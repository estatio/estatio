/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package net.ftlines.wicket.fullcalendar;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ftlines.wicket.fullcalendar.callback.ClickedEvent;
import net.ftlines.wicket.fullcalendar.callback.DroppedEvent;
import net.ftlines.wicket.fullcalendar.callback.ResizedEvent;
import net.ftlines.wicket.fullcalendar.callback.SelectedRange;
import net.ftlines.wicket.fullcalendar.callback.View;
import net.ftlines.wicket.fullcalendar.selector.EventSourceSelector;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.time.Duration;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class HomePage extends WebPage {

	public HomePage() {

		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);

		Config config = new Config();
		config.setSelectable(true);
		config.setSelectHelper(false);

		EventSource reservations = new EventSource();
		reservations.setTitle("Reservations");
		reservations
				.setEventsProvider(new RandomEventsProvider("Reservation "));
		reservations.setEditable(true);
		reservations.setBackgroundColor("#63BA68");
		reservations.setBorderColor("#63BA68");
		config.add(reservations);

		EventSource downtimes = new EventSource();
		downtimes.setTitle("Maintenance");
		downtimes.setBackgroundColor("#B1ADAC");
		downtimes.setBorderColor("#B1ADAC");
		downtimes.setEventsProvider(new RandomEventsProvider("Maintenance "));
		config.add(downtimes);

		EventSource other = new EventSource();
		other.setTitle("Other Reservations");
		other.setBackgroundColor("#E6CC7F");
		other.setBorderColor("#E6CC7F");
		other.setEventsProvider(new RandomEventsProvider("Other Reservations "));
		config.add(other);

		config.getHeader().setLeft("prev,next today");
		config.getHeader().setCenter("title");
		config.getHeader().setRight("month,agendaWeek,agendaDay");

		config.getButtonText().setToday("Week");

		config.setLoading("function(bool) { if (bool) $(\"#loading\").show(); else $(\"#loading\").hide(); }");

		config.setMinTime(new LocalTime(6, 30));
		config.setMaxTime(new LocalTime(17, 30));
		config.setAllDaySlot(false);
		FullCalendar calendar = new FullCalendar("cal", config) {
			@Override

			protected void onDateRangeSelected(SelectedRange range,
					CalendarResponse response) {
				info("Selected region: " + range.getStart() + " - "
						+ range.getEnd() + " / allDay: " + range.isAllDay());

				response.getTarget().add(feedback);
			}

			@Override

			protected boolean onEventDropped(DroppedEvent event,
					CalendarResponse response) {
				info("Event drop. eventId: " + event.getEvent().getId()
						+ " sourceId: " + event.getSource().getUuid()
						+ " dayDelta: " + event.getDaysDelta()
						+ " minuteDelta: " + event.getMinutesDelta()
						+ " allDay: " + event.isAllDay());
				info("Original start time: " + event.getEvent().getStart()
						+ ", original end time: " + event.getEvent().getEnd());
				info("New start time: " + event.getNewStartTime()
						+ ", new end time: " + event.getNewEndTime());

				response.getTarget().add(feedback);
				return false;
			}

			@Override

			protected boolean onEventResized(ResizedEvent event,
					CalendarResponse response) {
				info("Event resized. eventId: " + event.getEvent().getId()
						+ " sourceId: " + event.getSource().getUuid()
						+ " dayDelta: " + event.getDaysDelta()
						+ " minuteDelta: " + event.getMinutesDelta());
				response.getTarget().add(feedback);
				return false;
			}

			@Override

			protected void onEventClicked(ClickedEvent event,
					CalendarResponse response) {
				info("Event clicked. eventId: " + event.getEvent().getId()
						+ ", sourceId: " + event.getSource().getUuid());
				response.refetchEvents();
				response.getTarget().add(feedback);
			}

			@Override
			protected void onViewDisplayed(View view, CalendarResponse response) {

				info("View displayed. viewType: " + view.getType().name()
						+ ", start: " + view.getStart() + ", end: "
						+ view.getEnd());
				response.getTarget().add(feedback);
			}
		};
		calendar.setMarkupId("calendar");
		add(calendar);
		add(new EventSourceSelector("selector", calendar));
	}

	private static class RandomEventsProvider implements EventProvider {
		Map<Integer, Event> events = new HashMap<Integer, Event>();

		private final String title;

		public RandomEventsProvider(String title) {
			this.title = title;
		}

		@Override
		public Collection<Event> getEvents(DateTime start, DateTime end) {
			events.clear();
			SecureRandom random = new SecureRandom();

			Duration duration = Duration.valueOf(end.getMillis()
					- start.getMillis());

			for (int j = 0; j < 1; j++) {
				for (int i = 0; i < duration.days() + 1; i++) {
					DateTime calendar = start;
					calendar = calendar.plusDays(i).withHourOfDay(
							6 + random.nextInt(10));

					Event event = new Event();
					int id = (int) (j * duration.days() + i);
					event.setId("" + id);
					event.setTitle(title + (1 + i));
					event.setStart(calendar);
					calendar = calendar.plusHours(random.nextInt(8));
					event.setEnd(calendar);

					events.put(id, event);
				}
			}
			return events.values();
		}

		@Override
		public Event getEventForId(String id) throws EventNotFoundException {
			Integer idd = Integer.valueOf(id);
			Event event = events.get(idd);
			if (event != null) {
				return event;
			}
			throw new EventNotFoundException("Event with id: " + id
					+ " not found");
		}

	}

}
