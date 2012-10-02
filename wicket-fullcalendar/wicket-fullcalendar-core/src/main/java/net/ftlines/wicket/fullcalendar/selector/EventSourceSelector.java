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

package net.ftlines.wicket.fullcalendar.selector;

import net.ftlines.wicket.fullcalendar.FullCalendar;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebComponent;

public class EventSourceSelector extends WebComponent implements IHeaderContributor {

	private final FullCalendar calendar;

	public EventSourceSelector(String id, FullCalendar calendar) {
		super(id);
		this.calendar = calendar;
		setOutputMarkupId(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {

		response.render(OnLoadHeaderItem.forScript("$('#" + calendar.getMarkupId()
			+ "').fullCalendarExt('createEventSourceSelector', '" + getMarkupId() + "');"));
	}

}