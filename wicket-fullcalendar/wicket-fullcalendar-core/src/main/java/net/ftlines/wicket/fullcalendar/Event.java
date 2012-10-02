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

import org.joda.time.DateTime;

public class Event implements Serializable {

	private String id;

	private String title;

	private boolean allDay = false;

	private DateTime start;

	private DateTime end;

	private String url;

	private String className;

	private Boolean editable;

	private String color;

	private String backgroundColor;

	private String borderColor;

	private String textColor;
	private Serializable payload;

	public String getId() {
		return id;
	}

	public Event setId(String id) {
		this.id = id;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Event setTitle(String title) {
		this.title = title;
		return this;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public Event setAllDay(boolean allDay) {
		this.allDay = allDay;
		return this;
	}

	public DateTime getStart() {
		return start;
	}

	public Event setStart(DateTime start) {
		this.start = start;
		return this;
	}

	public DateTime getEnd() {
		return end;
	}

	public Event setEnd(DateTime end) {
		this.end = end;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public Event setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public Event setClassName(String className) {
		this.className = className;
		return this;
	}

	public Boolean isEditable() {
		return editable;
	}

	public Event setEditable(Boolean editable) {
		this.editable = editable;
		return this;
	}

	public String getColor() {
		return color;
	}

	public Event setColor(String color) {
		this.color = color;
		return this;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public Event setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public Event setBorderColor(String borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public String getTextColor() {
		return textColor;
	}

	public Event setTextColor(String textColor) {
		this.textColor = textColor;
		return this;
	}

	public Serializable getPayload() {
		return payload;
	}

	public void setPayload(Serializable payload) {

		this.payload = payload;
	}

}
