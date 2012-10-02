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

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.WicketAjaxJQueryResourceReference;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

abstract class AbstractFullCalendar extends MarkupContainer implements IHeaderContributor {
	public AbstractFullCalendar(String id) {
		super(id);
	}

	// TODO see if it makes sense to switch these to
	// Css/JavaScriptResourceReference
	private static final ResourceReference CSS = new PackageResourceReference(AbstractFullCalendar.class,
		"res/fullcalendar.css");
	private static final ResourceReference JS = new PackageResourceReference(AbstractFullCalendar.class,
		"res/fullcalendar.js");
	private static final ResourceReference JS_EXT = new PackageResourceReference(AbstractFullCalendar.class,
		"res/fullcalendar.ext.js");
	private static final ResourceReference JS_MIN = new PackageResourceReference(AbstractFullCalendar.class,
		"res/fullcalendar.min.js");

	@Override
	public void renderHead(IHeaderResponse response) {

		response.render(JavaScriptHeaderItem.forReference(WicketAjaxJQueryResourceReference.get()));

		response.render(CssReferenceHeaderItem.forReference(CSS));

		if (getApplication().usesDeploymentConfig()) {
			response.render(JavaScriptReferenceHeaderItem.forReference(JS_MIN));
		} else {
			response.render(JavaScriptReferenceHeaderItem.forReference(JS));
		}
		response.render(JavaScriptReferenceHeaderItem.forReference(JS_EXT));

	}

	public final String toJson(Object value) {
		return Json.toJson(value);
	}
}
