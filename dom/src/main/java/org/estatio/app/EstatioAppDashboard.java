/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.app;

import java.util.List;

import org.apache.isis.applib.annotation.Dashboard;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dom.EstatioService;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;

@Dashboard
@Named("Dashboard")
public class EstatioAppDashboard extends EstatioService<EstatioAppDashboard> {
    
    public EstatioAppDashboard() {
        super(EstatioAppDashboard.class);
    }

    @Override
    public String getId() {
        return "dashboard";
    }

    @Override
    public String iconName() {
        return "Dashboard";
    }

    // //////////////////////////////////////

    @Named("Leases about to expire")
    @Render(Type.EAGERLY)
    public List<Lease> getLeasesAboutToExpire() {
        return leases.findAboutToExpireOnDate(getClockService().now());
    }

    // //////////////////////////////////////

    private Leases leases;

    public void injectLeases(final Leases leases) {
        this.leases = leases;
    }
}
