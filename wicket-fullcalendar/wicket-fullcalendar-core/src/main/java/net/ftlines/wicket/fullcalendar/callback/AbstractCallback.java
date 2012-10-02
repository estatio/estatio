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

package net.ftlines.wicket.fullcalendar.callback;

import java.util.Map;

import net.ftlines.wicket.fullcalendar.FullCalendar;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.behavior.IBehaviorListener;
import org.apache.wicket.request.mapper.parameter.PageParameters;

abstract class AbstractCallback extends Behavior implements IBehaviorListener {
	private FullCalendar calendar;

	@Override
	public void bind(Component component) {
		super.bind(component);
		this.calendar = (FullCalendar) component;
	}

	protected final String getUrl(Map<String, Object> parameters) {
		PageParameters params = new PageParameters();
		String url = calendar.urlFor(IBehaviorListener.INTERFACE, params).toString();

		if (parameters != null) {
			for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
				url += "&" + parameter.getKey() + "=" + parameter.getValue();
			}
		}
		return url;
	}

	@Override
	public final void onRequest() {
		respond();
	}

	protected abstract void respond();

	protected final FullCalendar getCalendar() {
		return calendar;
	}

	@Override
	public boolean getStatelessHint(Component component) {
		return false;
	}
}
