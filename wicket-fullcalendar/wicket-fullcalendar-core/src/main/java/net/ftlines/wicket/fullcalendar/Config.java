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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonRawValue;
import org.joda.time.LocalTime;

public class Config implements Serializable {
	/** Use these to specify calendar column formats */
	public static enum ColumnFormat {
		day, week, month;
	}

	private List<EventSource> eventSources = new ArrayList<EventSource>();
	private Header header = new Header();
	private ButtonText buttonText = new ButtonText();
	private String loading;
	private Boolean editable;
	private String eventDrop;
	private String eventResize;
	private String eventClick;

	private String viewDisplay;
	private Boolean selectable;
	private Boolean selectHelper;
	/** A callback that will fire after a selection is made */
	private String select;
	private String defaultView;
	@JsonProperty
	private Map<ColumnFormat, String> columnFormat = new HashMap<Config.ColumnFormat, String>();

	private LocalTime minTime;
	private LocalTime maxTime;
	private Integer firstHour;
	private Boolean allDaySlot;

	private String timeFormat;

	private String eventRender;

	private Boolean disableDragging;
	private Boolean disableResizing;
	private Integer slotMinutes;
	private Float aspectRatio;
	private boolean ignoreTimezone = false;

	public Config add(EventSource eventSource) {
		eventSources.add(eventSource);
		return this;
	}

	public Collection<EventSource> getEventSources() {
		return Collections.unmodifiableList(eventSources);
	}

	public Header getHeader() {
		return header;
	}

	@JsonRawValue
	public String getEventResize() {
		return eventResize;
	}

	public void setEventResize(String eventResize) {
		this.eventResize = eventResize;
	}

	@JsonRawValue
	public String getLoading() {
		return loading;
	}

	public void setLoading(String loading) {
		this.loading = loading;
	}

	public Boolean isEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	@JsonRawValue
	public String getEventDrop() {
		return eventDrop;
	}

	public void setEventDrop(String eventDrop) {
		this.eventDrop = eventDrop;
	}

	public Boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(Boolean selectable) {
		this.selectable = selectable;
	}

	public Boolean isSelectHelper() {
		return selectHelper;
	}

	public void setSelectHelper(Boolean selectHelper) {
		this.selectHelper = selectHelper;
	}

	@JsonRawValue
	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	@JsonRawValue
	public String getEventClick() {
		return eventClick;
	}

	public void setEventClick(String eventClick) {
		this.eventClick = eventClick;
	}

	/**
	 * @return the defaultView
	 */
	public String getDefaultView() {
		return defaultView;
	}

	/**
	 * See <a href="http://arshaw.com/fullcalendar/docs/views/Available_Views/">http ://arshaw.com/
	 * fullcalendar/docs/views/Available_Views/</a> for the list of possible values.
	 * 
	 * @param defaultView
	 *            the defaultView to set
	 */
	public void setDefaultView(String defaultView) {
		this.defaultView = defaultView;
	}

	@JsonIgnore
	public String getColumnFormatDay() {
		return columnFormat.get(ColumnFormat.day);
	}

	public void setColumnFormatDay(String format) {
		columnFormat.put(ColumnFormat.day, format);
	}

	@JsonIgnore
	public String getColumnFormatWeek() {
		return columnFormat.get(ColumnFormat.week);
	}

	public void setColumnFormatWeek(String format) {
		columnFormat.put(ColumnFormat.week, format);
	}

	@JsonIgnore
	public String getColumnFormatMonth() {
		return columnFormat.get(ColumnFormat.month);
	}

	public void setColumnFormatMonth(String format) {
		columnFormat.put(ColumnFormat.month, format);
	}

	public ButtonText getButtonText() {
		return buttonText;
	}

	public LocalTime getMinTime() {
		return minTime;
	}

	public void setMinTime(LocalTime minTime) {
		this.minTime = minTime;
	}

	public LocalTime getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(LocalTime maxTime) {
		this.maxTime = maxTime;
	}

	public Integer getFirstHour() {
		return firstHour;
	}

	public void setFirstHour(Integer firstHour) {
		this.firstHour = firstHour;
	}

	public Boolean getAllDaySlot() {
		return allDaySlot;
	}

	public void setAllDaySlot(Boolean allDaySlot) {
		this.allDaySlot = allDaySlot;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	@JsonRawValue
	public String getEventRender() {
		return eventRender;
	}

	public void setEventRender(String eventRenderer) {
		this.eventRender = eventRenderer;
	}

	public Boolean getDisableDragging() {
		return disableDragging;
	}

	public void setDisableDragging(Boolean disableDragging) {
		this.disableDragging = disableDragging;
	}

	public Boolean getDisableResizing() {
		return disableResizing;
	}

	public void setDisableResizing(Boolean disableResizing) {
		this.disableResizing = disableResizing;
	}

	@JsonRawValue
	public String getViewDisplay() {
		return viewDisplay;
	}

	public void setViewDisplay(String viewDisplay) {
		this.viewDisplay = viewDisplay;
	}

	public void setSlotMinutes(Integer slotMinutes) {
		this.slotMinutes = slotMinutes;
	}

	public Integer getSlotMinutes() {
		return slotMinutes;
	}

	/**
	 * See <a href="http://arshaw.com/fullcalendar/docs/display/aspectRatio/">http ://arshaw.com/
	 * fullcalendar/docs/display/aspectRatio/</a>
	 * 
	 * @param aspectRatio
	 *            the aspectRatio to set
	 */
	public void setAspectRatio(Float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	/**
	 * See <a href="http://arshaw.com/fullcalendar/docs/display/aspectRatio/">http ://arshaw.com/
	 * fullcalendar/docs/display/aspectRatio/</a>
	 * 
	 * @return the aspectRatio
	 */
	public Float getAspectRatio() {
		return aspectRatio;
	}

	/**
	 * If <var>ignoreTimezone</var> is {@code true}, then the remote client's time zone will be ignored when determining
	 * selected date ranges, resulting in ranges with the selected start and end values, but in the server's time zone.
	 * The default value is {@code false}.
	 * <p>
	 * Not currently used on the client side.
	 * 
	 * @param ignoreTimezone
	 *            whether or not to ignore the remote client's time zone when determining selected date ranges
	 */
	public void setIgnoreTimezone(final boolean ignoreTimezone) {
		this.ignoreTimezone = ignoreTimezone;
	}

	/**
	 * If <var>ignoreTimezone</var> is {@code true}, then the remote client's time zone will be ignored when determining
	 * selected date ranges, resulting in ranges with the selected start and end values, but in the server's time zone.
	 * The default value is {@code false}.
	 * <p>
	 * Not currently used on the client side.
	 * 
	 * @return whether or not to ignore the remote client's time zone when determining selected date ranges
	 */
	@JsonIgnore
	public boolean isIgnoreTimezone() {
		return ignoreTimezone;
	}

}
