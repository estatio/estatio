package org.estatio.services.appsettings;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.applib.services.settings.ApplicationSettingsService;

@Hidden
public abstract class EstatioSettingsService {

    /**
     * The 'beginning of time' so far as Estatio is concerned.
     * 
     * <p>
     * This is used, for example, by the <tt>InvoiceCalculationService</tt>; it
     * doesn't go looking for invoices prior to this date because they won't
     * exist in the system.
     * 
     * <p>
     * One of the design principles for Estatio was to ensure that it would not
     * require invoices from the predecessor system.
     */
    public final static String EPOCH_DATE_KEY = "epochDate";

    @MemberOrder(sequence = "1")
    public LocalDate fetchEpochDate() {
        final ApplicationSetting epochDate = applicationSettings.find(EPOCH_DATE_KEY);
        return epochDate != null ? epochDate.valueAsLocalDate() : null;
    }

    public abstract void updateEpochDate(LocalDate epochDate);

    // //////////////////////////////////////

    protected ApplicationSettingsService applicationSettings;

    public void setApplicationSettings(ApplicationSettingsService applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

}
