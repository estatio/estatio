/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom;

import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.applib.value.Date;

import org.estatio.dom.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForApplib {

	public static FixtureDatumFactory<Date> dates() {
		return new FixtureDatumFactory<>(Date.class, new Date(2012, 7, 19), new Date(2012, 7, 20), new Date(2012, 8, 19), new Date(2013, 7, 19));
	}

	public static FixtureDatumFactory<Blob> blobs() {
		return new FixtureDatumFactory<>(Blob.class,
				new Blob("foo", "application/pdf", new byte[]{1,2,3}),
				new Blob("bar", "application/docx", new byte[]{4,5}),
				new Blob("baz", "application/xlsx", new byte[]{7,8,9,0})
				);
	}

	public static FixtureDatumFactory<Clob> clobs() {
		return new FixtureDatumFactory<>(Clob.class,
				new Clob("foo", "text/html", "<html/>".toCharArray()),
				new Clob("bar", "text/plain", "hello world".toCharArray()),
				new Clob("baz", "text/ini", "foo=bar".toCharArray())
				);
	}


}
