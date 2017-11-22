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
package org.estatio.module.base.platform.integtestsupport;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.core.runtime.authentication.standard.SimpleSession;

import org.isisaddons.module.command.dom.BackgroundCommandExecutionFromBackgroundCommandServiceJdo;
import org.isisaddons.module.command.dom.BackgroundCommandServiceJdoRepository;
import org.isisaddons.module.command.dom.CommandJdo;

import static org.assertj.core.api.Assertions.assertThat;

@DomainService(nature = NatureOfService.DOMAIN)
public class RunBackgroundCommandsService {

    private static final Logger LOG = LoggerFactory.getLogger(RunBackgroundCommandsService.class);

    @Programmatic
    public void runBackgroundCommands() throws InterruptedException {

        List<CommandJdo> commands = backgroundCommandRepository.findBackgroundCommandsNotYetStarted();
        assertThat(commands).hasSize(1);

        transactionService.nextTransaction();

        BackgroundCommandExecutionFromBackgroundCommandServiceJdo backgroundExec =
                new BackgroundCommandExecutionFromBackgroundCommandServiceJdo();
        final SimpleSession session = new SimpleSession("scheduler_user", new String[] { "admin_role" });

        final Thread thread = new Thread(() -> backgroundExec.execute(session, null));
        thread.start();

        thread.join(5000L);

        commands = backgroundCommandRepository.findBackgroundCommandsNotYetStarted();
        assertThat(commands).isEmpty();
    }

    @Inject
    protected BackgroundCommandServiceJdoRepository backgroundCommandRepository;

    @Inject
    protected TransactionService transactionService;


}

