package org.estatio.services.appsettings;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

@Hidden
public class EstatioSettingsService extends ApplicationSettingsServiceAbstract<EstatioSetting> {

    public EstatioSettingsService() {
        super(EstatioSetting.class);
    }

    /**
     * The 'beginning of time' so far as Estatio is concerned.
     * 
     * <p>
     * This is used, for example, by the <tt>InvoiceCalculationService</tt>; it doesn't
     * go looking for invoices prior to this date because they won't exist in the system.
     * 
     * <p>
     * One of the design principles for Estatio was to ensure that it would not require
     * invoices from the predecessor system.  
     */
    @MemberOrder(sequence = "1")
    public LocalDate fetchEpochDate() {
        return fetchSetting().getEpochDate();
    }

    public void updateEpochDate(LocalDate d) {
        fetchSetting().setEpochDate(d);
    }


}
