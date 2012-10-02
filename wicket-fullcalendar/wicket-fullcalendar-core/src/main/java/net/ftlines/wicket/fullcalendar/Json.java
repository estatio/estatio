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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

class Json {
	private Json() {

	}

	private static class MyJsonFactory extends MappingJsonFactory {
		@Override
		public JsonGenerator createJsonGenerator(Writer out) throws IOException {
			return super.createJsonGenerator(out).useDefaultPrettyPrinter();
		}

		@Override
		public JsonGenerator createJsonGenerator(File f, JsonEncoding enc) throws IOException {
			return super.createJsonGenerator(f, enc).useDefaultPrettyPrinter();
		}

		@Override
		public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
			return super.createJsonGenerator(out, enc).useDefaultPrettyPrinter();
		}
	}

	public static String toJson(Object object) {
		ObjectMapper mapper = new ObjectMapper(new MyJsonFactory());
		SimpleModule module = new SimpleModule("fullcalendar", new Version(1, 0, 0, null));
		module.addSerializer(new DateTimeSerializer());
		module.addSerializer(new LocalTimeSerializer());
		mapper.registerModule(module);
		mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);

		String json = null;
		try {
			json = mapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException("Error encoding object: " + object + " into JSON string", e);
		}
		return json;
	}

	public static class DateTimeSerializer extends JsonSerializer<DateTime> {
		@Override
		public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
			jgen.writeString(ISODateTimeFormat.dateTime().print(value));
		}

		@Override
		public Class<DateTime> handledType() {
			return DateTime.class;
		}

	}

	public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {
		@Override
		public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
			jgen.writeString(value.toString("h:mmaa"));
		}

		@Override
		public Class<LocalTime> handledType() {
			return LocalTime.class;
		}

	}

	public static class Script implements Serializable {
		private String code;

		public Script(String value) {
			this.code = value;
		}

		public String getDeclaration() {
			return code;
		}

	}

}
