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
package org.estatio.dom.apptenancy;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.estatio.dom.EstatioDomainObject;

public class ApplicationTenancyEventChanged extends ActionDomainEvent<EstatioDomainObject> {
    public ApplicationTenancyEventChanged(final EstatioDomainObject source, final Identifier identifier, final Object... arguments) {
        super(source, identifier, arguments);
    }

    /**
     * The proposed new {@link org.isisaddons.module.security.dom.tenancy.ApplicationTenancy} for the object.
     * @return
     */
    public ApplicationTenancy getNewApplicationTenancy() {
        switch (getEventPhase()) {
            case HIDE:
            case DISABLE:
                throw new IllegalStateException("phase is " + getEventPhase());
        }
        return (ApplicationTenancy) getArguments().get(0);
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void disable(final String reason) {
        super.disable(reason);
    }

    @Override
    public void invalidate(final String reason) {
        super.invalidate(reason);
    }
}
