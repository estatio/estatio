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

import org.joda.time.LocalDate;

import org.estatio.dom.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForJoda {

	public static FixtureDatumFactory<LocalDate> dates() {
		return new FixtureDatumFactory<>(LocalDate.class, new LocalDate(2012, 7, 19), new LocalDate(2012, 7, 20), new LocalDate(2012, 8, 19), new LocalDate(2013, 7, 19));
	}


}
