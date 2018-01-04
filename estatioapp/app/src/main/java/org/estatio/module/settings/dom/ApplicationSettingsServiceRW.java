package org.estatio.module.settings.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;

public interface ApplicationSettingsServiceRW extends ApplicationSettingsService {

    @MemberOrder(sequence="11")
    ApplicationSetting newBoolean(
            String name, String description, Boolean defaultValue);

    @MemberOrder(sequence="12")
    ApplicationSetting newString(
            String name, String description, String defaultValue);

    @MemberOrder(sequence="13")
    ApplicationSetting newLocalDate(
            String name, String description, LocalDate defaultValue);

    @MemberOrder(sequence="14")
    ApplicationSetting newInt(
            String name, String description, Integer defaultValue);

    @MemberOrder(sequence="15")
    ApplicationSetting newLong(
            String name, String description, Long defaultValue);
    
}
