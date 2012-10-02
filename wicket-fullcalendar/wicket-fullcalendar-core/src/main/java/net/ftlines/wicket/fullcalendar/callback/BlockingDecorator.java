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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.util.collections.MicroMap;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;

/**
 * Prevents multiple clicks while ajax request is executing
 * 
 * @author igor
 */
public class BlockingDecorator implements IAjaxCallListener {

	// @formatter:off

	private static final String template = "if (typeof(${var})=='undefined'){${var}=true;}"
			+ "if(${var}==false){return false;}" + "${var}=false;";

	// @formatter:on

	private final AbstractAjaxCallback callback;

	private static String clean(String str) {
		return str != null ? str.replaceAll("[^0-9a-zA-Z]", "") : null;
	}

	public BlockingDecorator(AbstractAjaxCallback callback) {
		this.callback = callback;
	}

	private String var() {
		String var = null;
		switch (callback.getCalendar().getAjaxConcurrency()) {
		case DROP:
			var = callback.getCalendar().getMarkupId();
			break;
		case DROP_PER_CALLBACK:
			var = callback.getClass().getName();
		}
		var = "window.block" + clean(var);
		return var;
	}

	public CharSequence decorateScript(Component component, CharSequence script) {
		switch (callback.getCalendar().getAjaxConcurrency()) {
		case QUEUE:
			return script;
		case DROP_PER_CALLBACK:
		case DROP:
			return new MapVariableInterpolator(template, new MicroMap<String, String>("var", var())).toString()
				+ script;

		default:
			throw new IllegalStateException();
		}
	}

	public CharSequence decorateOnSuccessScript(Component component, CharSequence script) {
		switch (callback.getCalendar().getAjaxConcurrency()) {
		case QUEUE:
			return script;
		case DROP_PER_CALLBACK:
		case DROP:
			return var() + "=true;";
		default:
			throw new IllegalStateException();
		}
	}

	public CharSequence decorateOnFailureScript(Component component, CharSequence script) {
		return decorateOnSuccessScript(component, script);
	}

	@Override
	public CharSequence getSuccessHandler(Component component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getFailureHandler(Component component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getBeforeHandler(Component component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getAfterHandler(Component component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getCompleteHandler(Component component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getPrecondition(Component component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getBeforeSendHandler(Component component) {
		// TODO Auto-generated method stub
		return null;
	}
}
