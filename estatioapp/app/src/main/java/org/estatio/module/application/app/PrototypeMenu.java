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
package org.estatio.module.application.app;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;

import org.estatio.module.base.dom.UdoDomainService;


@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.prototype.PrototypeMenu"
)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "900")
public class PrototypeMenu extends UdoDomainService<PrototypeMenu> {

    public PrototypeMenu() {
        super(PrototypeMenu.class);
    }


    // add any prototyping actions here...

    /**
     * As a simple convenience...
     *
     * @return
     * @throws MalformedURLException
     */
    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public URL openSwagger() throws MalformedURLException {
        return new java.net.URL("http://localhost:8080/swagger-ui/index.html");
    }

}
