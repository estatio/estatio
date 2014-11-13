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
package org.estatio.webapp.services.other;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.EstatioImmutableObject;
import org.estatio.dom.EstatioService;

/**
 * This is a dummy service that is, nevertheless, registered, in order that
 * miscellaneous domain services, typically for {@link EstatioImmutableObject
 * reference data} entities, can associate their various actions together.
 */
@DomainService(menuOrder = "91")
@Named("Other")
public class EstatioOtherServices extends EstatioService<EstatioOtherServices> {

    public EstatioOtherServices() {
        super(EstatioOtherServices.class);
    }

    private Map<String, String> properties;

    @Programmatic
    @PostConstruct
    public void init(final Map<String, String> properties) {
        super.init(properties);
        this.properties = properties;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Other", sequence = "999")
    public String execute() {
        String command = properties.get("executeCommand");
        if (command == null) {
            return "Command not configured in isis.properties";
        }
        return execute(command);
    }

    @Programmatic
    String execute(String command) {

        int exitValue = 1;
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        ExecuteStreamHandler handler = executor.getStreamHandler();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        executor.setStreamHandler(psh);

        try {
            handler.setProcessOutputStream(System.in);
        } catch (IOException e) {

        }

        try {
            exitValue = executor.execute(commandLine);
        } catch (ExecuteException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
        return stdout.toString();
    }

}
