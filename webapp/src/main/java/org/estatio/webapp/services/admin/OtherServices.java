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
package org.estatio.webapp.services.admin;

import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.EstatioImmutableObject;

/**
 * This is a dummy service that is, nevertheless, registered, in order that 
 * miscellaneous domain services, typically for {@link EstatioImmutableObject reference data} entities,
 * can associate their various actions together.
 */
@Named("Other")
public class OtherServices {

}
