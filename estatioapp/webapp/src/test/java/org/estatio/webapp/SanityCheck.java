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
package org.estatio.webapp;

import javax.jdo.spi.PersistenceCapable;

import org.estatio.module.lease.dom.Lease;

public class SanityCheck {

    public static void main(String[] args) {
        check(Lease.class);
    }

    private static void check(Class<?> cls) {
        if(PersistenceCapable.class.isAssignableFrom(cls)) {
            System.out.println(cls.getName() + " class enhanced OK");
            System.out.flush();
        } else {
            System.err.println(cls.getName() + " class NOT enhanced");
            System.err.flush();
        }
    }

}
