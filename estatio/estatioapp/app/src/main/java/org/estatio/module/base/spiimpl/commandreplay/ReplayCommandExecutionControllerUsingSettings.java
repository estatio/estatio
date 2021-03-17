package org.estatio.module.base.spiimpl.commandreplay;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CommandPersistence;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;

import org.isisaddons.module.command.replay.impl.SlaveConfiguration;
import org.isisaddons.module.command.replay.spi.ReplayCommandExecutionController;

import org.estatio.module.settings.dom.ApplicationSetting;
import org.estatio.module.settings.dom.ApplicationSettingForEstatio;
import org.estatio.module.settings.dom.ApplicationSettingsServiceForEstatio;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        menuOrder = "1",
        objectType = "commandreplay.ReplayCommandExecutionControllerUsingSettings"
)
public class ReplayCommandExecutionControllerUsingSettings
        implements ReplayCommandExecutionController {

    public static final String KEY = ReplayCommandExecutionControllerUsingSettings.class.getName() + ".state";
    SlaveConfiguration slaveConfig;

    @PostConstruct
    public void init(Map<String,String> properties) {
        slaveConfig = new SlaveConfiguration(properties);
    }

    @Programmatic
    public boolean isInitialized() {
        return slaveConfig != null;
    }

    @Override
    public State getState() {
        if(!isInitialized()) {
            return null;
        }
        ApplicationSetting setting = getApplicationSetting();
        return State.valueOf(setting.valueAsString());
    }

    @Action(
            commandPersistence = CommandPersistence.NOT_PERSISTED
    )
    @ActionLayout(
            cssClassFa = "wrench"
    )
    public void replayControl(State state) {
        getApplicationSetting().setValueRaw(state.name());
        messageService.informUser("Replay of commands: " + state);
    }
    public State default0ReplayControl() {
        return getState();
    }

    public boolean hideReplayControl() {
        return !slaveConfig.isConfigured();
    }

    private ApplicationSettingForEstatio getApplicationSetting() {
        ApplicationSetting setting = applicationSettingsService.find(KEY);
        if(setting == null) {
            final State initialState = State.PAUSED;
            setting = applicationSettingsService.newString(
                    KEY,
                    "Controls whether ReplayCommandExecution quartz job runs or pauses",
                    initialState.name());
        }
        return (ApplicationSettingForEstatio) setting;
    }


    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsService;
    @Inject
    MessageService messageService;

}
